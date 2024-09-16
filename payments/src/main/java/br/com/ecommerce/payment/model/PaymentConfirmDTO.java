package br.com.ecommerce.payment.model;

public record PaymentConfirmDTO(Long orderId, PaymentStatus status) {}