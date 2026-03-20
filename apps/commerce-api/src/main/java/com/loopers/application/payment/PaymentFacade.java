package com.loopers.application.payment;

import com.loopers.application.order.OrderService;
import com.loopers.config.PgProperties;
import com.loopers.domain.order.OrderModel;
import com.loopers.domain.payment.PaymentModel;
import com.loopers.infrastructure.payment.PgClient;
import com.loopers.infrastructure.payment.dto.PgPaymentRequest;
import com.loopers.infrastructure.payment.dto.PgPaymentResponse;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class PaymentFacade {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final PgClient pgClient;
    private final PgProperties pgProperties;

    @Transactional
    public PaymentInfo requestPayment(Long userId, PaymentCommand command) {
        // 주문 검증 + 상태 변경 + 결제 레코드 생성
        OrderModel order = orderService.getOrder(command.orderId(), userId);
        order.startPayment();

        PaymentModel payment = new PaymentModel(
            order.getId(),
            userId,
            command.cardType(),
            command.maskedCardNo(),
            order.finalAmount()
        );
        payment = paymentService.save(payment);

        // PG 호출
        PgPaymentResponse pgResponse = callPg(userId, command, payment);

        // transactionKey 업데이트
        payment.assignTransactionKey(pgResponse.transactionKey());

        return PaymentInfo.from(payment);
    }

    private PgPaymentResponse callPg(Long userId, PaymentCommand command, PaymentModel payment) {
        PgPaymentRequest pgRequest = new PgPaymentRequest(
            String.valueOf(command.orderId()),
            command.cardType().name(),
            command.cardNo(),
            String.valueOf(payment.amount().value()),
            pgProperties.callbackUrl()
        );

        try {
            PgPaymentResponse pgResponse = pgClient.requestPayment(pgRequest, String.valueOf(userId));
            if (pgResponse == null || pgResponse.transactionKey() == null) {
                throw new CoreException(ErrorType.PG_REQUEST_FAILED, "PG 응답이 유효하지 않습니다.");
            }
            return pgResponse;
        } catch (CoreException e) {
            throw e;
        } catch (Exception e) {
            throw new CoreException(ErrorType.PG_REQUEST_FAILED, "PG 결제 요청에 실패했습니다: " + e.getMessage());
        }
    }

    @Transactional
    public void handleCallback(String transactionKey, String status, String failureReason) {
        PaymentModel payment = paymentService.getByTransactionKey(transactionKey);
        OrderModel order = orderService.getOrderForAdmin(payment.orderId());

        if ("SUCCESS".equals(status)) {
            payment.markSuccess();
            order.confirmPayment();
        } else if ("FAILED".equals(status)) {
            payment.markFailed(failureReason);
            order.failPayment();
        }
    }

    @Transactional(readOnly = true)
    public PaymentInfo getPayment(Long paymentId) {
        return PaymentInfo.from(paymentService.getById(paymentId));
    }

    @Transactional(readOnly = true)
    public List<PaymentInfo> getPaymentsByOrderId(Long orderId) {
        return paymentService.getByOrderId(orderId).stream()
            .map(PaymentInfo::from)
            .toList();
    }
}
