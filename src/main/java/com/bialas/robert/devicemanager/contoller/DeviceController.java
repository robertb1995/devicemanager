package com.bialas.robert.devicemanager.contoller;

import com.bialas.robert.devicemanager.domain.dto.DeviceDTO;
import com.bialas.robert.devicemanager.service.DeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@Api(value="DeviceController")
@RestController
@RequestMapping("/device")
@AllArgsConstructor
@Slf4j
public class DeviceController {

    private final DeviceService deviceService;

    @ApiOperation(value="Add new device", response = DeviceDTO.class)
    @ResponseContract
    @PostMapping
    public ResponseEntity<DeviceDTO> addDevice(@RequestBody DeviceDTO device) {
        log.info("Received request to add new device: {}", device.toString());
        return new ResponseEntity<>(deviceService.addDevice(device), OK);
    }

    @ApiOperation(value="Get device by ID", response = DeviceDTO.class)
    @ResponseContract
    @GetMapping(path="/{id}")
    public ResponseEntity<DeviceDTO> getDevice(@PathVariable Long id) {
        log.info("Received request to get device by id: {}", id);
        Optional<DeviceDTO> device = deviceService.findById(id);

        if(device.isEmpty()) {
            log.info("Device with id: {}, has not been found.", id);
            return new ResponseEntity<>(NOT_FOUND);
        }

        return new ResponseEntity<>(device.get(), OK);
    }

    @ApiOperation(value="Get all devices", response = DeviceDTO.class, responseContainer = "List")
    @ResponseContract
    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        log.info("Received request to get all devices");
        return new ResponseEntity<>(deviceService.findAll(), OK);
    }

    @ApiOperation(value="Remove a device")
    @ResponseContract
    @DeleteMapping(path = "/{id}")
    public ResponseEntity removeDevice(@PathVariable Long id) {
        log.info("Received request to remove device with id: " + id);
        deviceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value="Get devices by brand", response = DeviceDTO.class, responseContainer = "List")
    @ResponseContract
    @GetMapping(params = "brand")
    public ResponseEntity<List<DeviceDTO>> getDevicesByBrand(@RequestParam String brand) {
        log.info("Received request to get devices by brand: " + brand);
        return new ResponseEntity<>(deviceService.findAllByBrand(brand), OK);
    }

    @ApiOperation(value="Fully update a device", response = DeviceDTO.class)
    @ResponseContract
    @PutMapping(path="/{id}")
    public ResponseEntity<DeviceDTO> fullyUpdateDevice(@PathVariable Long id, @RequestBody DeviceDTO deviceDTO) {
        log.info("Received request to fully update device by id: {}", id);

        Optional<DeviceDTO> updatedDevice = deviceService.fullyUpdateDevice(id, deviceDTO);

        if(updatedDevice.isEmpty()) {
            log.info("Unable to fully update device with id: {} since it has not been found.", id);
            return new ResponseEntity<>(NOT_FOUND);
        }

        return new ResponseEntity<>(updatedDevice.get(), OK);
    }

    @ApiOperation(value="Partially update a device", response = DeviceDTO.class)
    @ResponseContract
    @PatchMapping(path="/{id}")
    public ResponseEntity<DeviceDTO> partiallyUpdateDevice(@PathVariable Long id, @RequestBody DeviceDTO deviceDTO) {
        log.info("Received request to partially update device by id: {}", id);

        Optional<DeviceDTO> updatedDevice = deviceService.partiallyUpdateDevice(id, deviceDTO);

        if(updatedDevice.isEmpty()) {
            log.info("Unable to partially update device with id: {} since it has not been found.", id);
            return new ResponseEntity<>(NOT_FOUND);
        }

        return new ResponseEntity<>(updatedDevice.get(), OK);
    }

}
