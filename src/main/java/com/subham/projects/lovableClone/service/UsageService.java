package com.subham.projects.lovableClone.service;

import com.subham.projects.lovableClone.dto.subscription.PlanLimitsResponse;
import com.subham.projects.lovableClone.dto.subscription.UsageTodayResponse;

public interface UsageService {
     UsageTodayResponse getTodayUsageOfUser(Long userId);

    PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId);
}
