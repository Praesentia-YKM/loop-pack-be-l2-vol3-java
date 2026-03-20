package com.loopers.application.payment;

import com.loopers.application.order.OrderService;
import com.loopers.config.PgProperties;
import com.loopers.domain.order.OrderModel;
import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentModel;
import com.loopers.domain.product.Money;
import com.loopers.infrastructure.payment.PgClient;
import com.loopers.infrastructure.payment.dto.PgPaymentResponse;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentFacadeTest {

    @InjectMocks private PaymentFacade paymentFacade;
    @Mock private PaymentService paymentService;
    @Mock private OrderService orderService;
    @Mock private PgClient pgClient;
    @Mock private PgProperties pgProperties;

    @DisplayName("결제 요청")
    @Nested
    class RequestPayment {

        @DisplayName("PG 요청 성공 시 PENDING 상태의 PaymentInfo를 반환한다")
        @Test
        void returnsPendingPaymentOnSuccess() {
            // given
            Long userId = 1L;
            PaymentCommand command = new PaymentCommand(1L, CardType.SAMSUNG, "1234-5678-9814-1451");

            OrderModel mockOrder = mock(OrderModel.class);
            given(mockOrder.getId()).willReturn(1L);
            given(mockOrder.finalAmount()).willReturn(new Money(50000));
            given(orderService.getOrder(1L, userId)).willReturn(mockOrder);

            given(pgProperties.callbackUrl()).willReturn("http://localhost:8080/api/v1/payments/callback");
            given(pgClient.requestPayment(any(), eq(String.valueOf(userId))))
                .willReturn(new PgPaymentResponse("20250816:TR:abc123", "1", "PENDING", null));

            given(paymentService.save(any(PaymentModel.class))).willAnswer(invocation -> invocation.getArgument(0));

            // when
            PaymentInfo result = paymentFacade.requestPayment(userId, command);

            // then
            assertAll(
                () -> assertThat(result.status()).isEqualTo("PENDING"),
                () -> assertThat(result.transactionKey()).isEqualTo("20250816:TR:abc123"),
                () -> assertThat(result.maskedCardNo()).isEqualTo("1234-****-****-1451")
            );
            verify(mockOrder).startPayment();
        }

        @DisplayName("본인 주문이 아니면 예외가 발생한다")
        @Test
        void throwsWhenNotOrderOwner() {
            // given
            Long userId = 999L;
            PaymentCommand command = new PaymentCommand(1L, CardType.SAMSUNG, "1234-5678-9814-1451");
            given(orderService.getOrder(1L, userId)).willThrow(new CoreException(
                com.loopers.support.error.ErrorType.BAD_REQUEST, "본인의 주문만 조회할 수 있습니다."
            ));

            // when & then
            assertThrows(CoreException.class, () -> paymentFacade.requestPayment(userId, command));
        }
    }

    @DisplayName("콜백 처리")
    @Nested
    class HandleCallback {

        @DisplayName("SUCCESS 콜백 수신 시 결제 성공 + 주문 확인 처리된다")
        @Test
        void handlesSuccessCallback() {
            // given
            String transactionKey = "20250816:TR:abc123";
            PaymentModel mockPayment = mock(PaymentModel.class);
            given(mockPayment.orderId()).willReturn(1L);
            given(paymentService.getByTransactionKey(transactionKey)).willReturn(mockPayment);

            OrderModel mockOrder = mock(OrderModel.class);
            given(orderService.getOrderForAdmin(1L)).willReturn(mockOrder);

            // when
            paymentFacade.handleCallback(transactionKey, "SUCCESS", null);

            // then
            verify(mockPayment).markSuccess();
            verify(mockOrder).confirmPayment();
        }

        @DisplayName("FAILED 콜백 수신 시 결제 실패 + 주문 실패 처리된다")
        @Test
        void handlesFailedCallback() {
            // given
            String transactionKey = "20250816:TR:abc123";
            PaymentModel mockPayment = mock(PaymentModel.class);
            given(mockPayment.orderId()).willReturn(1L);
            given(paymentService.getByTransactionKey(transactionKey)).willReturn(mockPayment);

            OrderModel mockOrder = mock(OrderModel.class);
            given(orderService.getOrderForAdmin(1L)).willReturn(mockOrder);

            // when
            paymentFacade.handleCallback(transactionKey, "FAILED", "한도 초과");

            // then
            verify(mockPayment).markFailed("한도 초과");
            verify(mockOrder).failPayment();
        }
    }
}
