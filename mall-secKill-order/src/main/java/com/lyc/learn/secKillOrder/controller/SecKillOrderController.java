package com.lyc.learn.secKillOrder.controller;

import com.lyc.learn.secKillOrder.vo.OrderListVo;
import com.lyc.learn.secKillOrder.vo.PayOrderDetailVo;
import com.lyc.learn.secKillOrder.vo.ToPayVo;
import com.lyc.learn.secKillOrder.service.SecKillOrderService;
import com.lyc.learn.common.vo.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/order")
public class SecKillOrderController {
    
    @Autowired
    SecKillOrderService secKillOrderService;

    // 获取秒杀订单待支付信息
    @GetMapping("/getSeckillOrderPayInfo/{orderNo}")
    public CommonResult getSeckillOrderPayInfo(@PathVariable String orderNo) {
        PayOrderDetailVo vo = secKillOrderService.getSeckillOrderPayInfo(orderNo);
        return CommonResult.success(vo);
    }
    
    // 获取支付表单
    @PostMapping("/getPayForm/{orderNo}")
    public CommonResult getPayForm(@PathVariable String orderNo, @RequestBody ToPayVo vo, HttpServletResponse httpResponse) throws IOException {
        String form = secKillOrderService.getPayForm(orderNo, vo);
        return CommonResult.success(form);
    }
    
    // 更新订单支付信息
    @PostMapping("/updateOrderPayInfo/{orderNo}")
    public CommonResult updateOrderPayInfo(@PathVariable String orderNo) {
        secKillOrderService.updateOrderPayInfo(orderNo);
        return CommonResult.success();
    }
    
    // 获取订单详情
    @GetMapping("/getSeckillOrderDetail/{orderNo}")
    public CommonResult getSeckillOrderDetail(@PathVariable String orderNo) {
        PayOrderDetailVo res = secKillOrderService.getSeckillOrderDetail(orderNo);
         return CommonResult.success(res);
    }
    
    @GetMapping("/queryOrderList")
    public CommonResult queryOrderList() {
        List<OrderListVo> list = secKillOrderService.queryOrderList();
        return CommonResult.success(list);
    }
}
