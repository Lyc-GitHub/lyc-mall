package com.lyc.learn.secKillOrder.service;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lyc.learn.auth.sdk.utils.UserContextUtil;
import com.lyc.learn.common.exception.MallException;
import com.lyc.learn.secKillOrder.client.ProductClient;
import com.lyc.learn.common.dto.SecKillProductVo;
import com.lyc.learn.common.utils.JsonUtil;
import com.lyc.learn.common.vo.OrderInfoVo;
import com.lyc.learn.common.vo.ProductInfoVo;
import com.lyc.learn.common.vo.UserAddressInfoVo;
import com.lyc.learn.secKillOrder.entity.SeckillOrder;
import com.lyc.learn.secKillOrder.entity.UserAddress;
import com.lyc.learn.secKillOrder.vo.OrderListVo;
import com.lyc.learn.secKillOrder.vo.PayOrderDetailVo;
import com.lyc.learn.common.enums.OrderStatusEnums;
import com.lyc.learn.secKillOrder.mapper.SeckillOrderMapper;
import com.lyc.learn.secKillOrder.mapper.UserAddressMapper;
import com.lyc.learn.secKillOrder.vo.ToPayVo;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SecKillOrderService {
    
    @Autowired
    SeckillOrderMapper seckillOrderMapper;
    
    @Autowired
    ProductClient productClient;
    
    @Autowired
    RedissonClient redissonClient;
    
    @Autowired
    UserAddressMapper userAddressMapper;

    @Autowired
    AlipayService alipayService;

    // 获取秒杀订单待支付信息
    public PayOrderDetailVo getSeckillOrderPayInfo(String orderNo) {
        PayOrderDetailVo vo = new PayOrderDetailVo();
        // 订单信息
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new LambdaQueryWrapper<SeckillOrder>().eq(SeckillOrder::getOrderNo, orderNo));
        OrderInfoVo orderInfo = JsonUtil.obj2obj(seckillOrder, OrderInfoVo.class);
        orderInfo.setStatusStr(OrderStatusEnums.convertOrderStatus(seckillOrder.getStatus()).getDesc());
        vo.setOrderInfo(orderInfo);

        // 商品信息
        SecKillProductVo product = productClient.getProductInfo(seckillOrder.getProductId());
        ProductInfoVo productInfo = JsonUtil.obj2obj(product, ProductInfoVo.class);
        // 商品图片
        productInfo.setProductImage(product.getMainImgUrl());
        vo.setProductInfo(productInfo);

        // 收货地址信息
        Long userId = UserContextUtil.getUserId();
        UserAddress userAddress = userAddressMapper.selectOne(new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, 1));
        vo.setUserAddress(JsonUtil.obj2obj(userAddress, UserAddressInfoVo.class));
        return vo;
    }
    
    // 更新订单信息，并返回支付表单
    public String getPayForm(String orderNo, ToPayVo vo) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new LambdaQueryWrapper<SeckillOrder>().eq(SeckillOrder::getOrderNo, orderNo));
        // 运费
        seckillOrder.setShippingFee(vo.getShippingFee());
        // 更新支付金额
        seckillOrder.setPayAmount(seckillOrder.getProductAmount().add(seckillOrder.getShippingFee()));
        // 地址
        seckillOrder.setAddressId(vo.getAddressId());
        // 支付方式
        seckillOrder.setPaymentChannel(vo.getPaymentMethod());
        seckillOrderMapper.updateById(seckillOrder);
        
        // 创建支付表单
        try {
            return alipayService.createTradePage(seckillOrder.getOrderNo(), seckillOrder.getPayAmount().toPlainString(), vo.getProductName());
        } catch (AlipayApiException | URISyntaxException | UnsupportedEncodingException e) {
            throw new MallException(e.getMessage());
        }
    }

    // 更新订单支付状态
    public void updateOrderPayInfo(String orderNo) {
        try {
            String orderState = alipayService.queryOrderStatus(orderNo);
            if ("TRADE_SUCCESS".equals(orderState)) {
                SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new LambdaQueryWrapper<SeckillOrder>().eq(SeckillOrder::getOrderNo, orderNo));
                seckillOrder.setStatus(OrderStatusEnums.CPMPLETE.getCode());
                seckillOrder.setPayTime(LocalDateTime.now());
                seckillOrderMapper.updateById(seckillOrder);
            }
        } catch (AlipayApiException e) {
            throw new MallException(e.getMessage());
        }
    }

    public PayOrderDetailVo getSeckillOrderDetail(String orderNo) {
        PayOrderDetailVo vo = new PayOrderDetailVo();
        // 订单信息
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new LambdaQueryWrapper<SeckillOrder>().eq(SeckillOrder::getOrderNo, orderNo));
        OrderInfoVo orderInfo = JsonUtil.obj2obj(seckillOrder, OrderInfoVo.class);
        orderInfo.setStatusStr(OrderStatusEnums.convertOrderStatus(seckillOrder.getStatus()).getDesc());
        vo.setOrderInfo(orderInfo);

        // 商品信息
        SecKillProductVo product = productClient.getProductInfo(seckillOrder.getProductId());
        ProductInfoVo productInfo = JsonUtil.obj2obj(product, ProductInfoVo.class);
        // 商品图片
        productInfo.setProductImage(product.getMainImgUrl());
        vo.setProductInfo(productInfo);

        UserAddress userAddress;
        if (OrderStatusEnums.CPMPLETE.getCode() == seckillOrder.getStatus()) {
            Long addressId = seckillOrder.getAddressId();
            userAddress = userAddressMapper.selectById(addressId);
            // 物流信息等....
        } else {
            // 收货地址信息
            Long userId = UserContextUtil.getUserId();
            userAddress = userAddressMapper.selectOne(new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId, userId)
                    .eq(UserAddress::getIsDefault, 1));
        }

        vo.setUserAddress(JsonUtil.obj2obj(userAddress, UserAddressInfoVo.class));
        return vo;
    }

    public List<OrderListVo> queryOrderList() {
        // 获取该用户的所有订单
        Long userId = UserContextUtil.getUserId();
        List<SeckillOrder> seckillOrderList = seckillOrderMapper.selectList(new LambdaQueryWrapper<SeckillOrder>().eq(SeckillOrder::getUserId, userId));
        List<OrderListVo> orderListVos = new ArrayList<>();
        for (SeckillOrder seckillOrder : seckillOrderList) {
            OrderListVo orderListVo = JsonUtil.obj2obj(seckillOrder, OrderListVo.class);
            // 用户名
            String userName = UserContextUtil.getCurrentUser().getRealName();
            orderListVo.setUserName(userName);
            // 商品信息
            SecKillProductVo product = productClient.getProductInfo(seckillOrder.getProductId());
            orderListVo.setProductName(product.getProductName());
            // 状态转换
            orderListVo.setStatusStr(OrderStatusEnums.convertOrderStatus(seckillOrder.getStatus()).getDesc());
            orderListVo.setStatusVal(OrderListVo.convertStatus(seckillOrder.getStatus()));
            orderListVos.add(orderListVo);
        }
        return orderListVos;
    }
}
