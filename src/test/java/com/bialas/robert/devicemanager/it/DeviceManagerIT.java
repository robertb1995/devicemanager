package com.bialas.robert.devicemanager.it;

import com.bialas.robert.devicemanager.config.H2DatabaseConfig;
import com.bialas.robert.devicemanager.domain.dto.DeviceDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = H2DatabaseConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DeviceManagerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private Clock clock;

    @Before
    public void setup() {
        when(clock.instant()).thenReturn(Instant.ofEpochSecond(1000));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));
    }

    @Test
    public void addNewDevices_thenGetAllAddedDevices_thenDeleteADeviceAndCheckIfDeviceDisappearedByNotBeingAbleToFindItById() throws Exception {
        //add new devices
        DeviceDTO firstDevice = DeviceDTO.builder()
                .deviceName("Alpha")
                .brandName("Mayo")
                .build();

        DeviceDTO secondDevice = DeviceDTO.builder()
                .deviceName("Jones")
                .brandName("Jon")
                .build();

        mockMvc.perform(addANewDevice(firstDevice));
        mockMvc.perform(addANewDevice(secondDevice));

        //get all devices and verify if as expected
        MvcResult getAllDevicesResult = mockMvc.perform(getAllAddedDevices()).andReturn();
        assertEquals(HttpStatus.OK.value(), getAllDevicesResult.getResponse().getStatus());
        assertEquals("[" +
                "{\"id\":1,\"deviceName\":\"Alpha\",\"brandName\":\"Mayo\",\"creationTime\":\"1970-01-01T00:16:40Z\"}," +
                "{\"id\":2,\"deviceName\":\"Jones\",\"brandName\":\"Jon\",\"creationTime\":\"1970-01-01T00:16:40Z\"}" +
                "]", getAllDevicesResult.getResponse().getContentAsString()
        );

        //delete a device
        Long deviceToDeleteID = 1L;
        mockMvc.perform(deleteADevice(deviceToDeleteID));

        //try to fetch deleted device by id and expect 404
        MvcResult getDeviceResult = mockMvc.perform(getDeviceByID(deviceToDeleteID)).andReturn();
        assertEquals(HttpStatus.NOT_FOUND.value(), getDeviceResult.getResponse().getStatus());
    }

    @Test
    public void addNewDevices_partiallyUpdateAnExistingDeviceWithNewBrandName_thenGetDevicesByBrandName_thenFullyUpdateTheDevice_finallyFindTheUpdatedDeviceById() throws Exception {
        //add new devices
        DeviceDTO firstDevice = DeviceDTO.builder()
                .deviceName("Alpha")
                .brandName("Mayo")
                .build();

        DeviceDTO secondDevice = DeviceDTO.builder()
                .deviceName("Jones")
                .brandName("Jon")
                .build();

        mockMvc.perform(addANewDevice(firstDevice));
        mockMvc.perform(addANewDevice(secondDevice));

        //partially update a device
        DeviceDTO partialUpdateInformation = DeviceDTO.builder()
                .deviceName("Thomson")
                .brandName("Mayo")
                .build();

        mockMvc.perform(partiallyUpdateADevice(2L, partialUpdateInformation));

        //get devices by brand name
        MvcResult getDeviceByBrandNameResult = mockMvc.perform(getDevicesByBrandName("Mayo")).andReturn();
        assertEquals(HttpStatus.OK.value(), getDeviceByBrandNameResult.getResponse().getStatus());
        assertEquals("[" +
                "{\"id\":1,\"deviceName\":\"Alpha\",\"brandName\":\"Mayo\",\"creationTime\":\"1970-01-01T00:16:40Z\"}," +
                "{\"id\":2,\"deviceName\":\"Thomson\",\"brandName\":\"Mayo\",\"creationTime\":\"1970-01-01T00:16:40Z\"}" +
                "]", getDeviceByBrandNameResult.getResponse().getContentAsString()
        );

        //fully update a device
        ZonedDateTime updatedDate = ZonedDateTime.of(2020, 5, 5, 5, 5, 5, 5, ZoneId.of("UTC"));
        DeviceDTO fullUpdateInformation = DeviceDTO.builder()
                .deviceName("Jason")
                .brandName("Layson")
                .creationTime(updatedDate)
                .build();

        mockMvc.perform(fullyUpdateADevice(2L, fullUpdateInformation));

        MvcResult getFullyUpdatedDeviceById = mockMvc.perform(getDeviceByID(2L)).andReturn();
        assertEquals(HttpStatus.OK.value(), getDeviceByBrandNameResult.getResponse().getStatus());
        assertEquals("{\"id\":2,\"deviceName\":\"Jason\",\"brandName\":\"Layson\",\"creationTime\":\"2020-05-05T05:05:05.000000005Z\"}",
                getFullyUpdatedDeviceById.getResponse().getContentAsString()
        );
    }

    @Test
    public void tryToAddAnExistingDeviceToDatabase_returnConflictHttpStatus() throws Exception {
        //add new devices
        DeviceDTO firstDevice = DeviceDTO.builder()
                .deviceName("Alpha")
                .brandName("Mayo")
                .build();

        //add a duplicate device
        DeviceDTO duplicateDevice = DeviceDTO.builder()
                .deviceName("Alpha")
                .brandName("Mayo")
                .build();

        mockMvc.perform(addANewDevice(firstDevice));
        MvcResult existingDeviceAdditionResult = mockMvc.perform(addANewDevice(duplicateDevice)).andReturn();
        assertEquals(HttpStatus.CONFLICT.value(), existingDeviceAdditionResult.getResponse().getStatus());
        assertEquals("The device with name Alpha and brand Mayo is already present in the database.", existingDeviceAdditionResult.getResponse().getContentAsString());
    }

    private RequestBuilder getDevicesByBrandName(String brandName) {
        return MockMvcRequestBuilders.get("/device")
                .param("brand", brandName);
    }

    private RequestBuilder fullyUpdateADevice(Long id, DeviceDTO deviceDTO) throws JsonProcessingException {
        return MockMvcRequestBuilders.put("/device/" + id)
                .content(objectMapper.writeValueAsString(deviceDTO))
                .contentType(MediaType.APPLICATION_JSON);
    }

    private RequestBuilder partiallyUpdateADevice(Long id, DeviceDTO deviceDTO) throws JsonProcessingException {
        return MockMvcRequestBuilders.patch("/device/" + id)
                .content(objectMapper.writeValueAsString(deviceDTO))
                .contentType(MediaType.APPLICATION_JSON);
    }

    private RequestBuilder deleteADevice(Long id) {
        return MockMvcRequestBuilders.delete("/device/" + id);
    }

    private RequestBuilder getAllAddedDevices() {
        return MockMvcRequestBuilders.get("/device");
    }

    private RequestBuilder getDeviceByID(Long id) {
        return MockMvcRequestBuilders.get("/device/" + id);
    }

    private RequestBuilder addANewDevice(DeviceDTO requestDTO) throws JsonProcessingException {
        return MockMvcRequestBuilders.post("/device")
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON);
    }

}
