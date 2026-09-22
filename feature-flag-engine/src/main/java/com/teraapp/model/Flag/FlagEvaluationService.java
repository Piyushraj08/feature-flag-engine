package com.teraapp.model.Flag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlagEvaluationService {

    @Autowired
    private FlagCacheService flagCacheService;

    public EvaluationResponse evaluate(Long flagId, EvaluationRequest request) {

        Flag flag = flagCacheService.getFlag(flagId);

        // Step 1: Agar flag disabled hai, toh turant false
        if (!flag.getEnabled()) {
            return new EvaluationResponse(flag.getName(), false, "Flag is disabled");
        }

        for (TargetingRule rule : flag.getRules()) {
            String userValue = request.getAttributes().get(rule.getAttribute());

            if (userValue == null) {
                continue;
            }

            if (!matches(rule.getOperator(), userValue, rule.getValue())) {
                return new EvaluationResponse(flag.getName(), false,
                        "Rule failed: " + rule.getAttribute() + " " + rule.getOperator() + " " + rule.getValue());
            }
        }


        if (flag.getRolloutPercentage() != null && flag.getRolloutPercentage() > 0) {
            int bucket = getBucket(request.getUserId(), flag.getName());
            if (bucket >= flag.getRolloutPercentage()) {
                return new EvaluationResponse(flag.getName(), false,
                        "User not in rollout percentage (" + bucket + " >= " + flag.getRolloutPercentage() + ")");
            }
        }

        return new EvaluationResponse(flag.getName(), true, "All rules matched");
    }

    private boolean matches(String operator, String userValue, String ruleValue) {
        switch (operator) {
            case "equals":
                return userValue.equals(ruleValue);
            case "contains":
                return userValue.contains(ruleValue);
            case "in":
                return ruleValue.contains(userValue);
            default:
                return false;
        }
    }

    private int getBucket(String userId, String flagName) {
        String input = userId + ":" + flagName;
        int hash = Math.abs(input.hashCode());
        return hash % 100;
    }
}