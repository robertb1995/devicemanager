package com.bialas.robert.devicemanager.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DeviceDTO {

    private Long id;

    private String deviceName;

    private String brandName;

    private ZonedDateTime creationTime;

}
