package com.ai.master.user.domain.model;

import com.ai.master.common.domain.Id;

/**
 * 用户ID值对象
 */
public class UserId extends Id {
    public UserId(Long value) {
        super(value);
    }

    public static UserId of(Long value) {
        return new UserId(value);
    }
}
