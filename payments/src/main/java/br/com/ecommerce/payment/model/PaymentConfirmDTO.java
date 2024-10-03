package br.com.ecommerce.payment.model;

public record PaymentConfirmDTO(String orderId, PaymentStatus status) {}