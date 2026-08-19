package com.henrique.nookio_api.modules.schedules.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentDetailsInfo {


    private BigDecimal amount;
    private String currency;
    private PaymentType paymentType;
    private Integer installments;

    public PaymentDetailsInfo(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public PaymentDetailsInfo(Integer installments) {
        this.installments = installments;
        this.paymentType = PaymentType.CREDIT;
    }
}
