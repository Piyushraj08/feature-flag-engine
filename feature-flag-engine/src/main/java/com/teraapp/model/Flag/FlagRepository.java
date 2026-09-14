package com.teraapp.model.Flag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlagRepository extends JpaRepository<Flag, Long> { // saare crud methods free mil jayenge
}