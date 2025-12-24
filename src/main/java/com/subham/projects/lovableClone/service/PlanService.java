package com.subham.projects.lovableClone.service;

import com.codingshuttle.projects.lovable_clone.dto.subscription.PlanResponse;

import java.util.List;

public interface PlanService {
     List<PlanResponse> getAllActivePlans();
}
