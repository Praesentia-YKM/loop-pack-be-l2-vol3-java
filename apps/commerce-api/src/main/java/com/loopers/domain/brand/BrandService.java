package com.loopers.domain.brand;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class BrandService {

    private final BrandRepository brandRepository;

    @Transactional
    public BrandModel register(String name, String description) {
        brandRepository.findByName(name).ifPresent(existing -> {
            throw new CoreException(ErrorType.CONFLICT, "이미 존재하는 브랜드 이름입니다: " + name);
        });
        return brandRepository.save(new BrandModel(name, description));
    }

    @Transactional(readOnly = true)
    public BrandModel getById(Long id) {
        return brandRepository.findById(id)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "브랜드를 찾을 수 없습니다. [id = " + id + "]"));
    }

    @Transactional(readOnly = true)
    public Page<BrandModel> getAll(Pageable pageable) {
        return brandRepository.findAll(pageable);
    }

    @Transactional
    public BrandModel update(Long id, String name, String description) {
        BrandModel brand = getById(id);
        brandRepository.findByName(name)
            .filter(existing -> !existing.getId().equals(brand.getId()))
            .ifPresent(existing -> {
                throw new CoreException(ErrorType.CONFLICT, "이미 존재하는 브랜드 이름입니다: " + name);
            });
        brand.update(name, description);
        return brand;
    }

    @Transactional
    public void delete(Long id) {
        BrandModel brand = getById(id);
        brand.delete();
    }
}
