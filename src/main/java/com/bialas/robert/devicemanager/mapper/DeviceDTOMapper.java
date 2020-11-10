package com.bialas.robert.devicemanager.mapper;

import com.bialas.robert.devicemanager.domain.dto.DeviceDTO;
import com.bialas.robert.devicemanager.domain.entity.Device;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DeviceDTOMapper {

    @Mappings({
            @Mapping(target = "brandName", expression = "java(device.getBrand().getName())")
    })
    DeviceDTO toDeviceDTO(Device device);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDeviceFromDTO(DeviceDTO deviceDTO, @MappingTarget Device device);

}
