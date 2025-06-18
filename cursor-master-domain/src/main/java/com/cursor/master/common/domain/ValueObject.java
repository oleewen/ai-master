package com.cursor.master.common.domain;

/**
 * 值对象基类
 */
public abstract class ValueObject {
    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}
