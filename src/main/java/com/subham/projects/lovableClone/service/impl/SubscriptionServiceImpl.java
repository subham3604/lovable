package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.dto.subscription.SubscriptionResponse;
import com.subham.projects.lovableClone.entity.Plan;
import com.subham.projects.lovableClone.entity.Subscription;
import com.subham.projects.lovableClone.entity.User;
import com.subham.projects.lovableClone.enums.SubscriptionStatus;
import com.subham.projects.lovableClone.error.ResourceNotFoundException;
import com.subham.projects.lovableClone.mapper.SubscriptionMapper;
import com.subham.projects.lovableClone.repository.PlanRepository;
import com.subham.projects.lovableClone.repository.ProjectMemberRepository;
import com.subham.projects.lovableClone.repository.SubscriptionRepository;
import com.subham.projects.lovableClone.repository.UserRepository;
import com.subham.projects.lovableClone.security.AuthUtil;
import com.subham.projects.lovableClone.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final AuthUtil authUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final ProjectMemberRepository projectMemberRepository;
    private final int FREE_TIER_MAX_PROJECTS = 1;


    @Override
    public SubscriptionResponse getCurrentSubscription() {
        Long userId = authUtil.getCurrentUserId();

        var currentSubscription = subscriptionRepository.findByUserIdAndStatusIn(userId, Set.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE, SubscriptionStatus.TRIALING)).orElse(new Subscription());

        return subscriptionMapper.toSubscriptionResponseFromSubscription(currentSubscription);
    }

    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId, Instant periodStart, Instant periodEnd) {
        boolean exists = subscriptionRepository.existsByGatewaySubscriptionId(subscriptionId);
        if (exists) return;

        User user = getUser(userId);
        Plan plan = getPlan(planId);

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .gatewaySubscriptionId(subscriptionId)
                .status(SubscriptionStatus.ACTIVE)
                .currentPeriodStart(periodStart)
                .currentPeriodEnd(periodEnd)
                .build();

        subscriptionRepository.save(subscription);
    }

    @Override
    public void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        if (status != null) subscription.setStatus(status);
        if (periodStart != null) subscription.setCurrentPeriodStart(periodStart);
        if (periodEnd != null) subscription.setCurrentPeriodEnd(periodEnd);
        if (cancelAtPeriodEnd != null) subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
        if (planId != null) {
            Plan plan = getPlan(planId);
            subscription.setPlan(plan);
        }
        subscriptionRepository.save(subscription);
    }

    @Override
    public void cancelSubscription(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscriptionRepository.save(subscription);
    }

    @Override
    public void renewSubscriptionDate(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        Instant newStart = periodStart != null ? periodStart : subscription.getCurrentPeriodEnd();
        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);

        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE || subscription.getStatus() == SubscriptionStatus.INCOMPLETE) {
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }

        subscriptionRepository.save(subscription);

    }


    @Override
    public void markSubscriptionStatusPastDue(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);

        if (subscription.getStatus() == SubscriptionStatus.PAST_DUE) {
            return;
        }

        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);

        //TODO(Subham): Notify user via email
    }

    @Override
    public boolean canCreateNewProject() {
        Long userId = authUtil.getCurrentUserId();
        SubscriptionResponse subscription = getCurrentSubscription();
        int ownedProjects = projectMemberRepository.countProjectOwnedByUser(userId);


        if (subscription.plan() == null) {
            return ownedProjects < FREE_TIER_MAX_PROJECTS;
        }

        return ownedProjects < subscription.plan().maxProjects();
    }

    @Override
    public boolean existsByGatewaySubscriptionId(String subscriptionId) {
        return subscriptionRepository.existsByGatewaySubscriptionId(subscriptionId);
    }

    // Utility Methods
    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
    }

    private Plan getPlan(Long planId) {
        return planRepository.findById(planId).orElseThrow(() -> new ResourceNotFoundException("Plan", planId.toString()));
    }

    private Subscription getSubscription(String gatewaySubscriptionId) {
        return subscriptionRepository.findByGatewaySubscriptionId(gatewaySubscriptionId).orElseThrow(() -> new ResourceNotFoundException("Subscription", gatewaySubscriptionId));
    }
}
