package com.teraapp.model.Flag;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {
    private String flagName;
    private Boolean enabled;
    private String reason;
}