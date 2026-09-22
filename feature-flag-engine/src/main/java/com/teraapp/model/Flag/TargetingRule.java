package com.teraapp.model.Flag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "targeting_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TargetingRule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String attribute;

    @Column(nullable = false)
    private String operator;

    @Column(nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flag_id", nullable = false)
    @JsonIgnore
    private Flag flag;
}