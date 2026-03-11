# anno-spring-boot-starter

A Spring Boot starter that lets you annotate controller endpoints with arbitrary key-value metadata and automatically exposes it via a REST API.

## What it does

1. You add `@EndpointDescription({"key=value", ...})` to any Spring MVC controller method
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

The annotation accepts an array of `"key=value"` strings, allowing you to attach any metadata you need:

```java
@RestController
public class UserController {

    @GetMapping("/user")
    @EndpointDescription({
        "summary=Returns the current user profile",
        "tags=users"
    })
    public User getUser() {
        // ...
    }

    @PostMapping("/user")
    @EndpointDescription({
        "summary=Creates a new user",
        "tags=users"
    })
    public User createUser(@RequestBody User user) {
        // ...
    }

    @GetMapping("/user/{user_id}/project/{project_id}")
    @EndpointDescription({
        "summary=Returns a user project",
        "tags=users,projects",
        "deprecated=false"
    })
    public Project getUserProject(@PathVariable String user_id,
                                  @PathVariable String project_id) {
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
{
  "pathParameters": {},
  "endpoints": [
    {"method": "GET", "attributes": {"summary": "Returns the current user profile", "tags": "users"}},
    {"method": "POST", "attributes": {"summary": "Creates a new user", "tags": "users"}}
  ]
}
```

**Get metadata with path parameters:**

```
GET /anno/user/123/project/456
```

```json
{
  "pathParameters": {"user_id": "123", "project_id": "456"},
  "endpoints": [
    {
      "method": "GET",
      "attributes": {
        "summary": "Returns a user project",
        "tags": "users,projects",
        "deprecated": "false"
      }
    }
  ]
}
```

**Filter by HTTP method:**

```
GET /anno/user?method=POST
```

```json
{
  "pathParameters": {},
  "endpoints": [
    {"method": "POST", "attributes": {"summary": "Creates a new user", "tags": "users"}}
  ]
}
```

**List all annotated endpoints:**

```
GET /anno
```

```json
{
  "/user": [
    {"method": "GET", "attributes": {"summary": "Returns the current user profile", "tags": "users"}},
    {"method": "POST", "attributes": {"summary": "Creates a new user", "tags": "users"}}
  ],
  "/user/{user_id}/project/{project_id}": [
    {"method": "GET", "attributes": {"summary": "Returns a user project", "tags": "users,projects", "deprecated": "false"}}
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
- `GET /anno/user` returns the correct attributes for an annotated endpoint
- `GET /anno` returns a list of all annotated endpoints with their attributes
- `GET /anno/user/123/project/456` extracts path parameters and returns matching metadata
- `GET /anno/user?method=POST` filters endpoints by HTTP method
- `GET /anno/health` returns 404 for an unannotated endpoint
