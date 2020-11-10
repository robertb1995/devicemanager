package com.bialas.robert.devicemanager.repository;

import com.bialas.robert.devicemanager.domain.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query("SELECT DISTINCT d from Device d where d.deviceName = ?1 and d.brand.name = ?2")
    Optional<Device> findByDeviceNameAndBrand(String deviceName, String brandName);

    @Query("SELECT DISTINCT d from Device d JOIN FETCH d.brand where d.brand.name = ?1")
    List<Device> findAllByBrandName(String brandName);

    @Query("SELECT DISTINCT d from Device d JOIN FETCH d.brand")
    List<Device> findAll();

}
