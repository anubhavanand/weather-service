package com.assignment.weather.controller;

import com.assignment.weather.dto.MetricsRequest;
import com.assignment.weather.dto.MetricsResponse;
import com.assignment.weather.model.MetricType;
import com.assignment.weather.service.WeatherMetricsService;
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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WeatherMetricsControllerTest {

    @Mock
    private WeatherMetricsService metricsService;

    private WeatherMetricsController controller;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        controller = new WeatherMetricsController(metricsService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void getMetrics_shouldReturnOkResponse() {
        MetricsRequest request = new MetricsRequest();
        request.setSensorIds(List.of(1L));
        request.setMetricsTypes(List.of(MetricType.TEMPERATURE));

        List<MetricsResponse> expectedResponses = List.of(
                new MetricsResponse(1L, MetricType.TEMPERATURE, 22.5)
        );

        when(metricsService.getSensorMetrics(any(MetricsRequest.class))).thenReturn(expectedResponses);

        ResponseEntity<List<MetricsResponse>> response = controller.getMetrics(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(metricsService).getSensorMetrics(any(MetricsRequest.class));
    }

    @Test
    void getMetrics_withDateOlderThanOneMonth_shouldReturnBadRequest() throws Exception {
        String startDate = LocalDate.now().minusMonths(2).toString();
        String endDate = LocalDate.now().minusMonths(1).plusDays(1).toString();
        String requestJson = """
        {
          "sensorIds": [1],
          "metricsTypes": ["TEMPERATURE"],
          "startDate": "%s",
          "endDate": "%s"
        }
        """.formatted(startDate, endDate);

        mockMvc.perform(post("/v1/weather/metrics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}