package com.own.asd.service;

import com.own.asd.model.user.Child;
import com.own.asd.model.user.User;
import com.own.asd.repository.ChildRepository;
import com.own.asd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChildService {

    private final ChildRepository childRepository;
    private final UserRepository userRepository;

    public Child createChild(Child child) {
        // 从当前认证用户获取父用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // 从token中获取userId（临时方案）
            // TODO: 应该从JWT token中解析userId
            // 暂时使用默认值
            User parent = new User();
            parent.setId(1L); // 临时硬编码，后续需要修复
            child.setParent(parent);
        }
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

    public List<Child> getAllChildren() {
        return childRepository.findAll();
    }

    public Child getChildById(Long id) {
        return childRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Child not found with id: " + id));
    }

    public List<Child> getChildrenByParentWithAuth(Long parentId) {
        // 从认证上下文获取当前用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null) {
            // TODO: 应该从JWT token中解析userId
            // 暂时从用户名中提取（用于测试）
            String username = authentication.getName();
            Long currentUserId = 1L; // 临时硬编码：parent1@asd.com 的 id 是 1
            return childRepository.findByParentIdAndIsActiveTrue(currentUserId);
        }
        // 未认证或无法获取用户，返回空列表
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

