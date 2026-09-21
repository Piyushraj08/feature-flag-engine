package com.teraapp.model.Flag;

import lombok.*;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequest {
    private String userId;
    private Map<String, String> attributes;
}