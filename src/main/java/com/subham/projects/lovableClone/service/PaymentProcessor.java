package com.subham.projects.lovableClone.service;

import com.stripe.model.StripeObject;
import com.subham.projects.lovableClone.dto.subscription.CheckoutRequest;
import com.subham.projects.lovableClone.dto.subscription.CheckoutResponse;
import com.subham.projects.lovableClone.dto.subscription.PortalResponse;

import java.util.Map;

public interface PaymentProcessor {
    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request);

    PortalResponse openCustomerPortal();

    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata);
}
