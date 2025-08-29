package com.ai.master.appeal.domain.valueobject;

import lombok.Value;

import java.util.UUID;

/**
 * 申诉项ID值对象
 */
@Value
public class AppealItemId {
    private final Long value;
    
    public static AppealItemId of(Long value) {
        return new AppealItemId(value);
    }
    
    public static AppealItemId generate() {
        return new AppealItemId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
    }
}