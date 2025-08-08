package com.ebang.openapi.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PositionListQueryReq extends BaseRequest {

    /**
     * stock: 股票、funds: 基金
     */
    private String productType;
    /**
     * 股票代码或基金代码，多个用英文逗号分隔，不填则查询所有
     * <p>
     * 长桥证券：
     *  <li>股票代码，使用 ticker.region 格式，例如：AAPL.US
     *  <li>基金代码，使用 <a href="https://en.wikipedia.org/wiki/International_Securities_Identification_Number">ISIN 格式</a>，例如：HK0000676327
     *
     * 老虎证券:
     *  <li>股票代码，如：600884 / SNAP
     */
    private String symbol;

    /**
     * 老虎证券：账号 id
     */
    private String accountId;

    /**
     * 老虎证券：市场分类，包括：ALL/US/HK/CN 默认 ALL
     */
    private String market;

    /**
     * 华盛通证券：'K'-港股、'P'-美股、'v'-深股通、't'-沪股通
     * <p>
     * 盈立证券：交易类别(0-香港,5-美股, 67-A股，100-查询所有交易类别)
     */
    private String exchangeType;

    private Integer pageSize;
    /**
     * 微牛证券：上一页最后一条记录的 instrumentId，首次请求时为空
     */
    private String lastInstrumentId;

    /**
     * IB：分页返回Position信息。从 0 开始索引。每页最多返回 100 个Position。默认值为 0
     */
    private Integer pageId;

    /**
     * 富途证券：1-香港市场、2-美国市场、3-A股市场（仅模拟交易）、5-期货市场（环球期货）、113-香港基金市场、123-美国基金市场
     */
    private Integer trdMarket;

    /**
     * 富途证券：持仓ID，多个逗号分割
     */
    private String positionId;

    /**
     * 长桥证券：开始时间，yyyy-MM-dd HH:mm:ss。开始时间为空时，默认为结束时间或当前时间前九十天
     * <p>
     * 老虎证券：起始时间（yyyy-MM-dd HH-mm-ss）
     * <p>
     * 华盛通证券：起始日期	格式为：yyyyMMdd
     * <p>
     * 富途证券：起始时间，严格按照 yyyy-MM-dd HH:mm:ss
     */
    private String startDate;

    /**
     * 长桥证券：结束时间，yyyy-MM-dd HH:mm:ss。结束时间为空时，默认为开始时间后九十天或当前时间。
     * <p>
     * 老虎证券：截止时间（yyyy-MM-dd HH-mm-ss格式需要转换为毫秒的时间戳）
     * <p>
     * 华盛通证券：结束日期	格式为：yyyyMMdd
     * <p>
     * 富途证券：起始时间，严格按照 yyyy-MM-dd HH:mm:ss
     */
    private String endDate;
}
