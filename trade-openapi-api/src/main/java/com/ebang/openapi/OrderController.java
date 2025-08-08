package com.ebang.openapi;

import com.ebang.openapi.channel.ChannelFactory;
import com.ebang.openapi.req.*;
import com.ebang.openapi.resp.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final ChannelFactory channelFactory;
    /**
     * 查询订单详情
     */
    @PostMapping("/detail")
    public ResultResponse<Object> getOrderInfo(@RequestBody OrderInfoQueryReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).getOrderInfo(request));
    }
    /**
     * 查询订单列表
     */
    @PostMapping("/list")
    public ResultResponse<Object> getOrderList(@RequestBody OrderListQueryReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).getOrderList(request));
    }
    /**
     * 创建订单
     */
    @PostMapping("/create")
    public ResultResponse<Object> createOrder(@RequestBody CreateOrderReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).createOrder(request));
    }
    /**
     * 编辑订单
     */
    @PostMapping("/modify")
    public ResultResponse<Object> editOrder(@RequestBody ModifyOrderReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).modifyOrder(request));
    }
    /**
     * 取消订单
     */
    @PostMapping("/cancel")
    public ResultResponse<Object> cancelOrder(@RequestBody CancelOrderReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).cancelOrder(request));
    }
    /**
     * 预览订单影响
     */
    @PostMapping("/whatif")
    public ResultResponse<Object> whatifOrder(@RequestBody WhatifOrderReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).whatifOrder(request));
    }
    /**
     * 订单确认提交
     */
    @PostMapping("/reply")
    public ResultResponse<Object> replyOrder(@RequestBody ReplyOrderReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).replyOrder(request));
    }
    /**
     * 查询订单费用
     */
    @PostMapping("/fees")
    public ResultResponse<Object> fees(@RequestBody ReplyOrderReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).replyOrder(request));
    }
}