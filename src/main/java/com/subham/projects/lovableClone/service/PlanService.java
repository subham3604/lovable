package com.subham.projects.lovableClone.service;

import com.subham.projects.lovableClone.dto.subscription.PlanResponse;

import java.util.List;

public interface PlanService {
     List<PlanResponse> getAllActivePlans();
}
