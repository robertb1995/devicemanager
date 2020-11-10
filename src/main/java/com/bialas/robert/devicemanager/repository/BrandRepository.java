package com.bialas.robert.devicemanager.repository;

import com.bialas.robert.devicemanager.domain.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, String> {

}
