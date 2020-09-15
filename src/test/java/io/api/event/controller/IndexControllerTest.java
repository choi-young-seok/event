package io.api.event.controller;

import io.api.event.common.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IndexControllerTest extends BaseControllerTest {

    @Test
    public void ReadIndex_Test() throws Exception {
        // Given

        // When
        String urlTemplate = "/api";
        ResultActions resultActions = this.mockMvc.perform(get(urlTemplate)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
        );

        // Then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.events").exists())
        ;

    }

}