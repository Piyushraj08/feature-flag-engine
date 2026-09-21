package com.teraapp.model.Flag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flags")
public class EvaluationController {

    @Autowired
    private FlagEvaluationService evaluationService;

    @PostMapping("/{flagId}/evaluate")
    public EvaluationResponse evaluate(@PathVariable Long flagId,
                                       @RequestBody EvaluationRequest request) {
        return evaluationService.evaluate(flagId, request);
    }
}