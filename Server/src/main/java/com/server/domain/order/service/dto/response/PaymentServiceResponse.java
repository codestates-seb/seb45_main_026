package com.server.domain.order.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class PaymentServiceResponse {

    private String mId;
    private String lastTransactionKey;
    private String paymentKey;
    private String orderId;
    private String orderName;
    private Integer taxExemptionAmount;
    private String status;
    private String requestedAt;
    private String approvedAt;
    private Boolean useEscrow;
    private Boolean cultureExpense;
    private PaymentCardResponse card;
    private String virtualAccount;
    private String transfer;
    private String mobilePhone;
    private String giftCertificate;
    private String cashReceipt;
    private String cashReceipts;
    private String discount;
    private String cancels;
    private String secret;
    private String type;
    private EasyPay easyPay;
    private String country;
    private String failure;
    private Boolean isPartialCancelable;
    private Receipt receipt;
    private Checkout checkout;
    private String currency;
    private Integer totalAmount;
    private Integer balanceAmount;
    private Integer suppliedAmount;
    private Integer vat;
    private Integer taxFreeAmount;
    private String method;
    private String version;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentCardResponse {

        private String issuerCode;
        private String acquirerCode;
        private String number;
        private Integer installmentPlanMonths;
        private Boolean isInterestFree;
        private String interestPayer;
        private String approveNo;
        private Boolean useCardPoint;
        private String cardType;
        private String ownerType;
        private String acquireStatus;
        private Integer amount;
    }

    @AllArgsConstructor
    @Getter
    @NoArgsConstructor
    public static class Receipt {
        private String url;
    }

    @AllArgsConstructor
    @Getter
    @NoArgsConstructor
    public static class Checkout {
        private String url;
    }

    @AllArgsConstructor
    @Getter
    @NoArgsConstructor
    public static class EasyPay {
        private String provider;
        private Integer amount;
        private Integer discountAmount;
    }
}
