package com.assignment.weather.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.assignment.weather.dto.IngestionResponse;
import com.assignment.weather.dto.SensorRequest;
import com.assignment.weather.dto.SensorResponse;
import com.assignment.weather.model.MetricType;
import com.assignment.weather.service.SensorReadingIngestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SensorControllerTest {

    @Mock
    private SensorReadingIngestionService ingestionService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        final SensorController controller = new SensorController(ingestionService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void ingestReadings_withInvalidRequest_shouldReturnBadRequest() throws Exception {
        Long sensorId = 1L;
        String invalidJson = "{\"metricReadings\": []}";

        mockMvc.perform(post("/v1/sensor/ingest-readings/{sensorId}", sensorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ingestReadings_withValidRequest_shouldReturnCreatedMetrics() throws Exception {
        Long sensorId = 1L;
        String validJson = """
                {
                  "metricReadings": [
                    {"metricType": "TEMPERATURE", "metricValue": 22.5}
                  ]
                }
                """;

        List<SensorResponse> responses = List.of(
                SensorResponse.builder()
                        .id(sensorId)
                        .metricType(MetricType.TEMPERATURE)
                        .metricsValue(22.5)
                        .timestamp(Instant.now())
                        .build()
        );

        when(ingestionService.ingestReadings(eq(sensorId), any(SensorRequest.class))).thenReturn(responses);

        mockMvc.perform(post("/v1/sensor/ingest-readings/{sensorId}", sensorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isOk());
    }
}