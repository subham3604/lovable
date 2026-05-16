package com.subham.projects.lovableClone.entity;

import com.subham.projects.lovableClone.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @Setter
    Plan plan;

    @Setter
    @Enumerated(value = EnumType.STRING)
    SubscriptionStatus status;

    String gatewaySubscriptionId;

    @Setter
    Instant currentPeriodStart;
    @Setter
    Instant currentPeriodEnd;
    @Setter
    Boolean cancelAtPeriodEnd = false;

    @CreationTimestamp
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;
}
