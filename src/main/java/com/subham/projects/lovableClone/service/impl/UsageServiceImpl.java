package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.dto.subscription.PlanLimitsResponse;
import com.subham.projects.lovableClone.dto.subscription.UsageTodayResponse;
import com.subham.projects.lovableClone.entity.Plan;
import com.subham.projects.lovableClone.entity.Subscription;
import com.subham.projects.lovableClone.enums.SubscriptionStatus;
import com.subham.projects.lovableClone.repository.SubscriptionRepository;
import com.subham.projects.lovableClone.service.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public UsageTodayResponse getTodayUsageOfUser(Long userId) {
        var subscriptionOpt = subscriptionRepository.findByUserIdAndStatusIn(
                userId, Set.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE, SubscriptionStatus.TRIALING)
        );

        int tokensLimit = 10000;
        int previewsLimit = 1;

        if (subscriptionOpt.isPresent()) {
            Plan plan = subscriptionOpt.get().getPlan();
            if (plan != null) {
                tokensLimit = plan.getMaxTokensPerDay() != null ? plan.getMaxTokensPerDay() : 10000;
                previewsLimit = plan.getMaxPreviews() != null ? plan.getMaxPreviews() : 1;
            }
        }

        return new UsageTodayResponse(0, tokensLimit, 0, previewsLimit);
    }

    @Override
    public PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId) {
        var subscriptionOpt = subscriptionRepository.findByUserIdAndStatusIn(
                userId, Set.of(SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE, SubscriptionStatus.TRIALING)
        );

        String planName = "Free Plan";
        int maxTokensPerDay = 10000;
        int maxProjects = 1;
        boolean unlimitedAi = false;

        if (subscriptionOpt.isPresent()) {
            Plan plan = subscriptionOpt.get().getPlan();
            if (plan != null) {
                planName = plan.getName();
                maxTokensPerDay = plan.getMaxTokensPerDay() != null ? plan.getMaxTokensPerDay() : 10000;
                maxProjects = plan.getMaxProjects() != null ? plan.getMaxProjects() : 1;
                unlimitedAi = plan.getUnlimitedAi() != null ? plan.getUnlimitedAi() : false;
            }
        }

        return new PlanLimitsResponse(planName, maxTokensPerDay, maxProjects, unlimitedAi);
    }
}
