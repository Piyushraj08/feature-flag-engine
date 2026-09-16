package com.teraapp.model.Flag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Boolean enabled = false;

    @Column(name = "rollout_percentage")
    private Integer rolloutPercentage = 0;

    // NEW: One-to-many relationship
    @OneToMany(mappedBy = "flag", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TargetingRule> rules = new ArrayList<>();
}