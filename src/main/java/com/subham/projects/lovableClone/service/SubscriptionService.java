package com.subham.projects.lovableClone.service;

import com.subham.projects.lovableClone.dto.subscription.SubscriptionResponse;
import com.subham.projects.lovableClone.enums.SubscriptionStatus;

import java.time.Instant;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription();

    void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId);

    void updateSubscription(String subscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String subscriptionId);

    void renewSubscriptionDate(String subscriptionId, Instant periodStart, Instant periodEnd);

    void markSubscriptionStatusPastDue(String subscriptionId);

    boolean canCreateNewProject();
}
