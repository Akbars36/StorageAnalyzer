package com.vsu.amm;

/**
 * Created with IntelliJ IDEA.
 * User: vlzo0513
 * Date: 12/19/13
 * Time: 10:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class MasterWorkException extends Exception {
    public MasterWorkException(String message) {
        super(message);
    }

    public MasterWorkException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
