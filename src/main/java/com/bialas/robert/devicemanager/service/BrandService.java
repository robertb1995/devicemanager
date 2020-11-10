package com.bialas.robert.devicemanager.service;

import com.bialas.robert.devicemanager.domain.entity.Brand;
import com.bialas.robert.devicemanager.repository.BrandRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public Optional<Brand> findById(String id) {
        return brandRepository.findById(id);
    }

}
