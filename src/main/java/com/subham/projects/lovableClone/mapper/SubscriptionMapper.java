package com.subham.projects.lovableClone.mapper;

import com.subham.projects.lovableClone.dto.subscription.PlanResponse;
import com.subham.projects.lovableClone.dto.subscription.SubscriptionResponse;
import com.subham.projects.lovableClone.entity.Plan;
import com.subham.projects.lovableClone.entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    SubscriptionResponse toSubscriptionResponseFromSubscription(Subscription subscription);

    PlanResponse toPlanResponseFromPlan(Plan plan);
}
