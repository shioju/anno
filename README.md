# anno-spring-boot-starter

A Spring Boot starter that lets you annotate controller endpoints with typed action metadata and automatically exposes it via a REST API.

## What it does

1. You add a typed action annotation (e.g. `@ReadAction`, `@CreateAction`) to any Spring MVC controller method
2. At startup, the starter scans all registered handler methods for these annotations
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

Each annotation captures the action type and relevant attributes for that operation:

```java
@RestController
public class UserController {

    @GetMapping("/user")
    @ReadAction(
        resourceId = "currentUser",
        title = "User Profile",
        category = "users",
        classification = "internal"
    )
    public User getUser() {
        // ...
    }

    @PostMapping("/user")
    @CreateAction(
        resourceId = "user",
        title = "New User",
        category = "users",
        classification = "internal"
    )
    public User createUser(@RequestBody User user) {
        // ...
    }

    @GetMapping("/user/{user_id}/project/{project_id}")
    @ReadAction(
        condition = Condition.IF_AVAILABLE,
        resourceId = "project",
        title = "User Project",
        category = "projects",
        classification = "internal"
    )
    public Project getUserProject(@PathVariable String user_id,
                                  @PathVariable String project_id) {
        // ...
    }

    @DeleteMapping("/user/{user_id}")
    @DeleteAction(resourceId = "user")
    public void deleteUser(@PathVariable String user_id) {
        // ...
    }

    @GetMapping("/search/users")
    @SearchAction(
        query = "searchTerm",
        filters = "active=true",
        fromDate = "2024-01-01",
        toDate = "2024-12-31"
    )
    public List<User> searchUsers() {
        // ...
    }
}
```

### Available annotations

| Annotation | Attributes |
|---|---|
| `@ReadAction` | `resourceId`, `title`, `category`, `classification`, `condition` |
| `@CreateAction` | `resourceId`, `title`, `category`, `classification`, `condition` |
| `@UpdateAction` | `resourceId`, `condition` |
| `@DeleteAction` | `resourceId`, `condition` |
| `@ListAction` | `title`, `condition` |
| `@SearchAction` | `query`, `filters`, `fromDate`, `toDate`, `condition` |
| `@DownloadAction` | `resourceId`, `fileName`, `condition` |
| `@GrantAccessAction` | `resourceId`, `actorId`, `accessLevel`, `condition` |
| `@RevokeAccessAction` | `resourceId`, `actorId`, `accessLevel`, `condition` |
| `@ChangeAccessAction` | `resourceId`, `actorId`, `accessLevel`, `condition` |

All annotations have a `condition` attribute defaulting to `Condition.ALWAYS`. Available values: `ALWAYS`, `IF_AVAILABLE`, `SUCCESSFUL`, `FAILED`.

### Query the metadata

**Get metadata for a specific endpoint:**

```
GET /anno/user
```

```json
{
  "pathParameters": {},
  "endpoints": [
    {
      "method": "GET",
      "attributes": {
        "action": "READ",
        "condition": "ALWAYS",
        "resourceId": "currentUser",
        "title": "User Profile",
        "category": "users",
        "classification": "internal"
      }
    },
    {
      "method": "POST",
      "attributes": {
        "action": "CREATE",
        "condition": "ALWAYS",
        "resourceId": "user",
        "title": "New User",
        "category": "users",
        "classification": "internal"
      }
    }
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
        "action": "READ",
        "condition": "IF_AVAILABLE",
        "resourceId": "project",
        "title": "User Project",
        "category": "projects",
        "classification": "internal"
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
    {
      "method": "POST",
      "attributes": {
        "action": "CREATE",
        "condition": "ALWAYS",
        "resourceId": "user",
        "title": "New User",
        "category": "users",
        "classification": "internal"
      }
    }
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
    {"method": "GET", "attributes": {"action": "READ", "condition": "ALWAYS", "resourceId": "currentUser", "title": "User Profile", "category": "users", "classification": "internal"}},
    {"method": "POST", "attributes": {"action": "CREATE", "condition": "ALWAYS", "resourceId": "user", "title": "New User", "category": "users", "classification": "internal"}}
  ],
  "/user/{user_id}/project/{project_id}": [
    {"method": "GET", "attributes": {"action": "READ", "condition": "IF_AVAILABLE", "resourceId": "project", "title": "User Project", "category": "projects", "classification": "internal"}}
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
2. `EndpointDescriptionCollector` is created as a bean, injected with Spring's `RequestMappingHandlerMapping`, and scans all handler methods for typed action annotations (`@ReadAction`, `@CreateAction`, etc.) at startup
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
