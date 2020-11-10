package com.bialas.robert.devicemanager.controller;

import com.bialas.robert.devicemanager.contoller.DeviceController;
import com.bialas.robert.devicemanager.contoller.handlers.ErrorHandler;
import com.bialas.robert.devicemanager.domain.dto.DeviceDTO;
import com.bialas.robert.devicemanager.service.DeviceService;
import com.bialas.robert.devicemanager.service.exception.DeviceAlreadyExistentException;
import com.bialas.robert.devicemanager.service.exception.DeviceNotFoundException;
import com.bialas.robert.devicemanager.util.DeviceTestUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeviceControllerTest {

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private DeviceController deviceController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.deviceController)
                .setControllerAdvice(new ErrorHandler())
                .build();

        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void givenNotAddedDevice_whenAddDeviceEndpointCalled_returnNewAddedDeviceAndOkStatus() throws Exception {
        //given
        DeviceDTO requestDTO = DeviceDTO.builder()
                .deviceName("Alpha")
                .brandName("Mayo")
                .build();

        DeviceDTO expectedDevice = DeviceTestUtils.getExpectedDeviceDTO();

        when(deviceService.addDevice(requestDTO)).thenReturn(expectedDevice);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/device")
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON);

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        verifyOkStatusAndResponseBodySameAsExpected(mvcResult, expectedDevice);
    }

    @Test
    public void givenAddedDevice_whenAddDeviceEndpointCalled_returnConflictStatus() throws Exception {
        //given
        DeviceDTO requestDTO = DeviceDTO.builder()
                .deviceName("AlreadyAddedDevice")
                .brandName("AlreadyAddedBrand")
                .build();

        when(deviceService.addDevice(requestDTO)).thenThrow(new DeviceAlreadyExistentException(requestDTO.getDeviceName(), requestDTO.getBrandName()));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/device")
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON);

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.CONFLICT.value(), mvcResult.getResponse().getStatus());
        assertEquals("The device with name AlreadyAddedDevice and brand AlreadyAddedBrand is already present in the database.", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void givenExistentId_whenGetDeviceEndpointCalled_returnDeviceAndOkStatus() throws Exception {
        //given
        Long existentId = 1L;

        DeviceDTO expectedDevice = DeviceTestUtils.getExpectedDeviceDTO();

        when(deviceService.findById(existentId)).thenReturn(Optional.of(expectedDevice));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/device/" + existentId);

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        verifyOkStatusAndResponseBodySameAsExpected(mvcResult, expectedDevice);
    }

    @Test
    public void givenNonExistentId_whenGetDeviceEndpointCalled_returnNotFoundStatus() throws Exception {
        //given
        Long nonExistentId = 1000L;

        when(deviceService.findById(nonExistentId)).thenReturn(Optional.empty());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/device/" + nonExistentId);

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void whenGetDevicesEndpointCalled_returnListOfAllDevicesAndOkStatus() throws Exception {
        //given
        DeviceDTO expectedDevice = DeviceTestUtils.getExpectedDeviceDTO();

        when(deviceService.findAll()).thenReturn(DeviceTestUtils.getExpectedDeviceDTOs());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/device");

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        verifyOkStatusAndResponseBodySameAsExpected(mvcResult, DeviceTestUtils.getExpectedDeviceDTOs());
    }

    @Test
    public void givenExistentId_whenRemoveDeviceEndpointCalled_returnNoContentStatus() throws Exception {
        //given
        Long existentId = 1L;
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/device/" + existentId);

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.NO_CONTENT.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void givenNonExistentId_whenRemoveDeviceEndpointCalled_returnNotFoundStatus() throws Exception {
        //given
        Long nonExistentId = 1000L;
        String messageText = "Device with ID: " + nonExistentId + " not found.";

        doThrow(new DeviceNotFoundException(messageText)).when(deviceService).deleteById(nonExistentId);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/device/" + nonExistentId);

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
        assertEquals(messageText, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void givenExistentBrand_whenGetDevicesByBrandEndpointCalled_returnListOfDevicesWithGivenBrandNameAndOkStatus() throws Exception {
        //given
        String existentBrand = "Mayo";

        DeviceDTO expectedDevice = DeviceTestUtils.getExpectedDeviceDTO();

        when(deviceService.findAllByBrand(existentBrand)).thenReturn(Collections.singletonList(expectedDevice));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/device")
                .param("brand",existentBrand);

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        verifyOkStatusAndResponseBodySameAsExpected(mvcResult, Collections.singletonList(expectedDevice));
    }

    @Test
    public void givenNonExistentBrand_whenGetDevicesByBrandEndpointCalled_returnEmptyListAndOkStatus() throws Exception {
        //given
        String nonExistentBrand = "IDontExist";

        when(deviceService.findAllByBrand(nonExistentBrand)).thenReturn(Collections.emptyList());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/device")
                .param("brand",nonExistentBrand);

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals(objectMapper.writeValueAsString(Collections.emptyList()), mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void givenExistentIdAndDevicePropertiesToUpdate_whenFullyUpdateDeviceEndpointCalled_returnUpdatedDevicesAndOkStatus() throws Exception {
        //given
        Long existentId = 1L;

        DeviceDTO updateRequestDTO = DeviceDTO.builder()
                .deviceName("NewDeviceName")
                .brandName("NewBrandName")
                .creationTime(ZonedDateTime.of(2010, 5, 5, 5, 5, 5, 5, ZoneId.of("UTC")))
                .build();

        DeviceDTO expectedDevice = DeviceTestUtils.getExpectedFullyUpdatedDeviceDTO();

        when(deviceService.fullyUpdateDevice(existentId, updateRequestDTO)).thenReturn(Optional.of(expectedDevice));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/device/" + existentId)
                .content(objectMapper.writeValueAsString(updateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON);

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        verifyOkStatusAndResponseBodySameAsExpected(mvcResult, expectedDevice);
    }

    @Test
    public void givenNonExistentIdAndDevicePropertiesToUpdate_whenFullyUpdateDeviceEndpointCalled_returnNotFoundStatus() throws Exception {
        //given
        Long nonExistentId = 1000L;

        DeviceDTO updateRequestDTO = DeviceDTO.builder()
                .deviceName("NewDeviceName")
                .brandName("NewBrandName")
                .build();

        when(deviceService.fullyUpdateDevice(nonExistentId, updateRequestDTO)).thenReturn(Optional.empty());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/device/" + nonExistentId)
                .content(objectMapper.writeValueAsString(updateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON);

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void givenExistentIdAndDevicePropertiesToUpdate_whenPartialUpdateDeviceEndpointCalled_returnUpdatedDevicesAndOkStatus() throws Exception {
        //given
        Long existentId = 1L;

        DeviceDTO updateRequestDTO = DeviceDTO.builder()
                .deviceName("NewDeviceName")
                .brandName("NewBrandName")
                .build();

        DeviceDTO expectedDevice = DeviceTestUtils.getExpectedPartiallyUpdatedDeviceDTO();

        when(deviceService.partiallyUpdateDevice(existentId, updateRequestDTO)).thenReturn(Optional.of(expectedDevice));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/device/" + existentId)
                .content(objectMapper.writeValueAsString(updateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON);

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        verifyOkStatusAndResponseBodySameAsExpected(mvcResult, expectedDevice);
    }

    @Test
    public void givenNonExistentIdAndDevicePropertiesToUpdate_whenPartialUpdateDeviceEndpointCalled_returnNotFoundStatus() throws Exception {
        //given
        Long nonExistentId = 1000L;

        DeviceDTO updateRequestDTO = DeviceDTO.builder()
                .deviceName("NewDeviceName")
                .brandName("NewBrandName")
                .build();

        when(deviceService.partiallyUpdateDevice(nonExistentId, updateRequestDTO)).thenReturn(Optional.empty());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/device/" + nonExistentId)
                .content(objectMapper.writeValueAsString(updateRequestDTO))
                .contentType(MediaType.APPLICATION_JSON);

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    private <T> void verifyOkStatusAndResponseBodySameAsExpected(MvcResult mvcResult, T expectedResult) throws UnsupportedEncodingException, JsonProcessingException {
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals(objectMapper.writeValueAsString(expectedResult), mvcResult.getResponse().getContentAsString());
    }
}
