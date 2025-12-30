package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.dto.subscription.CheckoutRequest;
import com.subham.projects.lovableClone.dto.subscription.CheckoutResponse;
import com.subham.projects.lovableClone.dto.subscription.PortalResponse;
import com.subham.projects.lovableClone.dto.subscription.SubscriptionResponse;
import com.subham.projects.lovableClone.service.SubscriptionService;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    @Override
    public SubscriptionResponse getCurrentSubscription(Long userId) {
        return null;
    }

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request, Long userId) {
        return null;
    }

    @Override
    public PortalResponse openCustomerPortal(Long userId) {
        return null;
    }
}
