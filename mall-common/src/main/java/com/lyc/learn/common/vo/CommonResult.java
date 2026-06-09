package com.lyc.learn.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResult {
    private int code;
    private String msg;
    private Object data;

    public static CommonResult success() {
        return success("操作成功");
    }

    public static CommonResult success(Object data) {
        return success("操作成功", data);
    }

    public static CommonResult success(String msg) {
        return success(msg, (Object)null);
    }

    public static CommonResult success(String msg, Object data) {
        return new CommonResult(200, msg, data);
    }

    public static CommonResult error() {
        return error("操作失败");
    }

    public static CommonResult error(String msg) {
        return error(msg, (Object)null);
    }

    public static CommonResult error(String msg, Object data) {
        return new CommonResult(500, msg, data);
    }

    public static CommonResult error(int code, String msg) {
        return new CommonResult(code, msg, (Object)null);
    }
}
