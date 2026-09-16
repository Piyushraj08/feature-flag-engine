package com.teraapp.model.Flag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.teraapp.model.Flag.exception.FlagNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/flags/{flagId}/rules")
public class RuleController {

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private FlagRepository flagRepository;

    // GET all rules for a flag
    @GetMapping
    public List<TargetingRule> getRules(@PathVariable Long flagId) {
        return ruleRepository.findByFlagId(flagId);
    }

    // POST add rule to a flag
    @PostMapping
    public TargetingRule addRule(@PathVariable Long flagId,
                                 @RequestBody TargetingRule rule) {
        Flag flag = flagRepository.findById(flagId)
                .orElseThrow(() -> new FlagNotFoundException("Flag not found with id: " + flagId));

        rule.setFlag(flag);
        return ruleRepository.save(rule);
    }

    // DELETE a rule
    @DeleteMapping("/{ruleId}")
    public String deleteRule(@PathVariable Long flagId, @PathVariable Long ruleId) {
        TargetingRule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + ruleId));

        ruleRepository.delete(rule);
        return "Rule deleted successfully with id: " + ruleId;
    }
}