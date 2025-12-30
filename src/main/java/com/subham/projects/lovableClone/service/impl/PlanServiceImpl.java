package com.subham.projects.lovableClone.service.impl;

import com.subham.projects.lovableClone.dto.subscription.PlanResponse;
import com.subham.projects.lovableClone.service.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {
    @Override
    public List<PlanResponse> getAllActivePlans() {
        return List.of();
    }
}
