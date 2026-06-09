package com.lyc.learn.common.vo;

import lombok.Data;

@Data
public class UserAddressInfoVo {
    // 标识
    private Long id;
    // 收货人姓名
    private String receiverName;
    // 收货人手机号
    private String receiverPhone;
    // 省份
    private String province;
    // 城市
    private String city;
    // 区县
    private String district;
    // 详细地址
    private String detailAddress;
    // 邮政编码
    private String postalCode;
    /**
     * 是否默认地址：0-否，1-是
     */
    private Integer isDefault;
    /**
     * 地址标签（家、公司等）
     */
    private String tag;
}
