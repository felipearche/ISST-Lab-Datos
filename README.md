# ISST-Lab-Datos

A Spring Boot 3 REST API that demonstrates JPA-based data access patterns using an in-memory H2 database. Built as a lab exercise for the **ISST** (Ingeniería de Sistemas Software y de Telecomunicaciones) course at **UPM – DIT**.

The domain models a university department: professors, subjects, and the departments that organize them. The project covers derived query methods, JPQL, native SQL, DTO projections, bidirectional relationships, and JSON serialization.

---

## Tech Stack

| | |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.x |
| Spring Data JPA | (via Spring Boot starter) |
| Hibernate | (via Spring Data JPA) |
| H2 Database | in-memory, runtime only |
| Lombok | `@Data` for boilerplate-free entities |
| Build tool | Maven (wrapper included) |

---

## Domain Model

Three JPA entities with the following relationships:

```
Departamento ──(1:N)──► Profesor
Departamento ──(1:N)──► Asignatura
Profesor     ──(N:M)──► Asignatura   [join table: profesor_imparte_asignatura]
```

| Entity | Key fields |
|---|---|
| `Departamento` | `id`, `nombre`, `acronimo` |
| `Profesor` | `id`, `nrp`, `nombre`, `despacho`, `email`, `departamento` |
| `Asignatura` | `id`, `nombre`, `acronimo`, `curso`, `semestre`, `departamento` |

Bidirectional relationship sides are annotated with `@JsonIgnore` to prevent infinite recursion during JSON serialization.

---

## Project Structure

```
src/
└── main/
    ├── java/es/upm/dit/isst/lab_datos/
    │   ├── LabDatosApplication.java
    │   ├── domain/
    │   │   ├── Departamento.java
    │   │   ├── Asignatura.java
    │   │   ├── Profesor.java
    │   │   ├── DepartamentoRepository.java
    │   │   ├── AsignaturaRepository.java
    │   │   ├── ProfesorRepository.java        ← custom JPQL & native queries
    │   │   ├── ProfesorCargaDTO.java           ← record: nombre + totalAsignaturas
    │   │   └── DespachoCompartidoDTO.java      ← record: despacho + 2 profesores
    │   └── web/
    │       └── ProfesorController.java         ← REST endpoints
    └── resources/
        ├── application.properties
        └── import.sql                          ← seed data (loaded on startup)
```

---

## Getting Started

### Prerequisites

- Java 17 or later
- Nothing else — H2 is embedded and configured automatically.

### Run

```bash
./mvnw spring-boot:run
```

The application starts at `http://localhost:8080`.

### H2 Console

Once running, open the database browser at:

```
http://localhost:8080/h2-console
```

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:lab_datos` |
| Username | `sa` |
| Password | *(leave blank)* |

You should see the tables `DEPARTAMENTO`, `PROFESOR`, `ASIGNATURA`, and `PROFESOR_IMPARTE_ASIGNATURA` pre-loaded with seed data from `import.sql`.

---

## REST API

Base path: `/api/profesores`

### Standard endpoints

| Method | URL | Description |
|---|---|---|
| `GET` | `/api/profesores` | Return all professors |
| `GET` | `/api/profesores/nrp/{nrp}` | Find a professor by their NRP |
| `GET` | `/api/profesores/buscar?nombre=X` | Search professors by name (partial match) |

### Task endpoints

| Method | URL | Description |
|---|---|---|
| `GET` | `/api/profesores/carga-pesada` | Professors teaching more than 2 subjects |
| `GET` | `/api/profesores/despachos-compartidos` | Professors sharing the same office |

### Example responses

**`GET /api/profesores/carga-pesada`**

Returns professors with more than 2 assigned subjects, using a JPQL query with `GROUP BY / HAVING` and a DTO projection.

```json
[
  { "nombre": "Alejandro Alonso Muñoz", "totalAsignaturas": 4 },
  { "nombre": "Encarna Pastor", "totalAsignaturas": 3 }
]
```

**`GET /api/profesores/despachos-compartidos`**

Returns all pairs of professors who share an office, using a native SQL self-join on the `profesor` table.

```json
[
  {
    "despacho": "B202",
    "nombreProfesor1": "Juan Quemada Vives",
    "nombreProfesor2": "Monica Cortés Sack"
  },
  {
    "despacho": "B212",
    "nombreProfesor1": "Gabriel Huecas Fernández-Toribio",
    "nombreProfesor2": "Santiago Pavón Gómez"
  }
]
```

---

## Query Techniques Demonstrated

This project deliberately uses several different query styles side by side so they can be compared:

| Technique | Example |
|---|---|
| Derived query method | `findByNombreContaining(String nombre)` |
| Derived traversal across relations | `findByDepartamentoAcronimo(String acronimo)` |
| JPQL with `JOIN` | `SELECT p FROM Profesor p JOIN p.departamento d WHERE d.acronimo = :acronimo` |
| JPQL with `GROUP BY / HAVING` | Count professors per department above a threshold |
| JPQL with DTO constructor projection | `SELECT new ProfesorCargaDTO(p.nombre, COUNT(a)) ...` |
| Native SQL with self-join | Find professor pairs sharing the same `despacho` |

---

## Configuration

`src/main/resources/application.properties`:

```properties
spring.application.name=lab_datos

# H2 in-memory datasource
spring.datasource.url=jdbc:h2:mem:lab_datos
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# H2 browser console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Log generated SQL to console
spring.jpa.show-sql=true

# Schema is recreated from entities on every startup
spring.jpa.hibernate.ddl-auto=create
```

> **Note:** `ddl-auto=create` drops and recreates the schema on every startup. Seed data is reloaded from `import.sql` each time. This is intentional for a lab environment — do not use this setting in production.

---

## Seed Data

`import.sql` populates the database on startup with:

- **6 departments** (DIT, SSR, TFB, MAT, DIE, IES)
- **18 professors** across departments, with real office numbers and `@upm.es` emails
- **12 subjects** spanning 4 years and 2 semesters each
- Teaching assignments in the `profesor_imparte_asignatura` join table
