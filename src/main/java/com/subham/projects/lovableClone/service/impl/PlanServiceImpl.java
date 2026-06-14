package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.dto.subscription.PlanResponse;
import com.subham.projects.lovableClone.mapper.SubscriptionMapper;
import com.subham.projects.lovableClone.repository.PlanRepository;
import com.subham.projects.lovableClone.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Override
    public List<PlanResponse> getAllActivePlans() {
        return planRepository.findAll().stream()
                .filter(plan -> plan.getActive() != null && plan.getActive())
                .map(subscriptionMapper::toPlanResponseFromPlan)
                .toList();
    }
}
