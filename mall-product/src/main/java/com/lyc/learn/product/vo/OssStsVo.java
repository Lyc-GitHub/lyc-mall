package com.lyc.learn.product.vo;

import lombok.Data;

@Data
public class OssStsVo {
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
    private String endpoint;
    private String bucket;
    private String dir;          // 上传的目标目录（以 / 结尾）
    private String expiration;
}
