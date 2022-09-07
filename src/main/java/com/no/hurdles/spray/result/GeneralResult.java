package com.no.hurdles.spray.result;

/**
 * 只带有一个属性的VO
 **/
public class GeneralResult<T> {
    
    private T value;

    private GeneralResult(T value) {
        this.value = value;
    }

    public static GeneralResult ok(Object value) {
        return new GeneralResult(value);
    }

    public T getValue() {
        return value;
    }
}