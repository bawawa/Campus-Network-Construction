package com.own.asd.service;

import com.own.asd.model.system.Dictionary;
import com.own.asd.repository.DictionaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DictionaryService {

    @Autowired
    private DictionaryRepository dictionaryRepository;

    /**
     * 创建字典项
     */
    public Dictionary createDictionary(String type, String code, String displayName,
                                      String description, Integer sortOrder, Boolean isActive,
                                      Long parentId, String extraData) {

        if (dictionaryRepository.findByTypeAndCode(type, code).isPresent()) {
            throw new RuntimeException("字典代码已存在");
        }

        Dictionary dictionary = new Dictionary();
        dictionary.setType(type);
        dictionary.setCode(code);
        dictionary.setDisplayName(displayName);
        dictionary.setDescription(description);
        dictionary.setSortOrder(sortOrder != null ? sortOrder : 0);
        dictionary.setIsActive(isActive != null ? isActive : true);
        dictionary.setParentId(parentId);
        dictionary.setExtraData(extraData);

        return dictionaryRepository.save(dictionary);
    }

    /**
     * 更新字典项
     */
    public Dictionary updateDictionary(Long dictionaryId, String type, String code, String displayName,
                                      String description, Integer sortOrder, Boolean isActive,
                                      Long parentId, String extraData) {

        Dictionary dictionary = dictionaryRepository.findById(dictionaryId)
                .orElseThrow(() -> new RuntimeException("字典项不存在"));

        if (code != null && !code.equals(dictionary.getCode())) {
            if (dictionaryRepository.existsByTypeAndCodeAndIdNot(type, code, dictionaryId)) {
                throw new RuntimeException("字典代码已存在");
            }
            dictionary.setCode(code);
        }

        if (type != null) dictionary.setType(type);
        if (displayName != null) dictionary.setDisplayName(displayName);
        if (description != null) dictionary.setDescription(description);
        if (sortOrder != null) dictionary.setSortOrder(sortOrder);
        if (isActive != null) dictionary.setIsActive(isActive);
        if (parentId != null) dictionary.setParentId(parentId);
        if (extraData != null) dictionary.setExtraData(extraData);

        return dictionaryRepository.save(dictionary);
    }

    /**
     * 删除字典项
     */
    public void deleteDictionary(Long dictionaryId) {
        dictionaryRepository.deleteById(dictionaryId);
    }

    /**
     * 根据ID获取字典项
     */
    @Transactional(readOnly = true)
    public Optional<Dictionary> getDictionaryById(Long dictionaryId) {
        return dictionaryRepository.findById(dictionaryId);
    }

    /**
     * 根据类型和代码获取字典项
     */
    @Transactional(readOnly = true)
    public Optional<Dictionary> getDictionaryByTypeAndCode(String type, String code) {
        return dictionaryRepository.findByTypeAndCode(type, code);
    }

    /**
     * 根据类型获取所有启用的字典项
     */
    @Transactional(readOnly = true)
    public List<Dictionary> getDictionariesByType(String type) {
        return dictionaryRepository.findByTypeAndIsActiveTrueOrderBySortOrderAsc(type);
    }

    /**
     * 分页获取指定类型的字典项
     */
    @Transactional(readOnly = true)
    public Page<Dictionary> getDictionariesByType(String type, Pageable pageable) {
        return dictionaryRepository.findByTypeAndIsActiveTrueOrderBySortOrderAsc(type, pageable);
    }

    /**
     * 根据父ID获取子字典项
     */
    @Transactional(readOnly = true)
    public List<Dictionary> getChildDictionaries(Long parentId) {
        return dictionaryRepository.findByParentIdAndIsActiveTrueOrderBySortOrderAsc(parentId);
    }

    /**
     * 获取指定类型下的根字典项
     */
    @Transactional(readOnly = true)
    public List<Dictionary> getRootDictionariesByType(String type) {
        return dictionaryRepository.findByTypeAndParentIdIsNullAndIsActiveTrueOrderBySortOrderAsc(type);
    }

    /**
     * 根据显示名称搜索字典项
     */
    @Transactional(readOnly = true)
    public List<Dictionary> searchDictionariesByDisplayName(String displayName) {
        return dictionaryRepository.findByDisplayNameContainingIgnoreCaseAndIsActiveTrue(displayName);
    }

    /**
     * 获取所有字典类型
     */
    @Transactional(readOnly = true)
    public List<String> getAllDictionaryTypes() {
        return dictionaryRepository.findAllTypes();
    }

    /**
     * 检查字典代码是否唯一
     */
    @Transactional(readOnly = true)
    public boolean isDictionaryCodeUnique(String type, String code, Long excludeId) {
        if (excludeId == null) {
            return !dictionaryRepository.findByTypeAndCode(type, code).isPresent();
        }
        return !dictionaryRepository.existsByTypeAndCodeAndIdNot(type, code, excludeId);
    }

    /**
     * 构建字典树
     */
    @Transactional(readOnly = true)
    public List<Dictionary> buildDictionaryTree(String type) {
        List<Dictionary> rootDictionaries = getRootDictionariesByType(type);
        buildDictionaryTreeRecursive(rootDictionaries);
        return rootDictionaries;
    }

    private void buildDictionaryTreeRecursive(List<Dictionary> dictionaries) {
        for (Dictionary dictionary : dictionaries) {
            List<Dictionary> children = getChildDictionaries(dictionary.getId());
            if (!children.isEmpty()) {
                // 这里可以设置子字典项，如果需要的话
                buildDictionaryTreeRecursive(children);
            }
        }
    }

    /**
     * 获取字典映射（用于前端下拉框等）
     */
    @Transactional(readOnly = true)
    public Map<String, String> getDictionaryMap(String type) {
        List<Dictionary> dictionaries = getDictionariesByType(type);
        return dictionaries.stream()
                .collect(Collectors.toMap(
                        Dictionary::getCode,
                        Dictionary::getDisplayName
                ));
    }

    /**
     * 批量获取多个类型的字典映射
     */
    @Transactional(readOnly = true)
    public Map<String, Map<String, String>> getMultipleDictionaryMaps(List<String> types) {
        Map<String, Map<String, String>> result = new java.util.HashMap<>();
        for (String type : types) {
            result.put(type, getDictionaryMap(type));
        }
        return result;
    }
}

