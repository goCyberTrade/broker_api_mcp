package com.ebang.openapi.resp;

import lombok.Data;

/**
 * @author chenlanqing 2025/7/4 11:22
 * @version 1.0.0
 */
@Data
public class ResultResponse<T> {

    private T data;

    private long code;

    private String msg;

    public static <T> ResultResponse<T> success(T data) {
        ResultResponse<T> result = new ResultResponse<>();
        result.setCode(0);
        result.setData(data);
        result.setMsg("success");
        return result;
    }

    public static <T> ResultResponse<T> success(T data, long code) {
        ResultResponse<T> result = new ResultResponse<>();
        result.setCode(code);
        result.setData(data);
        result.setMsg("success");
        return result;
    }

    public static <T> ResultResponse<T> fail(long code, String msg) {
        ResultResponse<T> result = new ResultResponse<>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
