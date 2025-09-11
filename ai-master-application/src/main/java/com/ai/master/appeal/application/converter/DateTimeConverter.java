package com.ai.master.appeal.application.converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * 日期时间转换器
 */
public class DateTimeConverter {
    
    /**
     * 将Date转换为LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    
    /**
     * 将LocalDateTime转换为Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}