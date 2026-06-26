# Stage 1...........................................................................................................................................................................................................................

## Notification System Overview

The Campus Notification System provides real-time notifications to students regarding:

* Placements
* Results
* Events

The system supports creating, viewing, updating, and deleting notifications and also supports real-time delivery.

---

# Authentication

All APIs are protected using JWT authentication.

### Headers

```http
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

---

# Notification Schema

```json
{
  "id": "uuid",
  "studentId": 1042,
  "type": "PLACEMENT",
  "title": "Placement Update",
  "message": "Google hiring drive has started.",
  "isRead": false,
  "createdAt": "2026-06-26T18:30:00Z"
}
```

---

# Notification Types

```text
PLACEMENT
RESULT
EVENT
```

---

# 1. Create Notification

### Endpoint

```http
POST /api/v1/notifications
```

### Request

```json
{
  "studentId": 1042,
  "type": "PLACEMENT",
  "title": "Placement Update",
  "message": "Google hiring drive has started."
}
```

### Response

```json
{
  "id": "a123-b456-c789",
  "message": "Notification created successfully"
}
```

### Status Codes

```text
201 CREATED
400 BAD REQUEST
401 UNAUTHORIZED
500 INTERNAL SERVER ERROR
```

---

# 2. Get All Notifications

### Endpoint

```http
GET /api/v1/notifications
```

### Response

```json
{
  "notifications": [
    {
      "id": "a123",
      "studentId": 1042,
      "type": "PLACEMENT",
      "title": "Placement Update",
      "message": "Google hiring drive has started.",
      "isRead": false,
      "createdAt": "2026-06-26T18:30:00Z"
    }
  ]
}
```

---

# 3. Get Notification By Id

### Endpoint

```http
GET /api/v1/notifications/{id}
```

### Response

```json
{
  "id": "a123",
  "studentId": 1042,
  "type": "EVENT",
  "title": "Tech Fest",
  "message": "Tech Fest starts tomorrow.",
  "isRead": false,
  "createdAt": "2026-06-26T18:30:00Z"
}
```

---

# 4. Get Unread Notifications

### Endpoint

```http
GET /api/v1/notifications/unread
```

### Response

```json
{
  "notifications": [
    {
      "id": "a123",
      "studentId": 1042,
      "type": "RESULT",
      "title": "Mid Sem Result",
      "message": "Mid sem results are published.",
      "isRead": false,
      "createdAt": "2026-06-26T18:30:00Z"
    }
  ]
}
```

---

# 5. Mark Notification As Read

### Endpoint

```http
PATCH /api/v1/notifications/{id}/read
```

### Response

```json
{
  "message": "Notification marked as read"
}
```

---

# 6. Mark All Notifications As Read

### Endpoint

```http
PATCH /api/v1/notifications/read-all
```

### Response

```json
{
  "message": "All notifications marked as read"
}
```

---

# 7. Delete Notification

### Endpoint

```http
DELETE /api/v1/notifications/{id}
```

### Response

```json
{
  "message": "Notification deleted successfully"
}
```

---

# Error Response Format

```json
{
  "timestamp": "2026-06-26T18:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Notification not found"
}
```

---

# Real-Time Notification Mechanism

The system will use **WebSockets** to deliver notifications instantly to connected users.

### WebSocket Endpoint

```http
ws://localhost:8080/ws/notifications
```

### Workflow

```text
Notification Created
        ↓
Persisted to Database
        ↓
Published to WebSocket Server
        ↓
