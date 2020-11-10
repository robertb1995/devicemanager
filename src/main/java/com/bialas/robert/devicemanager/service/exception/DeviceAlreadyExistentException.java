package com.bialas.robert.devicemanager.service.exception;

import lombok.Data;

@Data
public class DeviceAlreadyExistentException extends RuntimeException {

    public DeviceAlreadyExistentException(String deviceName, String brandName) {
        super(String.format("The device with name %s and brand %s is already present in the database.", deviceName, brandName));
    }
}
