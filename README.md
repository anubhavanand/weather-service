# Weather Metrics Service

A Spring Boot REST API for ingesting weather sensor readings and querying aggregated metrics (min, max, sum, avg) over date ranges.

## Features
- Ingest sensor metric readings (Temperature, Humidity, Wind Speed)
- Query aggregated metrics by sensor, metric type, statistic type, and date range
- Built with Spring Boot, JPA, and OpenAPI documentation
- In-memory caching for query performance for latest metrics.

## Assumptions
- When querying metrics for multiple sensor IDs with a specific statistic, the response returns values grouped by `sensorId` and `metricType`.  
For example, requesting the average temperature for sensors 1 and 2 returns separate average values for each sensor.
- The application is designed for read-heavy usage, especially for fetching the latest metrics. To optimize this, a read-through cache is implemented for latest metric queries.
- The application uses H2 in-memory database. Data is not persisted across application restarts.

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- (Optional) curl or any HTTP client for testing

## How to Run the Application

### 1. Clone the repository
```bash
git clone <repository-url>
cd weather-service
```

### 2. Build the application
```bash
# Using Maven Wrapper (recommended)
./mvnw clean package

# Or using installed Maven
mvn clean package
```

### 3. Run the application
```bash
# Using Maven Wrapper
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

### 4. Access API Documentation
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Swagger UI: http://localhost:8080/swagger-ui.html

## API Endpoints

### 1. Ingest Sensor Readings
**Endpoint:** `POST /v1/sensor/ingest-readings/{sensorId}`

**Description:** Ingest one or more metric readings for a sensor.

**Sample Request:**
```bash
curl -X POST http://localhost:8080/v1/sensor/ingest-readings/1 \
  -H "Content-Type: application/json" \
  -d '{
    "metricReadings": [
      {
        "metricType": "TEMPERATURE",
        "metricValue": 23.5
      },
      {
        "metricType": "HUMIDITY",
        "metricValue": 65.2
      },
      {
        "metricType": "WIND_SPEED",
        "metricValue": 12.8
      }
    ]
  }'
```

**Sample Response:**
```json
{
  "sensorId": 1,
  "sensorResponse": [
    {
      "id": 1,
      "metricType": "TEMPERATURE",
      "metricsValue": 23.5,
      "timestamp": "2026-06-14T10:15:00Z"
    },
    {
      "id": 2,
      "metricType": "HUMIDITY",
      "metricsValue": 65.2,
      "timestamp": "2026-06-14T10:15:00Z"
    },
    {
      "id": 3,
      "metricType": "WIND_SPEED",
      "metricsValue": 12.8,
      "timestamp": "2026-06-14T10:15:00Z"
    }
  ]
}
```

### 2. Query Aggregated Metrics
**Endpoint:** `POST /v1/weather/metrics`

**Description:** Query aggregated sensor metrics (MIN, MAX, SUM, AVG) over a date range.

**Sample Request:**
```bash
curl -X POST http://localhost:8080/v1/weather/metrics \
  -H "Content-Type: application/json" \
  -d '{
    "sensorIds": [1, 2],
    "metricsTypes": ["TEMPERATURE", "HUMIDITY"],
    "statisticType": "AVG",
    "startDate": "2026-06-01",
    "endDate": "2026-06-14"
  }'
```

**Sample Response:**
```json
[
  {
    "sensorId": 1,
    "metricsType": "TEMPERATURE",
    "metricsValue": 22.4
  },
  {
    "sensorId": 1,
    "metricsType": "HUMIDITY",
    "metricsValue": 58.7
  },
  {
    "sensorId": 2,
    "metricsType": "TEMPERATURE",
    "metricsValue": 19.8
  },
  {
    "sensorId": 2,
    "metricsType": "HUMIDITY",
    "metricsValue": 72.1
  }
]
```

**Description:** Query latest sensor metrics.

**Sample Request:**
```bash
curl --location 'http://localhost:8080/v1/weather/metrics' \
--header 'Content-Type: application/json' \
--data '{
    "sensorIds": [1,2],
    "metricsTypes": ["TEMPERATURE", "HUMIDITY", "WIND_SPEED"]
}'
```

**Sample Response:**
```json
[
  {
    "sensorId": 1,
    "metricsType": "TEMPERATURE",
    "metricsValue": 30.8
  },
  {
    "sensorId": 1,
    "metricsType": "HUMIDITY",
    "metricsValue": 32.9
  },
  {
    "sensorId": 1,
    "metricsType": "WIND_SPEED",
    "metricsValue": 35.0
  },
  {
    "sensorId": 2,
    "metricsType": "TEMPERATURE",
    "metricsValue": 20.8
  },
  {
    "sensorId": 2,
    "metricsType": "HUMIDITY",
    "metricsValue": 22.9
  },
  {
    "sensorId": 2,
    "metricsType": "WIND_SPEED",
    "metricsValue": 25.0
  }
]
```

## Metric Types
- `TEMPERATURE`
- `HUMIDITY`
- `WIND_SPEED`

## Statistic Types
- `MIN`
- `MAX`
- `SUM`
- `AVG`

## Error Handling
The API returns appropriate error responses for invalid requests (HTTP 400) with descriptive messages.

## Development
- Run tests: `./mvnw test`

## Project Structure
```
src/main/java/com/assignment/weather/
├── controller/          # REST controllers
├── service/             # Business logic
├── repository/          # Data access
├── dto/                 # Request/Response models
├── entity/              # JPA entities
├── model/               # Enums and constants
├── validation/          # Custom validators
├── exception/           # Global exception handling
├── config/              # Configuration classes
└── WeatherServiceApplication.java
```

---