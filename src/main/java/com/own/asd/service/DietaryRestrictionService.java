package com.own.asd.service;

import com.own.asd.model.nutrition.DietaryRestriction;
import com.own.asd.model.user.Child;
import com.own.asd.repository.ChildRepository;
import com.own.asd.repository.DietaryRestrictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DietaryRestrictionService {

    private final DietaryRestrictionRepository dietaryRestrictionRepository;
    private final ChildRepository childRepository;

    public List<DietaryRestriction> getByChildId(Long childId) {
        return dietaryRestrictionRepository.findByChildIdAndIsActiveTrueOrderByCreatedAtDesc(childId);
    }

    public DietaryRestriction create(Long childId, DietaryRestriction restriction) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found with id: " + childId));

        restriction.setChild(child);
        restriction.setIsActive(true);
        return dietaryRestrictionRepository.save(restriction);
    }

    public DietaryRestriction update(Long childId, Long restrictionId, DietaryRestriction restrictionDetails) {
        DietaryRestriction existing = dietaryRestrictionRepository.findById(restrictionId)
                .orElseThrow(() -> new RuntimeException("Dietary restriction not found with id: " + restrictionId));

        if (!existing.getChild().getId().equals(childId)) {
            throw new RuntimeException("Dietary restriction does not belong to the specified child");
        }

        existing.setRestrictionType(restrictionDetails.getRestrictionType());
        existing.setFoodName(restrictionDetails.getFoodName());
        existing.setSeverityLevel(restrictionDetails.getSeverityLevel());
        existing.setDescription(restrictionDetails.getDescription());

        return dietaryRestrictionRepository.save(existing);
    }

    public void delete(Long childId, Long restrictionId) {
        DietaryRestriction restriction = dietaryRestrictionRepository.findById(restrictionId)
                .orElseThrow(() -> new RuntimeException("Dietary restriction not found with id: " + restrictionId));

        if (!restriction.getChild().getId().equals(childId)) {
            throw new RuntimeException("Dietary restriction does not belong to the specified child");
        }

        restriction.setIsActive(false);
        dietaryRestrictionRepository.save(restriction);
    }
}

