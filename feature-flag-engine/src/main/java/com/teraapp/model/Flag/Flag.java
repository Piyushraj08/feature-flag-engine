package com.teraapp.model.Flag;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // primary key
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Boolean enabled = false;

    @Column(name = "rollout_percentage")
    private Integer rolloutPercentage = 0;
}