package com.ai.master.appeal.domain.valueobject;

import lombok.Value;

import java.util.UUID;

/**
 * 申诉单ID值对象
 */
@Value
public class AppealId {
    private final Long value;
    
    public static AppealId of(Long value) {
        return new AppealId(value);
    }
    
    public static AppealId generate() {
        return new AppealId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
    }
}