Delivered instantly to connected students
```

### WebSocket Message Format

```json
{
  "id": "a123",
  "type": "PLACEMENT",
  "title": "Placement Update",
  "message": "Google hiring drive has started.",
  "createdAt": "2026-06-26T18:30:00Z"
}
```

---

# Advantages of WebSockets

* Real-time notification delivery.
* Eliminates continuous polling.
* Reduces server load.
* Better user experience.
* Easily scalable with Kafka or RabbitMQ.

---

# Stage 2............................................................................................................................................................................................................................

## Database Selection

I would use **PostgreSQL** as the primary database.

### Reasons

* Strong ACID compliance.
* Excellent indexing support.
* Handles large datasets efficiently.
* Supports JSON fields if required.
* Good support for replication and partitioning.

---

# Database Schema

## Students Table

```sql
CREATE TABLE students (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100)
);
```

---

## Notifications Table

```sql
CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    student_id BIGINT NOT NULL,
    notification_type VARCHAR(20) NOT NULL,
    title VARCHAR(200),
    message TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_student
    FOREIGN KEY(student_id)
    REFERENCES students(id)
);
```

---

# Entity Relationship

```text
Student (1)
      |
      |----< Notification (Many)
```

One student can have many notifications.

---

# Recommended Indexes

```sql
CREATE INDEX idx_notification_student
ON notifications(student_id);

CREATE INDEX idx_notification_created
ON notifications(created_at);

CREATE INDEX idx_notification_type
ON notifications(notification_type);

CREATE INDEX idx_notification_unread
ON notifications(student_id, is_read);
```

---

# Queries For Stage 1 APIs

## Create Notification

```sql
INSERT INTO notifications(
id,
student_id,
notification_type,
title,
message,
is_read,
created_at
)
VALUES(
?,
?,
?,
?,
?,
false,
NOW()
);
```

---

## Get All Notifications

```sql
SELECT *
FROM notifications
WHERE student_id = ?
ORDER BY created_at DESC;
```

---

## Get Notification By Id

```sql
SELECT *
FROM notifications
WHERE id = ?;
```

---

## Get Unread Notifications

```sql
SELECT *
FROM notifications
WHERE student_id = ?
AND is_read = false
ORDER BY created_at DESC;
```

---

## Mark Notification As Read

```sql
UPDATE notifications
SET is_read = true
WHERE id = ?;
```

---

## Mark All Notifications As Read

```sql
UPDATE notifications
SET is_read = true
WHERE student_id = ?;
```

---

## Delete Notification

```sql
DELETE
FROM notifications
WHERE id = ?;
```

---

# Problems As Data Volume Increases

Suppose:

* 50,000 students
* 5,000,000 notifications

Possible problems:

### 1. Slow Queries

Fetching unread notifications may become slow.

### 2. High Database Load

Thousands of users may open notifications simultaneously.

### 3. Large Table Size

The notifications table can grow to millions of rows.

### 4. Increased Disk Usage

Indexes and records consume significant storage.

### 5. Long Backup and Restore Time

Database maintenance becomes expensive.

---

# Solutions

## Add Proper Indexes

Index frequently searched columns:

* student_id
* is_read
* created_at

---

## Pagination

Instead of:

```sql
SELECT *
FROM notifications;
```

Use:

```sql
SELECT *
FROM notifications
WHERE student_id = ?
ORDER BY created_at DESC
LIMIT 20 OFFSET 0;
```

---

## Table Partitioning

Partition notifications by:

* Month
* Year

Example:

```text
notifications_2026_01
notifications_2026_02
notifications_2026_03
```

---

## Read Replicas

Use read replicas for:

* Fetching notifications
* Reporting queries

---

## Caching

Store frequently accessed notifications in Redis.

---

## Archive Old Notifications

Move very old notifications to an archive table.

---

# Final Architecture

```text
Application
      |
      |
   PostgreSQL
      |
      +---- Read Replica
      |
      +---- Redis Cache
      |
      +---- Archive Storage
