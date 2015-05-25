package com.vsu.amm.visualization.classification;

import java.util.ArrayList;
import java.util.List;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

/**
 * Класс для бинарной классификации методом SVM
 * 
 * @author Potapov Danila
 *
 */
public class Classificator {

	/**
	 * Метод линейной классификации
	 * 
	 * @param classificationPoints
	 *            точки по которым идет классификация
	 * @return коэффиценты разделяющей линии ax+by+c=0
	 */
	public static double[] classification(List<Point> classificationPoints) {
		// Создаем параметры
		svm_parameter param = new svm_parameter();
		svm.svm_set_print_string_function(new libsvm.svm_print_interface() {
			@Override
			public void print(String s) {
			} // Disables svm output
		});
		// default values
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.LINEAR;
		param.degree = 3;
		param.gamma = 0;
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 0;
		param.C = 5;
		param.eps = 1e-2;
		param.p = 0.1;
		param.shrinking = 0;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];

		// Создаем проблему
		svm_problem problem = new svm_problem();

		problem.l = classificationPoints.size(); // number of training examples
		problem.y = new double[problem.l];
		problem.x = new svm_node[problem.l][2];
		int i = 0;
		// Добавляем к проблеме точки (param.x) и значения их классов(0 и 1)
		List<Integer> vals = new ArrayList<>();
		for (Point point : classificationPoints) {
			problem.x[i][0] = new svm_node();
			problem.x[i][0].index = 1;
			problem.x[i][0].value = point.getX();
			problem.x[i][1] = new svm_node();
			problem.x[i][1].index = 2;
			problem.x[i][1].value = point.getY();
			problem.y[i] = point.value;
			vals.add(point.value);
			i++;
		}

		// создаем модель, содержащую решение(на основе настроек и проблемы)
		svm_model model = svm.svm_train(problem, param);
		double[] w = new double[3];
		for (i = 0; i < model.SV[0].length; i++) {
			for (int j = 0; j < model.SV.length; j++) {
				w[i] += model.SV[j][i].value * model.sv_coef[0][j];
			}
		}
		w[2] = -model.rho[0];
		// нормализуем решение
		double norma = Math.sqrt(w[0] * w[0] + w[1] * w[1] + w[2] * w[2]);
		if (model.label[1] == 0) {
			w[0] = -w[0];
			w[1] = -w[1];
			w[2] = -w[2];
		}
		w[0] /= norma;
		w[1] /= norma;
		w[2] /= norma;
		System.out.println(w[0] + "         " + w[1] + "          "
				+ (-model.rho[0]));
		// do_cross_validation(problem,param);
		return w;

	}

	/**
	 * Метод для проверки и поиска точности
	 * 
	 * @param prob
	 * @param param
	 */
	@SuppressWarnings("unused")
	private static void do_cross_validation(svm_problem prob,
			svm_parameter param) {
		int i;
		int total_correct = 0;
		double total_error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
		double[] target = new double[prob.l];

		svm.svm_cross_validation(prob, param, 2, target);
		if (param.svm_type == svm_parameter.EPSILON_SVR
				|| param.svm_type == svm_parameter.NU_SVR) {
			for (i = 0; i < prob.l; i++) {
				double y = prob.y[i];
				double v = target[i];
				total_error += (v - y) * (v - y);
				sumv += v;
				sumy += y;
				sumvv += v * v;
				sumyy += y * y;
				sumvy += v * y;
			}
			System.out.print("Cross Validation Mean squared error = "
					+ total_error / prob.l + "\n");
			System.out
					.print("Cross Validation Squared correlation coefficient = "
							+ ((prob.l * sumvy - sumv * sumy) * (prob.l * sumvy - sumv
									* sumy))
							/ ((prob.l * sumvv - sumv * sumv) * (prob.l * sumyy - sumy
									* sumy)) + "\n");
		} else
			for (i = 0; i < prob.l; i++)
				if (target[i] == prob.y[i])
					++total_correct;
		System.out.print("Cross Validation Accuracy = " + 100.0 * total_correct
				/ prob.l + "%\n");
	}
}
