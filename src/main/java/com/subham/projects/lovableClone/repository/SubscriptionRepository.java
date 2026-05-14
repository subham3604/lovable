package com.subham.projects.lovableClone.repository;

import com.subham.projects.lovableClone.entity.Subscription;
import com.subham.projects.lovableClone.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserIdAndStatusIn(Long userId, Set<SubscriptionStatus> statusSet);

    boolean existsByGatewaySubscriptionId(String subscriptionId);

    Optional<Subscription> findByGatewaySubscriptionId(String gatewaySubscriptionId);
}