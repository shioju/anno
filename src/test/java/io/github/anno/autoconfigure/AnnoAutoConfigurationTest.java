package io.github.anno.autoconfigure;

import io.github.anno.autoconfigure.testapp.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestApplication.class)
@AutoConfigureMockMvc
class AnnoAutoConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnMetadataForAnnotatedEndpoint() throws Exception {
        mockMvc.perform(get("/anno/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].path", hasItems("/user")))
                .andExpect(jsonPath("$[*].description",
                        hasItems("Returns the current user profile", "Creates a new user")));
    }

    @Test
    void shouldReturnAllMetadataAtBasePath() throws Exception {
        mockMvc.perform(get("/anno"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['/user']").isArray())
                .andExpect(jsonPath("$['/user'].length()").value(2))
                .andExpect(jsonPath("$['/user'][*].description",
                        hasItems("Returns the current user profile", "Creates a new user")))
                .andExpect(jsonPath("$['/user'][0].path").doesNotExist());
    }

    @Test
    void shouldReturn404ForUnannotatedEndpoint() throws Exception {
        mockMvc.perform(get("/anno/health"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldFilterByMethodQueryParam() throws Exception {
        mockMvc.perform(get("/anno/user?method=POST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Creates a new user"));
    }

    @Test
    void shouldReturn404ForUnknownMethod() throws Exception {
        mockMvc.perform(get("/anno/user?method=DELETE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldBeCaseInsensitiveMethodFilter() throws Exception {
        mockMvc.perform(get("/anno/user?method=get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Returns the current user profile"));
    }
}
