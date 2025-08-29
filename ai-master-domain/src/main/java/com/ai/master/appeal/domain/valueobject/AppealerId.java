package com.ai.master.appeal.domain.valueobject;

import lombok.Value;

import java.util.UUID;

/**
 * 申诉方ID值对象
 */
@Value
public class AppealerId {
    private final Long value;
    
    public static AppealerId of(Long value) {
        return new AppealerId(value);
    }
    
    public static AppealerId generate() {
        return new AppealerId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
    }
}