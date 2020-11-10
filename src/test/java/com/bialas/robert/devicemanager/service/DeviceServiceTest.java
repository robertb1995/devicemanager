package com.bialas.robert.devicemanager.service;

import com.bialas.robert.devicemanager.domain.dto.DeviceDTO;
import com.bialas.robert.devicemanager.domain.entity.Device;
import com.bialas.robert.devicemanager.mapper.DeviceDTOMapper;
import com.bialas.robert.devicemanager.mapper.DeviceDTOMapperImpl;
import com.bialas.robert.devicemanager.repository.DeviceRepository;
import com.bialas.robert.devicemanager.service.exception.DeviceAlreadyExistentException;
import com.bialas.robert.devicemanager.util.BrandTestUtils;
import com.bialas.robert.devicemanager.util.DeviceTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private BrandService brandService;

    @Mock
    private Clock clock;

    private DeviceService deviceService;

    private DeviceDTOMapper deviceDTOMapper = new DeviceDTOMapperImpl();

    @Before
    public void setup() {
        deviceService = new DeviceService(deviceRepository, brandService, clock, deviceDTOMapper);
        when(clock.instant()).thenReturn(Instant.ofEpochSecond(1000));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
    }

    @Test
    public void givenNewDeviceDTOWithUnaddedBrand_whenAddDeviceCalled_returnCreatedDeviceDTO() {
        //given
        DeviceDTO newDeviceDTO = DeviceDTO.builder()
                .deviceName("Alpha")
                .brandName("Mayo")
                .build();

        Device deviceToAdd = DeviceTestUtils.getExpectedDeviceBeforeCreation();
        Device addedDevice = DeviceTestUtils.getExpectedDeviceAfterCreation();

        DeviceDTO expectedDeviceDTO = DeviceTestUtils.getExpectedDeviceDTO(ZonedDateTime.now(Clock.fixed(Instant.ofEpochSecond(1000), ZoneId.of("UTC"))));

        when(deviceRepository.findByDeviceNameAndBrand("Alpha", "Mayo")).thenReturn(Optional.empty());
        when(brandService.findById("Mayo")).thenReturn(Optional.empty());
        when(deviceRepository.save(deviceToAdd)).thenReturn(addedDevice);

        //when
        DeviceDTO result = deviceService.addDevice(newDeviceDTO);

        //then
        assertEquals(expectedDeviceDTO, result);
    }

    @Test
    public void givenNewDeviceDTOWithAddedBrand_whenAddDeviceCalled_returnCreatedDeviceDTO() {
        //given
        DeviceDTO newDeviceDTO = DeviceDTO.builder()
                .deviceName("Alpha")
                .brandName("Mayo")
                .build();

        Device deviceToAdd = DeviceTestUtils.getExpectedDeviceBeforeCreation();
        Device addedDevice = DeviceTestUtils.getExpectedDeviceAfterCreation();

        DeviceDTO expectedDeviceDTO = DeviceTestUtils.getExpectedDeviceDTO(ZonedDateTime.now(Clock.fixed(Instant.ofEpochSecond(1000), ZoneId.of("UTC"))));

        when(deviceRepository.findByDeviceNameAndBrand("Alpha", "Mayo")).thenReturn(Optional.empty());
        when(brandService.findById("Mayo")).thenReturn(Optional.of(BrandTestUtils.getExpectedBrand()));
        when(deviceRepository.save(deviceToAdd)).thenReturn(addedDevice);

        //when
        DeviceDTO result = deviceService.addDevice(newDeviceDTO);

        //then
        assertEquals(expectedDeviceDTO, result);
    }

    @Test
    public void givenExistentDeviceDTO_whenAddDeviceCalled_throwDeviceAlreadyExistentException() {
        //given
        DeviceDTO newDeviceDTO = DeviceDTO.builder()
                .deviceName("Alpha")
                .brandName("Mayo")
                .build();

        Device addedDevice = DeviceTestUtils.getExpectedDeviceAfterCreation();

        when(deviceRepository.findByDeviceNameAndBrand("Alpha", "Mayo")).thenReturn(Optional.of(addedDevice));

        //when and then
        DeviceAlreadyExistentException deviceAlreadyExistentException = assertThrows(DeviceAlreadyExistentException.class, () -> deviceService.addDevice(newDeviceDTO));
        assertEquals("The device with name Alpha and brand Mayo is already present in the database.", deviceAlreadyExistentException.getMessage());
    }

    @Test
    public void givenExistentDeviceId_whenFindByIdCalled_returnDeviceDTO() {
        //given
        Long existentDeviceId = 1L;
        Device addedDevice = DeviceTestUtils.getExpectedDeviceAfterCreation();

        when(deviceRepository.findById(existentDeviceId)).thenReturn(Optional.of(addedDevice));

        //when
        Optional<DeviceDTO> result = deviceService.findById(existentDeviceId);

        //then
        assertEquals(Optional.of(deviceDTOMapper.toDeviceDTO(addedDevice)), result);
    }

    @Test
    public void whenFindAllCalled_returnDeviceDTOList() {
        //given
        List<Device> devices = Collections.singletonList(DeviceTestUtils.getExpectedDeviceAfterCreation());
        when(deviceRepository.findAll()).thenReturn(devices);

        //when
        List<DeviceDTO> result = deviceService.findAll();

        //then
        assertEquals(devices.stream()
                .map(deviceDTOMapper::toDeviceDTO)
                .collect(Collectors.toList()), result);
    }

    /*
    todo (due to lack of time)
    delete by id test for exception thrown and for calling the right repo method
    partial update and full update tests (two each with checking to check when optional is empty and with result), the one with partial including the brandName to directly test the functionality
    find by brand test (just one - since will just return an empty list if none found)
    */

}
