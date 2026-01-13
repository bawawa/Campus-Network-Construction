package com.own.asd.repository;

import com.own.asd.model.nutrition.DietaryRestriction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DietaryRestrictionRepository extends JpaRepository<DietaryRestriction, Long> {

    List<DietaryRestriction> findByChildIdAndIsActiveTrueOrderByCreatedAtDesc(Long childId);
}

