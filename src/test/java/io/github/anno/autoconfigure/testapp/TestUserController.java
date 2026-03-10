package io.github.anno.autoconfigure.testapp;

import io.github.anno.autoconfigure.EndpointDescription;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestUserController {

    @GetMapping("/user")
    @EndpointDescription("Returns the current user profile")
    public String getUser() {
        return "test-user";
    }

    @PostMapping("/user")
    @EndpointDescription("Creates a new user")
    public String createUser() {
        return "created";
    }

    @GetMapping("/user/{user_id}/project/{project_id}")
    @EndpointDescription("Returns a user project")
    public String getUserProject(@PathVariable String user_id, @PathVariable String project_id) {
        return "project";
    }

    @GetMapping("/health")
    public String health() {
        return "ok";
    }
}
