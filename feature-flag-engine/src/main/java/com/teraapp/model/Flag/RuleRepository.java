package com.teraapp.model.Flag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RuleRepository extends JpaRepository<TargetingRule, Long> {
    List<TargetingRule> findByFlagId(Long flagId);
}