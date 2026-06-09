package com.lyc.learn.secKillOrder.vo;

import com.lyc.learn.common.vo.OrderInfoVo;
import com.lyc.learn.common.vo.ProductInfoVo;
import com.lyc.learn.common.vo.UserAddressInfoVo;
import lombok.Data;

@Data
public class PayOrderDetailVo {
    OrderInfoVo orderInfo;
    ProductInfoVo productInfo;
    UserAddressInfoVo userAddress;
}
