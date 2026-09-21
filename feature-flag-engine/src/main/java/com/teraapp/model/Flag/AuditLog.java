package com.teraapp.model.Flag;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long flagId;

    private String flagName;

    @Column(nullable = false)
    private String eventType;

    private String actor;

    private LocalDateTime timestamp;
}