package com.bialas.robert.devicemanager.mapper;

import com.bialas.robert.devicemanager.domain.dto.DeviceDTO;
import com.bialas.robert.devicemanager.domain.entity.Device;
import com.bialas.robert.devicemanager.util.DeviceTestUtils;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static junit.framework.TestCase.assertEquals;

public class DeviceDTOMapperTest {

    private DeviceDTOMapper deviceDTOMapper = new DeviceDTOMapperImpl();

    @Test
    public void givenDevice_whenToDeviceDtoCalled_returnDeviceDto() {
        //given
        Device device = DeviceTestUtils.getExpectedDeviceAfterCreation();

        //when
        DeviceDTO result = deviceDTOMapper.toDeviceDTO(device);

        //then
        assertEquals(DeviceTestUtils.getExpectedDeviceDTO(ZonedDateTime.now(Clock.fixed(Instant.ofEpochSecond(1000), ZoneId.of("UTC")))),result);
    }

    @Test
    public void givenNullDevice_whenToDeviceDtoCalled_returnNull() {
        //when
        DeviceDTO result = deviceDTOMapper.toDeviceDTO(null);

        //then
        assertEquals(null, result);
    }

    @Test
    public void givenDeviceDTO_whenUpdateDeviceFromDTOCalled_thenUpdateDevice() {
        //given
        Device device = DeviceTestUtils.getExpectedDeviceAfterCreation();
        Device expectedDevice = Device.builder()
                .id(device.getId())
                .deviceName("newName")
                .brand(device.getBrand())
                .creationTime(device.getCreationTime())
                .build();
        DeviceDTO deviceDTOToUpdateFrom = DeviceDTO.builder()
                .deviceName("newName")
                .build();

        //when
        deviceDTOMapper.updateDeviceFromDTO(deviceDTOToUpdateFrom, device);

        //then
        assertEquals(expectedDevice, device);
    }

    @Test
    public void givenNullDTO_whenUpdateDeviceFromDTOCalled_thenDoNothingToDevice() {
        Device device = DeviceTestUtils.getExpectedDeviceAfterCreation();
        Device expectedDevice = Device.builder()
                .id(device.getId())
                .deviceName(device.getDeviceName())
                .brand(device.getBrand())
                .creationTime(device.getCreationTime())
                .build();

        //when
        deviceDTOMapper.updateDeviceFromDTO(null, device);

        //then
        assertEquals(expectedDevice, device);
    }

}
