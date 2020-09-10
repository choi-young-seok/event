package io.api.event.domain.dto.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.api.event.domain.entity.event.Event;
import io.api.event.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
class EventEntityModelTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void eventEntityModelTest() throws JsonProcessingException {

        // Given
        Event event = givenEvent();

    }

    private Event givenEvent() {
        return Event.builder()
                    .name("루나소프트 생활 체육회")
                    .description("제 2회 루나 배 풋살 대회")
                    .beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 06, 9, 30 ))
                    .closeEnrollmentDateTime(LocalDateTime.of(2020, 8, 07, 9, 30 ))
                    .beginEventDateTime(LocalDateTime.of(2020, 8, 6, 19, 00))
                    .endEventDateTime(LocalDateTime.of(2020, 8, 13, 22, 00))
                    .basePrice(100)
                    .maxPrice(200)
                    .limitOfEnrollment(0)
                    .location("서울시 강남구 일원동 마루공원 풋살장 1면")
                    .build();
    }

}