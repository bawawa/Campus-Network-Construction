package com.own.asd.service;

import com.own.asd.model.user.ASDProfile;
import com.own.asd.model.user.Child;
import com.own.asd.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ASDProfileService {

    private final ChildRepository childRepository;

    public ASDProfile createASDProfile(Long childId, ASDProfile asdProfile) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found with id: " + childId));

        asdProfile.setChild(child);
        child.getAsdProfiles().add(asdProfile);
        childRepository.save(child);

        return asdProfile;
    }

    public ASDProfile updateASDProfile(Long childId, Long profileId, ASDProfile profileDetails) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found with id: " + childId));

        ASDProfile existingProfile = child.getAsdProfiles().stream()
                .filter(p -> p.getId().equals(profileId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ASD Profile not found with id: " + profileId));

        // Update profile fields
        existingProfile.setAllergicFoods(profileDetails.getAllergicFoods());
        existingProfile.setIntolerantFoods(profileDetails.getIntolerantFoods());
        existingProfile.setColorPreference(profileDetails.getColorPreference());
        existingProfile.setTexturePreference(profileDetails.getTexturePreference());
        existingProfile.setSmellPreference(profileDetails.getSmellPreference());
        existingProfile.setEatingBehaviorNotes(profileDetails.getEatingBehaviorNotes());
        existingProfile.setSpecialNotes(profileDetails.getSpecialNotes());

        childRepository.save(child);
        return existingProfile;
    }

    public Optional<ASDProfile> getASDProfile(Long childId, Long profileId) {
        return childRepository.findById(childId)
                .flatMap(child -> child.getAsdProfiles().stream()
                        .filter(p -> p.getId().equals(profileId))
                        .findFirst());
    }

    public List<ASDProfile> getASDProfilesByChild(Long childId) {
        return childRepository.findById(childId)
                .map(child -> new ArrayList<>(child.getAsdProfiles()))
                .orElseThrow(() -> new RuntimeException("Child not found with id: " + childId));
    }

    public void deleteASDProfile(Long childId, Long profileId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new RuntimeException("Child not found with id: " + childId));

        child.getAsdProfiles().removeIf(p -> p.getId().equals(profileId));
        childRepository.save(child);
    }
}

