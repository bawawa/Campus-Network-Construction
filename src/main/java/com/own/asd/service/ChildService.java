package com.own.asd.service;

import com.own.asd.model.user.ASDProfile;
import com.own.asd.model.user.Child;
import com.own.asd.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChildService {

    private final ChildRepository childRepository;

    public Child createChild(Child child) {
        child.setIsActive(true);
        return childRepository.save(child);
    }

    public Child updateChild(Long id, Long parentId, Child childDetails) {
        Child child = getChildByIdAndParent(id, parentId);
        child.setName(childDetails.getName());
        child.setGender(childDetails.getGender());
        child.setBirthDate(childDetails.getBirthDate());
        child.setDiagnosisInfo(childDetails.getDiagnosisInfo());
        child.setAllergyHistory(childDetails.getAllergyHistory());
        child.setPreferredFoods(childDetails.getPreferredFoods());
        return childRepository.save(child);
    }

    public Child getChildByIdAndParent(Long id, Long parentId) {
        return childRepository.findByIdAndParentId(id, parentId)
                .orElseThrow(() -> new RuntimeException("Child not found with id: " + id + " for parent: " + parentId));
    }

    public List<Child> getChildrenByParent(Long parentId) {
        return childRepository.findByParentIdAndIsActiveTrue(parentId);
    }

    public List<Child> getActiveChildrenByParent(Long parentId) {
        return childRepository.findActiveChildrenByParent(parentId);
    }

    public Optional<Child> getChildWithASDProfile(Long childId) {
        return childRepository.findChildWithASDProfile(childId);
    }

    public void deactivateChild(Long id, Long parentId) {
        Child child = getChildByIdAndParent(id, parentId);
        child.setIsActive(false);
        childRepository.save(child);
    }

    public boolean existsByIdAndParent(Long id, Long parentId) {
        return childRepository.existsByIdAndParentId(id, parentId);
    }
}

