package com.lyc.learn.product.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.lyc.learn.auth.sdk.utils.UserContextUtil;
import com.lyc.learn.common.exception.MallException;
import com.lyc.learn.product.vo.OssStsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class OssClientService {
    
    Logger logger = LoggerFactory.getLogger(OssClientService.class);
    
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.bucket}")
    private String bucket;

    @Value("${aliyun.oss.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.role-arn}")
    private String roleArn;

    @Value("${aliyun.oss.sts-endpoint}")
    private String stsEndpoint;

    @Value("${aliyun.oss.upload-dir-prefix}")
    private String uploadDirPrefix;

    @Value("${aliyun.oss.expire-seconds}")
    private Long expireSeconds;
    
    @Autowired
    OSS ossClient;


    /**
     * 将URL授权临时读取
     */
    public String generatePresignedUrl(String url) throws URISyntaxException {
        Date expiration = new Date(System.currentTimeMillis() + expireSeconds * 1000);
        URI uri = new URI(url);
        String path = uri.getPath(); // 例如 /images/photo.jpg
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, path);
        request.setExpiration(expiration);
        
        URL targetUrl = ossClient.generatePresignedUrl(request);
        return targetUrl.toString();
    }

    /**
     * 获取上传OSS必要的信息
     * @return
     */
    public OssStsVo getSts() {
        try {
            // 1. 构造 AssumeRole 请求
            DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultAcsClient client = new DefaultAcsClient(profile);
            AssumeRoleRequest request = new AssumeRoleRequest();
            request.setSysEndpoint(stsEndpoint);
            request.setRoleArn(roleArn);
            request.setRoleSessionName("session-" + System.currentTimeMillis());
            request.setDurationSeconds(expireSeconds);

            // 可选：设置权限策略（Policy）进一步限制上传路径、文件大小等
            // 例如：只允许上传到 images/${userId}/ 目录
            String userId = UserContextUtil.getUserId() + ""; // 当前登录用户ID，从SecurityContext获取
            String policy = "{\n" +
                    "  \"Statement\": [{\n" +
                    "    \"Effect\": \"Allow\",\n" +
                    "    \"Action\": [\n" +
                    "      \"oss:PutObject\",\n" +
                    "      \"oss:GetObject\"\n" +
                    "    ],\n" +
                    "    \"Resource\": [\n" +
                    "      \"acs:oss:*:*:" + bucket + "/" + uploadDirPrefix + "/" + userId + "/*\"\n" +
                    "    ]\n" +
                    "  }],\n" +
                    "  \"Version\": \"1\"\n" +
                    "}";
            request.setPolicy(policy);

            // 2. 发起请求
            AssumeRoleResponse response = client.getAcsResponse(request);
            AssumeRoleResponse.Credentials credentials = response.getCredentials();

            // 3. 返回前端需要的信息
            OssStsVo vo = new OssStsVo();
            vo.setAccessKeyId(credentials.getAccessKeyId());
            vo.setAccessKeySecret(credentials.getAccessKeySecret());
            vo.setSecurityToken(credentials.getSecurityToken());
            vo.setEndpoint(endpoint);
            vo.setBucket(bucket);
            vo.setDir(uploadDirPrefix + "/" + userId + "/"); // 例如 images/1001/
            // 将UTC时间转换为东八区时间
            String utcExpiration = credentials.getExpiration();
            ZonedDateTime utcTime = ZonedDateTime.parse(utcExpiration);
            ZonedDateTime chinaTime = utcTime.withZoneSameInstant(ZoneId.of("Asia/Shanghai"));
            String formattedExpiration = chinaTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            vo.setExpiration(formattedExpiration);
            return vo;
        } catch (Exception e) {
            logger.error("获取上传凭证失败", e);
            throw new MallException("获取上传凭证失败！");
        }
    }
}
