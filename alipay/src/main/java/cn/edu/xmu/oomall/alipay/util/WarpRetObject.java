package cn.edu.xmu.oomall.alipay.util;

import cn.edu.xmu.oomall.alipay.model.vo.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xucangbai
 * 最上层的包装对象
 * 当某一字段空时，不写入
 *
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarpRetObject {
    @JsonProperty("alipay_trade_wap_pay_response")
    private PayRetVo payRetVo;

    @JsonProperty("alipay_trade_query_response")
    private PayQueryRetVo payQueryRetVo;

    @JsonProperty("alipay_trade_close_response")
    private CloseRetVo closeRetVo;

    @JsonProperty("alipay_trade_refund_response")
    private RefundRetVo refundRetVo;

    @JsonProperty("alipay_trade_fastpay_refund_query_response")
    private RefundQueryRetVo refundQueryRetVo;

    @JsonProperty("alipay_data_dataservice_bill_downloadurl_query_response")
    private DownloadUrlQueryRetVo downloadUrlQueryRetVo;

    /**
     * 固定:ERITJKEIJKJHKKKKKKKHJEREEEEEEEEEEE
     */
    private String sign="ERITJKEIJKJHKKKKKKKHJEREEEEEEEEEEE";

    public void setPayRetVo(PayRetVo payRetVo) {
        this.payRetVo = payRetVo;
    }

    public void setPayQueryRetVo(PayQueryRetVo payQueryRetVo) {
        this.payQueryRetVo = payQueryRetVo;
    }

    public void setCloseRetVo(CloseRetVo closeRetVo) {
        this.closeRetVo = closeRetVo;
    }

    public void setRefundRetVo(RefundRetVo refundRetVo) {
        this.refundRetVo = refundRetVo;
    }

    public void setRefundQueryRetVo(RefundQueryRetVo refundQueryRetVo) {
        this.refundQueryRetVo = refundQueryRetVo;
    }

    public void setDownloadUrlQueryRetVo(DownloadUrlQueryRetVo downloadUrlQueryRetVo) {
        this.downloadUrlQueryRetVo = downloadUrlQueryRetVo;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