```


# Stage 3...................................................................................................................................................................................................................

## Query Optimization

### Existing Query

```sql
SELECT *
FROM notifications
WHERE student_id = ?
AND is_read = false
ORDER BY created_at DESC;
```

---

# Why is this query becoming slow?

As the number of notifications grows into millions of records, the database may perform a full table scan to locate the required rows.

The query filters on:

* student_id
* is_read

and then sorts on:

* created_at

Without proper indexing, the database must:

1. Scan a large portion of the table.
2. Filter unread notifications.
3. Sort the results.

This significantly increases query execution time.

---

# Optimization Strategy

## Composite Index

Create a composite index on the columns used in filtering and sorting.

```sql
CREATE INDEX idx_notifications_student_read_created
ON notifications(student_id, is_read, created_at DESC);
```

### Why this helps

The database can:

1. Directly locate notifications of a specific student.
2. Filter only unread notifications.
3. Return rows already sorted by created_at.

This avoids:

* Full table scans
* Expensive sorting operations

---

# Optimized Query

```sql
SELECT *
FROM notifications
WHERE student_id = ?
AND is_read = false
ORDER BY created_at DESC
LIMIT 20;
```

---

# Why Pagination is Important

A student may have thousands of notifications.

Fetching all notifications:

```sql
SELECT *
FROM notifications
WHERE student_id = ?;
```

is inefficient.

Instead:

```sql
SELECT *
FROM notifications
WHERE student_id = ?
ORDER BY created_at DESC
LIMIT 20 OFFSET 0;
```

Benefits:

* Lower memory usage
* Faster response time
* Better user experience

---

# Why Not Index Every Column?

Creating indexes on every column is a bad practice because:

1. Increased storage usage.
2. Slower INSERT operations.
3. Slower UPDATE operations.
4. Slower DELETE operations.
5. Additional maintenance overhead.

Indexes should only be created on frequently queried columns.

---

# Additional Recommended Indexes

```sql
CREATE INDEX idx_notifications_student
ON notifications(student_id);

CREATE INDEX idx_notifications_type
ON notifications(notification_type);

CREATE INDEX idx_notifications_created
ON notifications(created_at);
```

---

# SQL Query

Find all unread placement notifications received by a student in the last 7 days.

```sql
SELECT *
FROM notifications
WHERE student_id = ?
AND notification_type = 'PLACEMENT'
AND is_read = false
AND created_at >= NOW() - INTERVAL '7 days'
ORDER BY created_at DESC;
```

---

# Recommended Index For This Query

```sql
CREATE INDEX idx_notifications_placement
ON notifications(
student_id,
notification_type,
is_read,
created_at DESC
);
```

---

# Final Execution Flow

```text
Request
   ↓
Composite Index Lookup
   ↓
Filter Matching Rows
   ↓
Already Sorted Results
   ↓
Return Paginated Response
```

This reduces query execution time significantly and allows the notification system to scale efficiently with millions of records.


# Stage 4...................................................................................................................................................................................................................

## Scaling the Notification System

As the number of students and notifications increases significantly, the system should be designed to scale efficiently while maintaining low latency and high availability.

---

# Challenges

Suppose:

* 1,00,000+ students
* Millions of notifications
* Thousands of concurrent users

Potential issues:

1. High database load.
2. Slow query execution.
3. Increased API response time.
4. Large number of simultaneous notification deliveries.
5. High memory consumption.

---

# Horizontal Scaling

Instead of running a single server:

```text
Client
   |
Application Server
   |
Database
```

Use:

```text
                  Load Balancer
                         |
        -----------------------------------
        |                |                |
    App Server 1     App Server 2     App Server 3
                         |
                    PostgreSQL
```

Benefits:

* Better fault tolerance.
* Higher throughput.
* Easy to add more servers.

---

# Database Read Replicas

Most operations are read-heavy.

Use:

```text
                Primary Database
                        |
            -------------------------
            |                       |
      Read Replica 1          Read Replica 2
```

Write operations:

* Create Notification
* Mark As Read
* Delete Notification

Read operations:

* Get Notifications
* Get Unread Notifications

Benefits:

* Reduced load on primary database.
* Faster response times.

---

# Redis Caching

Frequently accessed notifications can be cached.

```text
Client
   |
Application
   |
 Redis Cache
   |
