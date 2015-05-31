package com.vsu.amm.data.storage;

import com.vsu.amm.stat.ICounterSet;
import com.vsu.amm.stat.ICounterSet.OperationType;
import com.vsu.amm.stat.SimpleCounterSet;

import java.util.*;

/**
 * Created by Nikita Skornyakov on 30.05.2015.
 */
public class LinearAdaptiveStorage extends AbstractStorage {
    private IDataStorage activeStorage;
    private ICounterSet activeOperations = new SimpleCounterSet();
    private IDataStorage reserveStorage;
    private ICounterSet deferredOperations = new SimpleCounterSet();
    private double[] isrCoeffs; //коэффициенты для расчёта функции эффективности хранилища
    boolean isNegativeResult = false;
    private int calculationsStep; //как часто должны проводиться вычисления на эффективность хранилища
    private final Map<Character, Integer> operationHistory = new HashMap<>(); //история операций;
    int storedOperationsCount;
    private final List<Thread> deferredOperationsList = new ArrayList<>(); //список отложенных операций

    public LinearAdaptiveStorage() {
        operationHistory.put('I', 0);
        operationHistory.put('S', 0);
        operationHistory.put('R', 0);
    }

    private void checkFunc() {
        if (storedOperationsCount < calculationsStep)
            return;

        boolean swap = needToSwapStorage();
        //очищаем хранилице
        operationHistory.put('I', 0);
        operationHistory.put('S', 0);
        operationHistory.put('R', 0);
        storedOperationsCount = 0;

        if (!swap)
            return;

        //дожидаемся окончания выполнения всех отложенных операций
        for (Thread t : deferredOperationsList)
            if (t == null)
                continue;
            else
                synchronized (t) {
                    try {
                        t.join();
                    } catch (Exception ignored) {}
                }
        //очищаем список
        deferredOperationsList.clear();

        //меняем хранилища местами
        IDataStorage ref = activeStorage;
        activeStorage = reserveStorage;
        reserveStorage = ref;
        activeStorage.setCounterSet(activeOperations);
        reserveStorage.setCounterSet(deferredOperations);

    }

    /**
     * проверка на необходимость смены активного хранилища
     * @return {@code true} если хранилища необходимо поменять местами
     */
    private boolean needToSwapStorage() {
        //вычисляем линейную функцию
        double res = isrCoeffs[0] * operationHistory.get('I') +
                        isrCoeffs[1] * operationHistory.get('S') +
                        isrCoeffs[2] * operationHistory.get('R') +
                        isrCoeffs[3];
        //если результат вычислентия достаточно близок к нулю, то смена хранилищ не даст никакой выгоды
        if (Math.abs(res) < 1e-2)
            return false;

        if ((res < 0) ^ isNegativeResult) {
            isNegativeResult = !isNegativeResult;
            return true;
        }

        return false;

    }


    public IDataStorage getActiveStorage() {
        return activeStorage;
    }

    public void setActiveStorage(IDataStorage activeStorage) {
        this.activeStorage = activeStorage;
        activeStorage.setCounterSet(activeOperations);
    }

    public IDataStorage getReserveStorage() {
        return reserveStorage;
    }

    public void setReserveStorage(IDataStorage reserveStorage) {
        this.reserveStorage = reserveStorage;
        reserveStorage.setCounterSet(deferredOperations);
    }

    public double[] getIsrCoeffs() {
        return isrCoeffs;
    }

    public void setIsrCoeffs(double[] isrCoeffs) {
        this.isrCoeffs = isrCoeffs;
    }

    public int getCalculationsStep() {
        return calculationsStep;
    }

    public void setCalculationsStep(int calculationsStep) {
        this.calculationsStep = calculationsStep;
    }

    @Override
    public String getStorageName() {
        return "Linear Adaptive Storage. Active Storage: " + activeStorage.getStorageName() + ". Passive Storage: " +
                reserveStorage.getStorageName();
    }


    @Override
    public boolean get(int value) {
        //select проводим только по активному хранилищу
        operationHistory.put('S', operationHistory.get('S') + 1);
        storedOperationsCount++;
        boolean res = activeStorage.get(value);
        if (storedOperationsCount == calculationsStep)
            checkFunc();
        return res;
    }

    @Override
    public boolean set(int value) {
        operationHistory.put('I', operationHistory.get('I') + 1);
        storedOperationsCount++;
        boolean res = activeStorage.set(value);
        Thread t = null;
        if (res)
            t = new Thread() {
                @Override
                public void run() {
                    synchronized (reserveStorage) {
                        reserveStorage.uncheckedInsert(value);
                    }
                }
            };
        if (t != null) {
            deferredOperationsList.add(t);
            t.start();
        }

        if (storedOperationsCount == calculationsStep)
            checkFunc();
        return res;
    }

    @Override
    public boolean remove(int value) {
        operationHistory.put('R', operationHistory.get('R') + 1);
        storedOperationsCount++;
        boolean res = activeStorage.remove(value);
        Thread t = null;
        if (res)
            t = new Thread() {
                @Override
                public void run() {
                    synchronized (reserveStorage) {
                        reserveStorage.remove(value);
                    }
                }
            };
        if (t != null) {
            deferredOperationsList.add(t);
            t.start();
        }

        if (storedOperationsCount == calculationsStep)
            checkFunc();

        return res;
    }

    @Override
    public void uncheckedInsert(int value) {
        activeStorage.uncheckedInsert(value);
    }

    @Override
    public ICounterSet getCounterSet() {
        ICounterSet cs = new SimpleCounterSet();
        cs.inc(OperationType.ASSIGN, activeOperations.get(OperationType.ASSIGN));
        cs.inc(OperationType.COMPARE, activeOperations.get(OperationType.COMPARE));
        cs.inc(OperationType.CALCULATION, activeOperations.get(OperationType.CALCULATION));
        cs.inc(OperationType.DEFERRED_ASSIGN, deferredOperations.get(OperationType.ASSIGN));
        cs.inc(OperationType.DEFERRED_COMPARE, deferredOperations.get(OperationType.COMPARE));
        cs.inc(OperationType.DEFERRED_CALCULATION, deferredOperations.get(OperationType.CALCULATION));
        return cs;
    }
}
