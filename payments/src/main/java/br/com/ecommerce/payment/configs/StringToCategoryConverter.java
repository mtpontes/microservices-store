package br.com.ecommerce.payment.configs;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import br.com.ecommerce.payment.model.PaymentStatus;

public class StringToCategoryConverter implements Converter<String, PaymentStatus> {

	@Override
	public PaymentStatus convert(@NonNull String source) {
		try {
			return PaymentStatus.fromString(source);
		
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}