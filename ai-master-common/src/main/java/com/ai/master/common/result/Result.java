package com.ai.master.common.result;

import lombok.Data;

/**
 * 统一返回结果
 */
@Data
public class Result<T> {
    
    private boolean success;
    private String code;
    private String message;
    private T data;
    
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode("SUCCESS");
        result.setMessage("操作成功");
        return result;
    }
    
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode("SUCCESS");
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }
    
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode("FAIL");
        result.setMessage(message);
        return result;
    }
    
    public static <T> Result<T> fail(String code, String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}