# TED Talk API

A Spring Boot REST API for managing and analyzing TED Talks data with database persistence, influence analysis, and
asynchronous CSV import capabilities.

---

## Overview

This application provides a REST API for importing, managing, and analyzing TED Talks data.  
It supports CRUD operations, advanced filtering, speaker and talk influence analysis, and an asynchronous CSV import
mechanism with real-time status tracking.

The project is designed as an **assessment-quality MVP**, with explicit documentation of trade-offs and production
considerations.

---

## Features

### Core Functionality

- **RESTful API** for TED Talks management
- **Persistent storage** using H2 + JPA/Hibernate
- **Asynchronous CSV import** using Java 21 Virtual Threads
- **Import status tracking** with real-time progress
- **Advanced filtering** with combined AND logic (author, year, keyword)
- **Influence analysis** for speakers and talks
- **Year-over-year analysis** of most influential talks
- **Pagination & sorting** with configurable limits

---

## Requirements

- Java 21+
- Maven 3.8+

---

## Build & Run

### Build

```bash
mvn clean install
````

### Run

```bash
mvn spring-boot:run
```

Or:

```bash
java -jar target/tedtalks-api-1.0.0.jar
```

Application starts on:

```
http://localhost:8080
```

---

## API Documentation

* **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **OpenAPI JSON**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

---

## H2 Database Console

* **URL**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
* **JDBC URL**: `jdbc:h2:mem:tedtalksdb`
* **Username**: `sa`
* **Password**: *(empty)*

---

## API Endpoints

### TED Talks Management

| Method | Endpoint             | Description             |
|--------|----------------------|-------------------------|
| POST   | `/api/v1/talks`      | Create a TED Talk       |
| GET    | `/api/v1/talks`      | List talks with filters |
| GET    | `/api/v1/talks/{id}` | Get talk by ID          |
| PUT    | `/api/v1/talks/{id}` | Update talk             |
| DELETE | `/api/v1/talks/{id}` | Delete talk             |

### Filtering Example

```http
GET /api/v1/talks?author=John&year=2024&keyword=AI&page=0&size=20&sortBy=views&sortDirection=DESC
```

**Query parameters**

* `author` – partial, case-insensitive
* `year` – exact match
* `keyword` – searches title and author
* `page` – default `0`
* `size` – default `100`, max `100`
* `sortBy` – `id`, `title`, `author`, `views`, `likes`, `year`
* `sortDirection` – `ASC` | `DESC`

All filters are combined using **AND** logic.

---

### Influence Analysis

| Method | Endpoint                                  | Description       |
|--------|-------------------------------------------|-------------------|
| GET    | `/api/v1/influence/speakers?topN={n}`     | Top N speakers    |
| GET    | `/api/v1/influence/speaker?author={name}` | Speaker influence |
| GET    | `/api/v1/influence/talks?topN={n}`        | Top N talks       |
| GET    | `/api/v1/influence/talks/by-year`         | Top talk per year |

---

## CSV Import

### Import Endpoints

| Method | Endpoint                           | Description   |
|--------|------------------------------------|---------------|
| POST   | `/api/v1/import/csv`               | Upload CSV    |
| GET    | `/api/v1/import/status/{importId}` | Import status |

### Upload Example

```bash
curl -X POST http://localhost:8080/api/v1/import/csv \
  -F "file=@tedtalks.csv"
```

### Import Response

```json
{
  "importId": "uuid",
  "message": "CSV import started",
  "statusUrl": "/api/v1/import/status/{importId}"
}
```

### Status Values

* `PROCESSING`
* `COMPLETED`
* `FAILED`

---

## CSV Format

```csv
title,author,date,views,likes,link
Climate action needs new frontline leadership,Ozawa Bineshi Albert,December 2021,404000,12000,https://ted.com
```

Supported date formats:

* `January 2024`
* `Jan 2024`
* `2024-03`

---

## CSV Import Semantics

### Current Behavior (MVP)

* Import is **asynchronous**
* Records are processed in **batches**
* Invalid rows are skipped
* **No global rollback**
* If import fails:

    * Status becomes `FAILED`
    * Already persisted records remain in DB

This behavior is **intentional** to:

* avoid long-running transactions
* support large files
* improve resilience and observability

---

## Configuration

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB

tedtalks:
  csv:
    batch-size: 500             # Records per batch during import
  influence:
    views-weight: 0.7           # Weight for views (70%)
    likes-weight: 0.3           # Weight for likes (30%)
```

