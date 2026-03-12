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
                .andExpect(jsonPath("$.endpoints").isArray())
                .andExpect(jsonPath("$.endpoints.length()").value(2))
                .andExpect(jsonPath("$.endpoints[*].method", hasItems("GET", "POST")))
                .andExpect(jsonPath("$.endpoints[*].attributes.action",
                        hasItems("READ", "CREATE")))
                .andExpect(jsonPath("$.pathParameters").isEmpty());
    }

    @Test
    void shouldReturnAllMetadataAtBasePath() throws Exception {
        mockMvc.perform(get("/anno"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['/user']").isArray())
                .andExpect(jsonPath("$['/user'].length()").value(2))
                .andExpect(jsonPath("$['/user'][*].attributes.action",
                        hasItems("READ", "CREATE")))
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
                .andExpect(jsonPath("$.endpoints").isArray())
                .andExpect(jsonPath("$.endpoints.length()").value(1))
                .andExpect(jsonPath("$.endpoints[0].attributes.action").value("CREATE"))
                .andExpect(jsonPath("$.endpoints[0].attributes.resourceId").value("user"));
    }

    @Test
    void shouldReturn404ForUnknownMethod() throws Exception {
        mockMvc.perform(get("/anno/user?method=PATCH"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldBeCaseInsensitiveMethodFilter() throws Exception {
        mockMvc.perform(get("/anno/user?method=get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoints.length()").value(1))
                .andExpect(jsonPath("$.endpoints[0].attributes.action").value("READ"))
                .andExpect(jsonPath("$.endpoints[0].attributes.resourceId").value("currentUser"));
    }

    @Test
    void shouldReturnPathParametersForParameterizedEndpoint() throws Exception {
        mockMvc.perform(get("/anno/user/123/project/456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pathParameters.user_id").value("123"))
                .andExpect(jsonPath("$.pathParameters.project_id").value("456"))
                .andExpect(jsonPath("$.endpoints[0].attributes.action").value("READ"))
                .andExpect(jsonPath("$.endpoints[0].attributes.condition").value("IF_AVAILABLE"))
                .andExpect(jsonPath("$.endpoints[0].attributes.resourceId").value("project"))
                .andExpect(jsonPath("$.endpoints[0].attributes.title").value("User Project"))
                .andExpect(jsonPath("$.endpoints[0].method").value("GET"));
    }

    @Test
    void shouldReturnDefaultConditionAsAlways() throws Exception {
        mockMvc.perform(get("/anno/user?method=GET"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoints[0].attributes.condition").value("ALWAYS"));
    }

    @Test
    void shouldReturnSearchEndpointAttributes() throws Exception {
        mockMvc.perform(get("/anno/search/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoints[0].attributes.action").value("SEARCH"))
                .andExpect(jsonPath("$.endpoints[0].attributes.query").value("searchTerm"))
                .andExpect(jsonPath("$.endpoints[0].attributes.filters").value("active=true"))
                .andExpect(jsonPath("$.endpoints[0].attributes.fromDate").value("2024-01-01"))
                .andExpect(jsonPath("$.endpoints[0].attributes.toDate").value("2024-12-31"));
    }

    @Test
    void shouldReturnDeleteEndpointAttributes() throws Exception {
        mockMvc.perform(get("/anno/user/42?method=DELETE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoints[0].attributes.action").value("DELETE"))
                .andExpect(jsonPath("$.endpoints[0].attributes.resourceId").value("user"))
                .andExpect(jsonPath("$.endpoints[0].attributes.condition").value("ALWAYS"));
    }
}
