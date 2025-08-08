package com.ebang.openapi;

import com.ebang.openapi.channel.ChannelFactory;
import com.ebang.openapi.req.*;
import com.ebang.openapi.resp.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/position")
public class PositionController {

    private final ChannelFactory channelFactory;

    @PostMapping("/get-portfolio-accounts")
    public ResultResponse<Object> getPortfolioAccounts(@RequestBody GetPortfolioAccountsReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).getPortfolioAccounts(request));
    }
    @PostMapping("/get-position-info")
    public ResultResponse<Object> getPositionInfo(@RequestBody GetPositionInfo request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).getPositionInfo(request));
    }
    @PostMapping("/get-sub-accounts")
    public ResultResponse<Object> getSubAccounts(@RequestBody GetSubAccounts request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).getSubAccounts(request));
    }

    @PostMapping("/get-all-position")
    public ResultResponse<Object> getAllPosition(@RequestBody PositionListQueryReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).getAllPosition(request));
    }

    @PostMapping("/get-portfolio-account-summary")
    public ResultResponse<Object> portfolioAccountSummary(@RequestBody AccountSummaryReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).portfolioAccountSummary(request));
    }

    @PostMapping("/get-portfolio-allocation")
    public ResultResponse<Object> getPortfolioAllocation(@RequestBody PortfolioAllocationReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).getPortfolioAllocation(request));
    }

    @PostMapping("/get-account-ledger")
    public ResultResponse<Object> getAccountLedger(@RequestBody AccountLedgerReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).getAccountLedger(request));
    }

    @PostMapping("/get-account-attributes")
    public ResultResponse<Object> getAccountAttribute(@RequestBody AccountAttributesReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).getAccountAttributes(request));
    }

    @PostMapping("/get-instrument-position")
    public ResultResponse<Object> getInstrumentPosition(@RequestBody InstrumentPositionReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).getInstrumentPosition(request));
    }

    @PostMapping("/get-account-performance")
    public ResultResponse<Object> getAccountPerformance(@RequestBody AccountPerformanceReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).getAccountPerformance(request));
    }

    @PostMapping("/get-period-account-performance")
    public ResultResponse<Object> getPeriodAccountPerformance(@RequestBody PeriodAccountPerformanceReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).getPeriodAccountPerformance(request));
    }

    @PostMapping("/get-history-transaction")
    public ResultResponse<Object> getHistoryTransaction(@RequestBody HistoryTransactionReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).getHistoryTransaction(request));
    }

    @PostMapping("/get-today-transaction")
    public ResultResponse<Object> getTodayTransaction(@RequestBody TodayTransactionReq request) throws Exception {
        return ResultResponse.success(channelFactory.getChannel(request).getTodayTransaction(request));
    }

}
