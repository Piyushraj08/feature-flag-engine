package com.teraapp.model.Flag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;   // ← ADD KAR
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flag implements Serializable {   // ← ADD KAR

    private static final long serialVersionUID = 1L;   // ← ADD KAR

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Boolean enabled = false;

    @Column(name = "rollout_percentage")
    private Integer rolloutPercentage = 0;

    @OneToMany(mappedBy = "flag", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TargetingRule> rules = new ArrayList<>();
}