package com.back.api.payment.payment.client;

import org.springframework.stereotype.Component;

import com.back.api.payment.payment.dto.request.PaymentConfirmCommand;
import com.back.api.payment.payment.dto.response.PaymentConfirmResult;

@Component
public interface PaymentClient {
	PaymentConfirmResult confirm(PaymentConfirmCommand command);
}
