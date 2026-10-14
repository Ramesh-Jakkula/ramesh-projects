# Hibernate Envers and Delta Reporting POC

## 1. Project Overview

This project demonstrates Hibernate Envers auditing and custom delta reporting in a Spring Boot application.

- **Hibernate Envers:** Automatically maintains historical snapshots of audited entities whenever records are created, updated, or deleted through Hibernate.
- **Delta Reporting:** Compares audit revisions and generates a column-level report containing old and new values.

The application manages customer accounts and stores generated change details in a separate delta-report table.

## 2. Hibernate Envers

### What is Hibernate Envers?

Hibernate Envers is a Hibernate module that provides auditing for JPA entities. It maintains historical versions of entity data, allowing us to understand what changed and inspect previous values.

### Why do we need it?

Without auditing, the main database table generally contains only the latest values. Envers maintains historical snapshots, reducing the need to implement custom audit-recording logic.

**Example: Without Envers**

**CUSTOMER_ACCOUNT**

| ACCOUNT_ID | CUSTOMER_NAME | BALANCE | CITY |
|---:|---|---:|---|
| 1 | Ramesh | 30000 | Bengaluru |

Suppose the original values were:

- BALANCE = 25000
- CITY = Chennai

After updating the account, the current table contains only the updated values:

| ACCOUNT_ID | CUSTOMER_NAME | BALANCE | CITY |
|---:|---|---:|---|
| 1 | Ramesh | 30000 | Bengaluru |

The previous values are no longer available in the current table unless a separate history mechanism exists.

### How is it configured?

Add the Hibernate Envers dependency:

```xml
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-envers</artifactId>
</dependency>
```

Annotate the entity:

```java
@Entity
@Table(name = "CUSTOMER_ACCOUNT")
@Audited(withModifiedFlag = true)
public class CustomerAccount {
    // Entity fields
}
```

- `@Audited` enables auditing for the entity.
- `withModifiedFlag = true` enables modified flags for eligible audited fields.

The application also configures `store_data_at_delete: true` to retain entity data in delete audit records.

### Tables created by Envers

| Table | Description |
|---|---|
| `CUSTOMER_ACCOUNT_AUD` | Stores historical snapshots of the customer account entity, including entity values, revision information, and modified flags. |
| `REVINFO` | Stores revision metadata. `REV` uniquely identifies a revision, while `REVTSTMP` stores its timestamp in epoch milliseconds. |

The main `CUSTOMER_ACCOUNT` table stores the current account data. The `CUSTOMER_ACCOUNT_DELTA` table is created by our custom implementation, not by Envers.

### Important audit columns

- **`REV`:** Identifies the revision associated with an audit record.
- **`REVTYPE`:** Identifies the operation — `0` for INSERT, `1` for UPDATE, and `2` for DELETE.
- **`_MOD` columns:** Indicate whether the corresponding field was modified in a revision. For example, `BALANCE_MOD` indicates whether the balance changed.

### Excluding fields from auditing

Use `@NotAudited` to completely exclude a field from Envers history.

```java
@NotAudited
private String internalNote;
```

Use `@Audited(withModifiedFlag = false)` to retain the field in audit snapshots without generating its modified flag.

```java
@Audited(withModifiedFlag = false)
private String accountNumber;
```

In this POC, `accountNumber` and `createdAt` are audited without modified flags. As a result, the delta implementation skips these fields when processing updates.

### Advantages

- Automatically maintains entity history.
- Reduces manual auditing code.
- Provides revision numbers and operation types.
- Supports modified flags for identifying changed fields.
- Helps with troubleshooting, historical analysis, and audit investigations.

## 3. Delta Reporting

### What is Delta Reporting?

Delta reporting identifies the differences between two versions of a record. Instead of displaying the complete entity snapshot, it reports the individual columns that changed, along with their old and new values.

### Example

Suppose an account is updated:

| Column | Old value | New value |
|---|---|---|
| `BALANCE` | 25000 | 30000 |
| `CITY` | Chennai | Bengaluru |

The delta report stores one row per included column change.

### How it works in this POC

1. Reads audit records from `CUSTOMER_ACCOUNT_AUD` in revision order.
2. Tracks the previous audit row for each account.
3. Uses `REVTYPE` to identify INSERT, UPDATE, or DELETE operations.
4. Uses `_MOD` flags to identify modified columns during updates.
5. Captures old and new values according to the operation.
6. Retrieves the revision timestamp from `REVINFO`.
7. Saves the generated column-level changes into `CUSTOMER_ACCOUNT_DELTA`.

### Delta report table

The `CUSTOMER_ACCOUNT_DELTA` table is created from the custom `DeltaReport` entity.

| Field | Description |
|---|---|
| `ACCOUNT_ID` | Account identifier |
| `REV` | Envers revision number |
| `REVTYPE` | Envers operation type |
| `MODE_OF_OPERATION` | INSERT, UPDATE, or DELETE |
| `COLUMN_NAME` | Name of the included column |
| `OLD_COLUMN_VALUE` | Previous value |
| `NEW_COLUMN_VALUE` | Current value |
| `DML_TSTAMP` | Original revision timestamp |
| `CREATED_AT` | Time the delta report was generated |

### How INSERT, UPDATE, and DELETE are reported

- **INSERT:** Old value is `null`; new value contains the inserted value.
- **UPDATE:** Old value comes from the previous audit row; new value comes from the current audit row. Only columns with a corresponding true `_MOD` flag are included.
- **DELETE:** Old value comes from the delete audit row; new value is `null`.

## 4. Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 3.5.16 | Application framework |
| Spring Web | REST APIs |
| Spring Data JPA | Database access |
| Hibernate ORM | Object-relational mapping |
| Hibernate Envers | Auditing and revision history |
| H2 Database | In-memory database |
| Jakarta Bean Validation | Request validation |
| Spring Boot Actuator | Management endpoints |
| Springdoc OpenAPI | Swagger documentation |
| Lombok | Reduces boilerplate code |
| Maven | Dependency management and build |

## 5. REST API Endpoints

Base URL: `http://localhost:8085`

### Customer Account APIs

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/accounts` | Create an account |
| GET | `/api/accounts` | Retrieve all accounts |
| GET | `/api/accounts/{accountNumber}` | Retrieve an account |
| PUT | `/api/accounts/{accountNumber}` | Update an account |
| DELETE | `/api/accounts/{accountNumber}` | Delete an account |

### Delta Report APIs

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/delta-reports/generate` | Generate and save the delta report |
| GET | `/api/delta-reports` | Retrieve generated delta records |

## 6. Running the Application

**Prerequisites:** JDK 17 and Maven.

Run the application from the project directory:

```bash
mvn spring-boot:run
```

Useful URLs:

- **Swagger UI:** `http://localhost:8085/swagger-ui/index.html`
- **H2 Console:** `http://localhost:8085/h2-console`
- **Actuator Mappings:** `http://localhost:8085/actuator/mappings`

H2 connection details:

```text
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (blank)
```

Because the database is configured as in-memory H2, its data normally disappears when the application stops.

## 7. Summary

This POC demonstrates how Hibernate Envers maintains historical entity snapshots and how custom delta-reporting logic uses those snapshots to generate column-level change records.

Envers handles the audit history, while the application compares revisions and stores the resulting changes in `CUSTOMER_ACCOUNT_DELTA`.