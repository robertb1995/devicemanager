package com.bialas.robert.devicemanager.service;

import com.bialas.robert.devicemanager.domain.dto.DeviceDTO;
import com.bialas.robert.devicemanager.domain.entity.Brand;
import com.bialas.robert.devicemanager.domain.entity.Device;
import com.bialas.robert.devicemanager.mapper.DeviceDTOMapper;
import com.bialas.robert.devicemanager.repository.DeviceRepository;
import com.bialas.robert.devicemanager.service.exception.DeviceAlreadyExistentException;
import com.bialas.robert.devicemanager.service.exception.DeviceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;

    private final BrandService brandService;

    private final Clock clock;

    private final DeviceDTOMapper deviceDTOMapper;

    public DeviceDTO addDevice(DeviceDTO deviceDTO) {
        Device device = new Device();

        log.info("Seeing if device {} already exists.", deviceDTO.toString());
        Optional<Device> deviceOptional = deviceRepository.findByDeviceNameAndBrand(deviceDTO.getDeviceName(), deviceDTO.getBrandName());

        if (deviceOptional.isPresent()) {
            log.info("Device {} already exists.", deviceOptional.get());
            throw new DeviceAlreadyExistentException(deviceDTO.getDeviceName(), deviceDTO.getBrandName());
        }

        setBrand(device, deviceDTO.getBrandName());
        device.setDeviceName(deviceDTO.getDeviceName());
        device.setCreationTime(ZonedDateTime.now(clock));
        log.info("Adding new device {}.", device.toString());

        return deviceDTOMapper.toDeviceDTO(deviceRepository.save(device));
    }

    public Optional<DeviceDTO> findById(Long id) {
        log.info("Finding Device for id {}", id);
        return deviceRepository.findById(id).map(deviceDTOMapper::toDeviceDTO);
    }

    public List<DeviceDTO> findAll() {
        log.info("Finding all devices");
        return deviceRepository.findAll()
                .stream()
                .map(deviceDTOMapper::toDeviceDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        try {
            log.info("Deleting device by id {}", id);
            deviceRepository.deleteById(id);
        } catch (EmptyResultDataAccessException exception) {
            log.error("Device with id {} doesn't exist", id);
            throw new DeviceNotFoundException(exception.getMessage());
        }
    }

    public List<DeviceDTO> findAllByBrand(String brand) {
        log.info("Finding all devices by brand name: {}", brand);
        return deviceRepository.findAllByBrandName(brand)
                .stream()
                .map(deviceDTOMapper::toDeviceDTO)
                .collect(Collectors.toList());
    }

    public Optional<DeviceDTO> fullyUpdateDevice(Long id, DeviceDTO deviceDTO) {
        log.info("Fully updating device with id: {}, with the following new values: {}", id, deviceDTO);
        Optional<Device> deviceOptional = deviceRepository.findById(id);

        if (deviceOptional.isPresent()) {
            Device deviceToUpdate = deviceOptional.get();
            setBrand(deviceToUpdate, deviceDTO.getBrandName());
            deviceToUpdate.setCreationTime(deviceDTO.getCreationTime());
            deviceToUpdate.setDeviceName(deviceDTO.getDeviceName());

            log.info("Fully updating device with id: {}, with the following properties: {}", id, deviceToUpdate.toString());
            return Optional.of(deviceDTOMapper.toDeviceDTO(deviceRepository.save(deviceToUpdate)));
        } else {
            log.info("Fully updating device with id: {} failed as no device has been found with this id.", id);
            return Optional.empty();
        }
    }

    public Optional<DeviceDTO> partiallyUpdateDevice(Long id, DeviceDTO deviceDTO) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);

        if (deviceOptional.isPresent()) {
            Device deviceToUpdate = deviceOptional.get();
            deviceDTOMapper.updateDeviceFromDTO(deviceDTO, deviceToUpdate);
            if (deviceDTO.getBrandName() != null) {
                log.info("Brand field in the request contains non-null field with name: {}, setting brand for device: {}",
                        deviceDTO.getBrandName(), deviceToUpdate.toString());
                setBrand(deviceToUpdate, deviceDTO.getBrandName());
            }
            log.info("Partially updating device with id: {}, with the following properties: {}", id, deviceToUpdate.toString());
            return Optional.of(deviceDTOMapper.toDeviceDTO(deviceRepository.save(deviceToUpdate)));
        } else {
            log.info("Partially updating device with id: {} failed as no device has been found with this id.", id);
            return Optional.empty();
        }
    }

    private void setBrand(Device device, String brandName) {
        log.info("Seeing if brand {} already exists for device {}.", brandName, device.toString());
        Optional<Brand> brand = brandService.findById(brandName);
        brand.ifPresentOrElse(device::setBrand,
                () -> {
                    device.setBrand(Brand.builder()
                            .name(brandName)
                            .build()
                    );
                    log.info("New brand: {} has been created for device {}.", brandName, device.toString());
                }
        );
    }
}
