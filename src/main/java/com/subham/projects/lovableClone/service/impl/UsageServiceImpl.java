package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.dto.subscription.PlanLimitsResponse;
import com.subham.projects.lovableClone.dto.subscription.UsageTodayResponse;
import com.subham.projects.lovableClone.service.UsageService;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceImpl implements UsageService {
    @Override
    public UsageTodayResponse getTodayUsageOfUser(Long userId) {
        return null;
    }

    @Override
    public PlanLimitsResponse getCurrentSubscriptionLimitsOfUser(Long userId) {
        return null;
    }
}
