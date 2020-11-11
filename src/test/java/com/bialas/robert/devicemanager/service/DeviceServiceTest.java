package com.bialas.robert.devicemanager.service;

import com.bialas.robert.devicemanager.domain.dto.DeviceDTO;
import com.bialas.robert.devicemanager.domain.entity.Brand;
import com.bialas.robert.devicemanager.domain.entity.Device;
import com.bialas.robert.devicemanager.mapper.DeviceDTOMapper;
import com.bialas.robert.devicemanager.mapper.DeviceDTOMapperImpl;
import com.bialas.robert.devicemanager.repository.DeviceRepository;
import com.bialas.robert.devicemanager.service.exception.DeviceAlreadyExistentException;
import com.bialas.robert.devicemanager.service.exception.DeviceNotFoundException;
import com.bialas.robert.devicemanager.util.BrandTestUtils;
import com.bialas.robert.devicemanager.util.DeviceTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.EmptyResultDataAccessException;

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
import static org.mockito.Mockito.*;

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
    public void givenExistentDeviceId_whenFindByIdCalled_returnOptionalOfDeviceDTO() {
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

    @Test
    public void givenExistentDeviceId_whenDeleteByIdCalled_verifyDeleteRepositoryIsCalledOnceForGivenId() {
        //given
        Long existentDeviceId = 1L;

        //when
        deviceService.deleteById(existentDeviceId);

        //then
        verify(deviceRepository).deleteById(existentDeviceId);
    }

    @Test
    public void givenInexistentDeviceId_whenDeleteByIdCalled_throwDeviceNotFoundExceptionWithAppropriateMessage() {
        //given
        Long inexistentDeviceId = 1000L;
        String message = "Entity with id 1000L hasn't been found!";

        doThrow(new EmptyResultDataAccessException(message, 0)).when(deviceRepository).deleteById(inexistentDeviceId);

        //when and then
        DeviceNotFoundException deviceAlreadyExistentException = assertThrows(DeviceNotFoundException.class, () -> deviceService.deleteById(inexistentDeviceId));
        assertEquals(message, deviceAlreadyExistentException.getMessage());
    }

    @Test
    public void givenExistentBrandId_whenFindAllByBrand_returnListOfDevicesForGivenBrand() {
        //given
        String existentBrandName = "Mayo";
        Device expectedDevice = DeviceTestUtils.getExpectedDeviceAfterCreation();
        when(deviceRepository.findAllByBrandName(existentBrandName)).thenReturn(Collections.singletonList(expectedDevice));

        //when
        List<DeviceDTO> result = deviceService.findAllByBrand(existentBrandName);

        //then
        assertEquals(Collections.singletonList(deviceDTOMapper.toDeviceDTO(expectedDevice)), result);
    }

    @Test
    public void givenExistentIdAndDtoWithAllProperties_whenFullyUpdateDeviceCalled_returnOptionalOfUpdatedDevice() {
        //given
        Long existentId = 1L;
        String deviceNameToUpdate = "newName";
        String brandNameToUpdate = "newBrand";
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2005, 5, 5, 5, 5, 5, 5, ZoneId.of("UTC"));
        DeviceDTO newDeviceDTO = DeviceDTO.builder()
                .deviceName(deviceNameToUpdate)
                .brandName(brandNameToUpdate)
                .creationTime(zonedDateTime)
                .build();

        Device addedDevice = DeviceTestUtils.getExpectedDeviceAfterCreation();
        Device beforeUpdateDevice = Device.builder()
                .id(1L)
                .deviceName(deviceNameToUpdate)
                .brand(Brand.builder()
                        .name(brandNameToUpdate)
                        .build())
                .creationTime(zonedDateTime)
                .build();

        when(deviceRepository.findById(existentId)).thenReturn(Optional.of(addedDevice));
        when(brandService.findById(brandNameToUpdate)).thenReturn(Optional.empty());
        when(deviceRepository.save(beforeUpdateDevice)).thenReturn(beforeUpdateDevice);

        //when
        Optional<DeviceDTO> result = deviceService.fullyUpdateDevice(existentId, newDeviceDTO);

        //then
        newDeviceDTO.setId(addedDevice.getId());
        assertEquals(Optional.of(newDeviceDTO), result);
    }

    @Test
    public void givenNonExistentId_whenFullyUpdateDeviceCalled_returnEmptyOptional() {
        //given
        Long nonExistentId = 1000L;
        String deviceNameToUpdate = "newName";
        String brandNameToUpdate = "newBrand";
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2005, 5, 5, 5, 5, 5, 5, ZoneId.of("UTC"));
        DeviceDTO newDeviceDTO = DeviceDTO.builder()
                .deviceName(deviceNameToUpdate)
                .brandName(brandNameToUpdate)
                .creationTime(zonedDateTime)
                .build();

        when(deviceRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        //when
        Optional<DeviceDTO> result = deviceService.fullyUpdateDevice(nonExistentId, newDeviceDTO);

        //then
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void givenExistentIdAndDtoWithNewBrandNameAndDeviceName_whenPartiallyUpdateDeviceCalled_returnOptionalOfUpdatedDevice() {
        //given
        Long existentId = 1L;
        String deviceNameToUpdate = "newName";
        String brandNameToUpdate = "newBrand";

        DeviceDTO newDeviceDTO = DeviceDTO.builder()
                .deviceName(deviceNameToUpdate)
                .brandName(brandNameToUpdate)
                .build();

        Device addedDevice = DeviceTestUtils.getExpectedDeviceAfterCreation();
        Device beforeUpdateDevice = Device.builder()
                .id(1L)
                .deviceName(deviceNameToUpdate)
                .brand(Brand.builder()
                        .name(brandNameToUpdate)
                        .build())
                .creationTime(addedDevice.getCreationTime())
                .build();

        when(deviceRepository.findById(existentId)).thenReturn(Optional.of(addedDevice));
        when(brandService.findById(brandNameToUpdate)).thenReturn(Optional.empty());
        when(deviceRepository.save(beforeUpdateDevice)).thenReturn(beforeUpdateDevice);

        //when
        Optional<DeviceDTO> result = deviceService.partiallyUpdateDevice(existentId, newDeviceDTO);

        //then
        newDeviceDTO.setId(addedDevice.getId());
        newDeviceDTO.setCreationTime(addedDevice.getCreationTime());
        assertEquals(Optional.of(newDeviceDTO), result);
    }

    @Test
    public void givenNonExistentId_whenPartiallyUpdateDeviceCalled_returnEmptyOptional() {
        //given
        Long nonExistentId = 1000L;
        String deviceNameToUpdate = "newName";
        String brandNameToUpdate = "newBrand";

        DeviceDTO newDeviceDTO = DeviceDTO.builder()
                .deviceName(deviceNameToUpdate)
                .brandName(brandNameToUpdate)
                .build();

        when(deviceRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        //when
        Optional<DeviceDTO> result = deviceService.partiallyUpdateDevice(nonExistentId, newDeviceDTO);

        //then
        assertEquals(Optional.empty(), result);
    }

}
