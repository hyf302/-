package com.scenic.common;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    private Map<String, Object> extra;

    public Result() {
        this.extra = new HashMap<>();
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public Result<T> addExtra(String key, Object value) {
        this.extra.put(key, value);
        return this;
    }
} 