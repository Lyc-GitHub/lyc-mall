package com.lyc.learn.secKillOrder.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AlipayService {
    @Value("${alipay.app-id}")
    private String appId;
    @Value("${alipay.merchant-private-key}")
    private String merchantPrivateKey;
    @Value("${alipay.alipay-public-key}")
    private String alipayPublicKey;
    @Value("${alipay.gateway-url}")
    private String gatewayUrl;
    @Value("${alipay.notify-url}")
    private String notifyUrl;
    @Value("${alipay.return-url}")
    private String returnUrl;

    // 用于初始化支付宝客户端
    private AlipayClient getAlipayClient() {
        return new DefaultAlipayClient(
                gatewayUrl, appId, merchantPrivateKey, "json", "UTF-8",
                alipayPublicKey, "RSA2"
        );
    }

    // 创建支付订单，返回一个支付页面
    public String createTradePage(String outTradeNo, String totalAmount, String subject) throws AlipayApiException, URISyntaxException, UnsupportedEncodingException {
        AlipayClient alipayClient = getAlipayClient();
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        Map<String, Object> returnParams = new HashMap<String, Object>();
        returnParams.put("orderNo", outTradeNo);
        returnParams.put("refreshOrderStatus", 1);
        URI uri = new URI(returnUrl);
        StringBuilder newQuery = new StringBuilder();
        // 保留已有查询参数
        String existingQuery = uri.getQuery();
        if (existingQuery != null && !existingQuery.isEmpty()) {
            newQuery.append(existingQuery);
        }
        // 添加新参数
        for (Map.Entry<String, Object> entry : returnParams.entrySet()) {
            if (newQuery.length() > 0) {
                newQuery.append('&');
            }
            String key = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString());
            String value = URLEncoder.encode(String.valueOf(entry.getValue()), StandardCharsets.UTF_8.toString());
            newQuery.append(key).append('=').append(value);
        }

        // 构建新 URI，保留原方案、主机、端口、路径、片段（锚点），替换查询部分
        URI newUri = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(),
                newQuery.toString(), uri.getFragment());
        String returnUrl = newUri.toString();
        request.setReturnUrl(returnUrl);
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + outTradeNo + "\"," +
                "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"," +
                "\"total_amount\":\"" + totalAmount + "\"," +
                "\"subject\":\"" + subject + "\"" +
                "  }");
        try {
            // 调用SDK生成表单，会自动提交请求到支付宝
            String form = alipayClient.pageExecute(request).getBody();
            return form;
        } catch (AlipayApiException e) {
            log.error("支付宝支付失败", e);
            throw e;
        }
    }
    
    // 查询订单状态
    public String queryOrderStatus(String outTradeNo) throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        // 使用商户订单号发起查询
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + outTradeNo + "\"" +
                // 也可以在后续查询中使用支付宝返回的 trade_no，两者提供其一即可
                // "\"trade_no\":\"" + tradeNo + "\"" +
                "  }");
        AlipayTradeQueryResponse response = getAlipayClient().execute(request);
        // 查询成功，即可从response中获取订单状态等详细信息
        if (response.isSuccess()) {
            System.out.println("订单状态: " + response.getTradeStatus());
            // 其他字段: response.getTotalAmount(), response.getBuyerLogonId() 等
            return response.getTradeStatus();
        } else {
            // 查询失败，需要根据失败原因进行相应处理
            System.out.println("查询失败，错误码：" + response.getSubCode());
            return null;
        }
    }
}
