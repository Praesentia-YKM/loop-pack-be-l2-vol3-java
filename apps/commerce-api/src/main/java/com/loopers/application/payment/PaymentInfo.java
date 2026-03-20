package com.loopers.application.payment;

import com.loopers.domain.payment.PaymentModel;

import java.time.ZonedDateTime;

public record PaymentInfo(
    Long paymentId,
    Long orderId,
    Long userId,
    String cardType,
    String maskedCardNo,
    int amount,
    String status,
    String transactionKey,
    String failureReason,
    ZonedDateTime createdAt
) {
    public static PaymentInfo from(PaymentModel payment) {
        return new PaymentInfo(
            payment.getId(),
            payment.orderId(),
            payment.userId(),
            payment.cardType().name(),
            payment.maskedCardNo(),
            payment.amount().value(),
            payment.status().name(),
            payment.transactionKey(),
            payment.failureReason(),
            payment.getCreatedAt()
        );
    }
}
