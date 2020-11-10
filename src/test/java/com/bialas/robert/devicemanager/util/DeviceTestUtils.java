package com.bialas.robert.devicemanager.util;

import com.bialas.robert.devicemanager.domain.dto.DeviceDTO;
import com.bialas.robert.devicemanager.domain.entity.Brand;
import com.bialas.robert.devicemanager.domain.entity.Device;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

public class DeviceTestUtils {

    public static DeviceDTO getExpectedDeviceDTO() {
        return DeviceDTO.builder()
                .id(1L)
                .deviceName("Alpha")
                .brandName("Mayo")
                .creationTime(ZonedDateTime.of(2005, 5, 5, 5, 5, 5, 5, ZoneId.of("UTC")))
                .build();
    }

    public static DeviceDTO getExpectedDeviceDTO(ZonedDateTime zonedDateTime) {
        return DeviceDTO.builder()
                .id(1L)
                .deviceName("Alpha")
                .brandName("Mayo")
                .creationTime(zonedDateTime)
                .build();
    }

    public static List<DeviceDTO> getExpectedDeviceDTOs() {
        return Arrays.asList(
                getExpectedDeviceDTO(),
                DeviceDTO.builder()
                        .id(1L)
                        .deviceName("Sigma")
                        .brandName("Delta")
                        .creationTime(ZonedDateTime.of(2005, 5, 5, 5, 5, 5, 5, ZoneId.of("UTC")))
                        .build()
        );
    }

    public static DeviceDTO getExpectedFullyUpdatedDeviceDTO() {
        return DeviceDTO.builder()
                .id(1L)
                .deviceName("NewDeviceName")
                .brandName("NewBrandName")
                .creationTime(ZonedDateTime.of(2010, 5, 5, 5, 5, 5, 5, ZoneId.of("UTC")))
                .build();
    }

    public static DeviceDTO getExpectedPartiallyUpdatedDeviceDTO() {
        return DeviceDTO.builder()
                .id(1L)
                .deviceName("NewDeviceName")
                .brandName("NewBrandName")
                .creationTime(ZonedDateTime.of(2005, 5, 5, 5, 5, 5, 5, ZoneId.of("UTC")))
                .build();
    }

    public static Device getExpectedDeviceBeforeCreation() {
        return Device.builder()
                .creationTime(ZonedDateTime.now(Clock.fixed(Instant.ofEpochSecond(1000), ZoneId.of("UTC"))))
                .brand(Brand.builder()
                        .name("Mayo")
                        .build())
                .deviceName("Alpha")
                .build();
    }

    public static Device getExpectedDeviceAfterCreation() {
        return Device.builder()
                .id(1L)
                .creationTime(ZonedDateTime.now(Clock.fixed(Instant.ofEpochSecond(1000), ZoneId.of("UTC"))))
                .brand(Brand.builder()
                        .name("Mayo")
                        .build())
                .deviceName("Alpha")
                .build();
    }

}