---

## Influence Formula

```
influence = (views × viewsWeight) + (likes × likesWeight)
```

---

## Error Handling

Consistent error responses:

```json
{
  "status": 404,
  "message": "TED Talk not found",
  "path": "/api/v1/talks/123"
}
```

Validation errors return HTTP `400`.

---

## Testing

```bash
mvn test
```

---

## Design Decisions & Assumptions

### Framework & Technology Choices

**Spring Boot Framework**

- Chosen for its comprehensive ecosystem and ease of data management
- Built-in dependency injection simplifies service layer architecture
- Spring Data JPA abstracts database operations, reducing boilerplate code
- Autoconfiguration minimizes setup complexity for an MVP

**H2 Database**

- Persistent storage enables easier data manipulation compared to in-memory collections
- Provides SQL querying capabilities for complex filtering and aggregation
- JPA/Hibernate integration allows for object-relational mapping
- Lightweight and embeddable, suitable for MVP; can be replaced with PostgreSQL/MySQL for production

---

### CSV Import Design

**Asynchronous Processing**

- Large CSV files (up to 10GB+) require non-blocking processing to avoid memory overload
- Async import prevents API timeout issues and improves user experience
- Background processing with status tracking allows users to continue other operations

**Batch Processing**

- Records are processed in configurable batches (default: 500) to optimize memory usage
- Reduces database round-trips while preventing out-of-memory errors
- Balances throughput and resource consumption

**Data Quality Assumptions**

- CSV is imported as-is without duplicate detection (assumes clean data source)
- Invalid/malformed records are skipped with warning logs (fail-safe approach)
- Import continues despite individual record failures (resilient processing)
- No data validation beyond basic field constraints (title length, non-negative numbers)

**Error Handling**

- Failed imports are marked with `FAILED` status for tracking
- Already-saved batches remain in the database (no automatic rollback)
- Invalid individual records are skipped with warnings; import continues
- Status tracking persisted separately to track the import lifecycle

---

### Influence Analysis

**Metric Definition**

- Influence calculated as a weighted sum: `(views × 0.7) + (likes × 0.3)`
- Views weighted higher (70%) as they represent broader reach
- Likes (30%) indicate deeper engagement
- Weights are configurable to allow experimentation with different formulas

**Speaker Aggregation**

- Speaker influence = sum of all their talks' influence scores
- Assumes speaker name uniqueness (no disambiguation for common names)
- Case-sensitive matching for speaker names

---

### Data Management

**Filtering Logic**

- Combined filters use AND logic for precise results
- Partial matching on text fields (case-insensitive) for better UX
- Pagination with configurable page size (max 100) prevents overwhelming responses
- Full scans are acceptable for assessment scope and should be optimized for production

**Date Handling**

- Supports multiple formats: "Month YYYY", "Mon YYYY", "YYYY-MM" (English locale)
- Stored as `YearMonth` for year-based queries and analysis
- No time zone considerations (dates only, no timestamps)

---

### API Design

**RESTful Principles**

- Standard HTTP methods (GET, POST, PUT, DELETE) for CRUD operations
- Resource-based URLs (`/api/v1/talks`, `/api/v1/influence`)
- Consistent error responses with appropriate HTTP status codes

**Versioning**

- API versioned at `/api/v1/` to support future changes without breaking clients

---

### Scope Limitations

**Out of Scope for MVP**

- Duplicate detection during CSV import (assumes data quality)
- User authentication/authorization (public API)
- Rate limiting or API throttling
- Data export functionality
- Advanced analytics (trends, growth metrics)
- Multi-language support (English only)

---
