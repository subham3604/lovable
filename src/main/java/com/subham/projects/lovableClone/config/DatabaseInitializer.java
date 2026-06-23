package com.subham.projects.lovableClone.config;

import com.subham.projects.lovableClone.entity.Plan;
import com.subham.projects.lovableClone.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final PlanRepository planRepository;

    @Override
    public void run(String... args) {
        if (planRepository.count() == 0) {
            log.info("Seeding plan database with initial tiers...");

            Plan proPlan = new Plan();
            proPlan.setId(1L);
            proPlan.setName("Pro Plan");
            proPlan.setStripePriceId("price_1THqGM2eTzfeupZ87zYcXWOg");
            proPlan.setMaxProjects(3);
            proPlan.setMaxTokensPerDay(10000);
            proPlan.setMaxPreviews(3);
            proPlan.setUnlimitedAi(false);
            proPlan.setPrice("199");
            proPlan.setActive(true);

            Plan elitePlan = new Plan();
            elitePlan.setId(2L);
            elitePlan.setName("Elite Plan");
            elitePlan.setStripePriceId("price_1THqH22eTzfeupZ88sH8jiWK");
            elitePlan.setMaxProjects(10);
            elitePlan.setMaxTokensPerDay(100000);
            elitePlan.setMaxPreviews(10);
            elitePlan.setUnlimitedAi(false);
            elitePlan.setPrice("699");
            elitePlan.setActive(true);

            Plan unlimitedPlan = new Plan();
            unlimitedPlan.setId(3L);
            unlimitedPlan.setName("Unlimited Plan");
            unlimitedPlan.setStripePriceId("price_1THqHa2eTzfeupZ829OMxub4");
            unlimitedPlan.setMaxProjects(100);
            unlimitedPlan.setMaxTokensPerDay(1000000);
            unlimitedPlan.setMaxPreviews(100);
            unlimitedPlan.setUnlimitedAi(true);
            unlimitedPlan.setPrice("1599");
            unlimitedPlan.setActive(true);

            planRepository.saveAll(List.of(proPlan, elitePlan, unlimitedPlan));
            log.info("Plan database seeded successfully!");
        } else {
            log.info("Plan database already seeded.");
        }
    }
}