PostgreSQL
```

Benefits:

* Faster reads.
* Reduced database load.
* Lower latency.

---

# Pagination

Bad approach:

```sql
SELECT *
FROM notifications
WHERE student_id = ?;
```

Optimized approach:

```sql
SELECT *
FROM notifications
WHERE student_id = ?
ORDER BY created_at DESC
LIMIT 20 OFFSET 0;
```

Benefits:

* Faster response.
* Lower memory usage.
* Better user experience.

---

# Database Partitioning

The notifications table may contain millions of rows.

Partition by month:

```text
notifications_2026_01
notifications_2026_02
notifications_2026_03
```

Benefits:

* Faster queries.
* Easier maintenance.
* Faster backups.

---

# Asynchronous Notification Processing

Notification creation should not directly send notifications.

Instead:

```text
Create Notification
          |
          ↓
     Message Queue
          |
          ↓
 Notification Workers
          |
          ↓
   WebSocket Delivery
```

Benefits:

* Better scalability.
* Reduced API response time.
* Handles traffic spikes.

---

# Real-Time Notification Delivery

Use WebSockets.

```text
Student Device
        |
   WebSocket Server
        |
Notification Service
```

Benefits:

* Instant delivery.
* No polling required.
* Reduced network overhead.

---

# Monitoring

Use:

* Prometheus
* Grafana
* ELK Stack

Monitor:

* API response times
* Error rates
* Database usage
* Queue size
* Memory usage

---

# Final Scalable Architecture

```text
                        Load Balancer
                               |
        ------------------------------------------------
        |                      |                       |
   App Server 1          App Server 2           App Server 3
        |                      |                       |
        ---------------- Notification Service ---------
                               |
                        Redis Cache
                               |
                       PostgreSQL Primary
                               |
                  -------------------------
                  |                       |
            Read Replica 1         Read Replica 2
                               |
                         Message Queue
                               |
                      Notification Workers
                               |
                        WebSocket Server
                               |
                            Students
```

This architecture provides high availability, scalability, and fault tolerance for handling millions of notifications.

---

# Stage 5...................................................................................................................................................................................................................

## Processing Large Volumes of Notifications

The current pseudocode sends notifications synchronously.

Example:

```text
For each student
      |
      ↓
Send Notification
      |
      ↓
Wait for completion
      |
      ↓
Move to next student
```

This approach does not scale.

---

# Problems

1. Slow processing.
2. High response time.
3. Failure of one notification affects others.
4. Cannot handle traffic spikes.
5. Difficult to retry failed notifications.

---

# Improved Design

Use asynchronous processing.

```text
Notification Request
          |
          ↓
     Message Queue
          |
          ↓
     Worker Services
          |
          ↓
 Notification Delivery
```

---

# Message Queue

Recommended technologies:

* Apache Kafka
* RabbitMQ
* AWS SQS

Benefits:

* Decouples services.
* Handles spikes efficiently.
* Provides reliability.

---

# Worker Services

Multiple workers process notifications independently.

```text
Queue
  |
--------------------------------
|              |              |
Worker 1     Worker 2      Worker 3
```

Benefits:

* Parallel processing.
* High throughput.
* Easy scaling.

---

# Retry Mechanism

If notification delivery fails:

```text
Attempt 1
     ↓
Attempt 2
     ↓
Attempt 3
     ↓
Move to Dead Letter Queue
```

Benefits:

* Handles temporary failures.
* Prevents data loss.

---

# Dead Letter Queue (DLQ)

Failed notifications are moved to a separate queue.

Benefits:

* Easy debugging.
* Prevents blocking the system.
* Supports reprocessing.

---

# Updated Pseudocode

```text
Create Notification
          |
          ↓
Publish Event To Queue
          |
          ↓
Worker Picks Message
          |
          ↓
Send Notification
          |
      Success ?
       /      \
     Yes      No
      |         |
      ↓         ↓
   Complete   Retry
                  |
                  ↓
                DLQ
```

---

# Monitoring

Track:

* Queue length
* Failed deliveries
* Retry count
* Processing latency
* Worker health

---

# Final Architecture

```text
Application
      |
      ↓
 Message Queue
      |
------------------------------
|             |              |
Worker 1    Worker 2      Worker 3
      |
 Notification Service
      |
 WebSocket Delivery
      |
    Students
```

This design can efficiently process millions of notifications while maintaining reliability and high throughput.
