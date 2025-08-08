package com.ebang.openapi;

import com.ebang.openapi.channel.ChannelFactory;
import com.ebang.openapi.context.RequestContext;
import com.ebang.openapi.req.*;
import com.ebang.openapi.resp.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/ipo")
public class IpoController {

    private final ChannelFactory channelFactory;
    /**
     * 获取新股详细信息
     */
    @PostMapping("/ipo-info")
    public ResultResponse<Object> ipoInfo(@RequestBody @Validated IpoInfoReq request) throws Exception {
        log.info("ipoInfo request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).ipoInfo(request));
    }
    /**
     * 新股认购
     */
    @PostMapping("/apply-ipo")
    public ResultResponse<Object> applyIpo(@RequestBody @Validated ApplyIpoReq request) throws Exception {
        log.info("applyIpo request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).applyIpo(request));
    }
    /**
     * ipo改单/撤单
     */
    @PostMapping("/modify-ipo")
    public ResultResponse<Object> modifyIpo(@RequestBody @Validated ModifyIpoReq request) throws Exception {
        log.info("modifyIpo request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).modifyIpo(request));
    }

    /**
     *  获取客户ipo申购列表-分页查询
     */
    @PostMapping("/ipo-record-list")
    public ResultResponse<Object> ipoRecordList(@RequestBody @Validated IpoRecordListReq request) throws Exception {
        log.info("ipoRecordList request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).ipoRecordList(request));
    }

    /**
     * 获取客户ipo申购明细
     */
    @PostMapping("/ipo-record")
    public ResultResponse<Object> ipoRecord(@RequestBody @Validated IpoRecordReq request) throws Exception {
        log.info("ipoRecord request channel: {}", RequestContext.getChannel());
        return ResultResponse.success(channelFactory.getChannel(request).ipoRecord(request));
    }

}
