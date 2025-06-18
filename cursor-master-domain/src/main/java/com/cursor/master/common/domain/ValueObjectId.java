package com.cursor.master.common.domain;

/**
 * 值对象标识
 *
 * @author only
 * @since 2020-05-22
 */
public interface ValueObjectId<T> {
    /**
     * 取值对象的值
     *
     * @return 值对象值
     */
    T id();
} 