package com.cuzz.webac.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class WeChatPaymentResponseDTO {

    @JsonProperty("mchid")
    private String mchid;

    @JsonProperty("appid")
    private String appid;

    @JsonProperty("out_trade_no")
    private String outTradeNo;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("trade_type")
    private String tradeType;

    @JsonProperty("trade_state")
    private String tradeState;

    @JsonProperty("trade_state_desc")
    private String tradeStateDesc;

    @JsonProperty("bank_type")
    private String bankType;

    @JsonProperty("attach")
    private String attach;

    @JsonProperty("success_time")
    private String successTime;

    @JsonProperty("payer")
    private Payer payer;

    @JsonProperty("amount")
    private Amount amount;

    // Getters and setters

    public static class Payer {
        @JsonProperty("openid")
        private String openid;

        // Getters and setters
    }

    public static class Amount {
        @JsonProperty("total")
        private int total;

        @JsonProperty("payer_total")
        private int payerTotal;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("payer_currency")
        private String payerCurrency;

        // Getters and setters
    }
}
