package io.api.event.controller;

import io.api.event.common.BaseControllerTest;
import io.api.event.domain.dto.event.EventDto;
import io.api.event.domain.entity.event.Event;
import io.api.event.repository.EventRepository;
import io.api.event.repository.account.AccountRepository;
import io.api.event.service.account.AccountService;
import io.api.event.util.common.TestDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * https://velog.io/@kingcjy/Spring-REST-Docs%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%9C-API-%EB%AC%B8%EC%84%9C-%EC%9E%90%EB%8F%99%ED%99%94
 * https://woowabros.github.io/experience/2018/12/28/spring-rest-docs.html
 */
//@AutoConfigureRestDocs()
public class EventControllerFailTest extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @BeforeEach
    public void setUpRepository() {
        eventRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @TestDescription("JSR303 Annotation을 이용한 입력값이 없는 요청의 400 Bad Request 처리")
    @DisplayName("Create Event API : 입력값이 없는 요청")
    public void createEventAPI_EmptyRequest_Test() throws Exception {
        // Given
        EventDto eventDto = new EventDto();

        // When
        String urlTemplate = "/api/events";
        ResultActions resultActions = mockMvc.perform(post(urlTemplate)
                .header(HttpHeaders.AUTHORIZATION, this.authInfoGenerator.getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(objectMapper.writeValueAsString(eventDto))
        );

        /** Then : Check list
         * - 응답 코드 확인 : 400 Bad Request
         * - 응답 항목 내 오류 정보 확인 : field/global Error (objectName, defaultMessage, code)
         * - 응답 항목 내 index link 항목 확인
         */
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].field").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("CustomValidator를 이용한 입력값이 유효하지 못한 요청의 400 Bad Request 처리")
    @DisplayName("Create Event API : 입력값이 유효하지 못한 요청")
    public void createEventAPI_WrongParameterRequest_Test() throws Exception {
        // Given
        Event event = Event.builder()
                .name("루나소프트 생활 체육회")
                .description("제 2회 루나 배 풋살 대회")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 6, 9, 30 ))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 8, 7, 9, 30 ))
                .beginEventDateTime(LocalDateTime.of(2020, 8, 6, 19, 0))
                .endEventDateTime(LocalDateTime.of(2020, 8, 13, 22, 0))
                .basePrice(300)
                .maxPrice(200)
                .limitOfEnrollment(0)
                .location("서울시 강남구 일원동 마루공원 풋살장 1면")
                .build();

        // When
        String urlTemplate = "/api/events";
        ResultActions resultActions = mockMvc.perform(post(urlTemplate)
                .header(HttpHeaders.AUTHORIZATION, authInfoGenerator.getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(objectMapper.writeValueAsString(event))
        );

        /** Then : Check list
         * - 응답 코드 확인 : 400 Bad Request
         * - 응답 항목 내 오류 정보 확인 : field/global Error (objectName, defaultMessage, code)
         * - 응답 항목 내 index link 정보 확인
         */
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("요청 정보에 해당하는 Event 객체 조회 실패 시, 404 Not Found 처리")
    @DisplayName("Get Event API : 존재 하지 않는 이벤트 조회 요청")
    public void getEventAPI_NotFount_Test() throws Exception {
        // Given
        String urlTemplate = "/api/events/12134";

        // When
        ResultActions resultActions = mockMvc.perform(get(urlTemplate)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
        );

        /** Then : Check list
         * - 응답 코드 확인 : 404 Not Found
         * - 응답 항목 내 index link 항목 확인
         */
        resultActions
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().exists(HttpHeaders.LOCATION))
        ;
    }

    @Test
    @TestDescription("JSR303 Annotation을 이용한 입력값이 없는 요청의 400 Bad Request 처리")
    @DisplayName("Update Event API : 입력값이 없는 요청")
    public void updateEventAPI_EmptyRequest_Test() throws Exception {
        // Given
        Event event = eventDomainGenerator.generatedEvent(100);
        EventDto eventDto = new EventDto();

        // When
        String urlTemplate = "/api/events/{id}";
        ResultActions resultActions = mockMvc.perform(put(urlTemplate, event.getId())
                .header(HttpHeaders.AUTHORIZATION, authInfoGenerator.getBearerToken())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(this.objectMapper.writeValueAsString(eventDto))
        );

        // Then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content").isArray())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].field").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("CustomValidator를 이용한 입력값이 유효하지 못한 요청의 400 Bad Request 처리")
    @DisplayName("Update Event API : 입력값이 유효하지 못한 요청")
    public void updateEventAPI_WrongParameterRequest_Test() throws Exception {
        // Given
        Event event =  eventDomainGenerator.generatedEvent(100);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(10000);
        String updatedEventName = "updated Event Name";
        eventDto.setName(updatedEventName);

        // When
        String urlTemplate = "/api/events/{id}";
        ResultActions resultActions = mockMvc.perform(put(urlTemplate, event.getId())
                .header(HttpHeaders.AUTHORIZATION, authInfoGenerator.getBearerToken())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(this.objectMapper.writeValueAsString(eventDto))
        );

        // Then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("요청 정보에 해당하는 Event 객체 조회 실패 시, 404 Not Found 처리")
    @DisplayName("Update Event API : 존재 하지 않는 이벤트 수정 요청")
    public void updateEventAPI_NotFound_Test() throws Exception {
        // Given
        Event event =  eventDomainGenerator.generatedEvent(100);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String updatedEventName = "updated Event Name";
        eventDto.setName(updatedEventName);

        // When
        String urlTemplate = "/api/events/123124";
        ResultActions resultActions = mockMvc.perform(put(urlTemplate)
                .header(HttpHeaders.AUTHORIZATION, authInfoGenerator.getBearerToken())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(this.objectMapper.writeValueAsString(eventDto))
        );

        // Then
        resultActions.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(header().exists(HttpHeaders.LOCATION))
        ;
    }
}