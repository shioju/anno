package io.github.anno.autoconfigure;

import io.github.anno.autoconfigure.testapp.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

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
                .andExpect(jsonPath("$.path").value("/user"))
                .andExpect(jsonPath("$.description").value("Returns the current user profile"))
                .andExpect(jsonPath("$.methods").isArray());
    }

    @Test
    void shouldReturnAllMetadataAtBasePath() throws Exception {
        mockMvc.perform(get("/anno"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturn404ForUnannotatedEndpoint() throws Exception {
        mockMvc.perform(get("/anno/health"))
                .andExpect(status().isNotFound());
    }
}
