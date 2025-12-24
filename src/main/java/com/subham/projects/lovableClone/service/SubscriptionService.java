package com.subham.projects.lovableClone.service;

import com.subham.projects.lovableClone.dto.subscription.CheckoutRequest;
import com.subham.projects.lovableClone.dto.subscription.CheckoutResponse;
import com.subham.projects.lovableClone.dto.subscription.PortalResponse;
import com.subham.projects.lovableClone.dto.subscription.SubscriptionResponse;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription(Long userId);

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request, Long userId);

    PortalResponse openCustomerPortal(Long userId);
}
