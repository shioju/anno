# anno-spring-boot-starter

A Spring Boot starter that lets you annotate controller endpoints with descriptions and automatically exposes that metadata via a REST API.

## What it does

1. You add `@EndpointDescription("...")` to any Spring MVC controller method
2. At startup, the starter scans all registered handler methods for the annotation
3. A new endpoint is automatically registered (default: `/anno/**`) that serves the collected metadata as JSON

## Quick start

### Add the dependency

```xml
<dependency>
    <groupId>io.github.anno</groupId>
    <artifactId>anno-spring-boot-starter</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### Annotate your endpoints

```java
@RestController
public class UserController {

    @GetMapping("/user")
    @EndpointDescription("Returns the current user profile")
    public User getUser() {
        // ...
    }

    @PostMapping("/user")
    @EndpointDescription("Creates a new user")
    public User createUser(@RequestBody User user) {
        // ...
    }
}
```

### Query the metadata

**Get metadata for a specific endpoint:**

```
GET /anno/user
```

```json
[
  {"method": "GET", "description": "Returns the current user profile"},
  {"method": "POST", "description": "Creates a new user"}
]
```

**List all annotated endpoints:**

```
GET /anno
```

```json
{
  "/user": [
    {"method": "GET", "description": "Returns the current user profile"},
    {"method": "POST", "description": "Creates a new user"}
  ]
}
```

Unannotated endpoints return `404` from the `/anno` path.

## Configuration

Add these to your `application.properties` or `application.yml`:

| Property | Default | Description |
|---|---|---|
| `anno.base-path` | `/anno` | URL prefix for the metadata endpoint |
| `anno.enabled` | `true` | Set to `false` to disable the feature entirely |

Example:

```yaml
anno:
  base-path: /api-docs
  enabled: true
```

With this config, metadata is served at `GET /api-docs/user` instead of `GET /anno/user`.

## How it works

The starter uses Spring Boot auto-configuration:

1. `AnnoAutoConfiguration` activates conditionally — only in servlet web applications and when `anno.enabled` is not `false`
2. `EndpointDescriptionCollector` is created as a bean, injected with Spring's `RequestMappingHandlerMapping`, and scans all handler methods for `@EndpointDescription` annotations at startup
3. `AnnoEndpointController` is registered with a `@GetMapping` on the configured base path, and serves metadata lookups from the collector's in-memory index

## Requirements

- Java 21+
- Spring Boot 3.x (servlet stack)

## Building

```bash
mvn clean install
```

## Running tests

```bash
mvn test
```

The test suite boots a minimal Spring application with a sample controller and verifies:
- `GET /anno/user` returns the correct metadata for an annotated endpoint
- `GET /anno` returns a list of all annotated endpoints
- `GET /anno/health` returns 404 for an unannotated endpoint
