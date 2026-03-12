package io.github.anno.autoconfigure.testapp;

import io.github.anno.autoconfigure.Condition;
import io.github.anno.autoconfigure.CreateEndpoint;
import io.github.anno.autoconfigure.DeleteEndpoint;
import io.github.anno.autoconfigure.ReadEndpoint;
import io.github.anno.autoconfigure.SearchEndpoint;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestUserController {

    @GetMapping("/user")
    @ReadEndpoint(
            resourceId = "currentUser",
            title = "User Profile",
            category = "users",
            classification = "internal"
    )
    public String getUser() {
        return "test-user";
    }

    @PostMapping("/user")
    @CreateEndpoint(
            resourceId = "user",
            title = "New User",
            category = "users",
            classification = "internal"
    )
    public String createUser() {
        return "created";
    }

    @GetMapping("/user/{user_id}/project/{project_id}")
    @ReadEndpoint(
            condition = Condition.IF_AVAILABLE,
            resourceId = "project",
            title = "User Project",
            category = "projects",
            classification = "internal"
    )
    public String getUserProject(@PathVariable String user_id, @PathVariable String project_id) {
        return "project";
    }

    @DeleteMapping("/user/{user_id}")
    @DeleteEndpoint(resourceId = "user")
    public String deleteUser(@PathVariable String user_id) {
        return "deleted";
    }

    @GetMapping("/search/users")
    @SearchEndpoint(
            query = "searchTerm",
            filters = "active=true",
            fromDate = "2024-01-01",
            toDate = "2024-12-31"
    )
    public String searchUsers() {
        return "results";
    }

    @GetMapping("/health")
    public String health() {
        return "ok";
    }
}
