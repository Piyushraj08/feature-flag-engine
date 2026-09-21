package com.teraapp.model.Flag;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlagEvent {
    private Long flagId;
    private String flagName;
    private String eventType;   // CREATE, UPDATE, DELETE
    private String actor;
    private LocalDateTime timestamp;
}