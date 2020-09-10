package io.api.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.api.event.config.RestDocsConfiguration;
import io.api.event.domain.dto.event.EventDto;
import io.api.event.domain.entity.event.Event;
import io.api.event.domain.entity.event.EventStatus;
import io.api.event.repository.EventRepository;
import io.api.event.util.common.TestDescription;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc // @SpringBootTest annotation을 이용한 통합테스트 진행 시 해당 TC내에서 MockMvc를 주입하기위한 annotation
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

    @Test
    @TestDescription("Spring HATEOAS,Spring REST DOCS를 이용한 Mocking TC 결과에 API 응답, 전이가능한 Link정보, API Docs 생성 유무 확인")
    public void create_EventAPI_Test() throws Exception {

        // Given
        EventDto eventDto = EventDto.builder()
                .name("루나소프트 생활 체육회")
                .description("제 2회 루나 배 풋살 대회")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 06, 9, 30 ))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 8, 07, 9, 30 ))
                .beginEventDateTime(LocalDateTime.of(2020, 8, 13, 19, 00))
                .endEventDateTime(LocalDateTime.of(2020, 8, 13, 22, 00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(0)
                .location("서울시 강남구 일원동 마루공원 풋살장 1면")
                .build();

        // When
        String urlTemplate = "/api/events";
        ResultActions resultActions = mockMvc.perform(post(urlTemplate)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(objectMapper.writeValueAsString(eventDto))
        );

        // Then
        resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-event").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("_links.profile").exists())
                /** write Docs snippets
                 * - Request Header/Body and each Field 문서화
                 * - Response Header/Body and each Field 문서화
                 * - Link info 문서화
                 *  - self
                 *  - query-events
                 *  - update-event
                 *  - profile
                 * */
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-event").description("link to query ans event"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type haeder")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("Description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("Date time of begin enrollment of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("Date time of close enrollment of new event"),
                                fieldWithPath("beginEventDateTime").description("Date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("Date time of close of new event"),
                                fieldWithPath("location").description("Location of new event"),
                                fieldWithPath("basePrice").description("Base Price of new event"),
                                fieldWithPath("maxPrice").description("MaxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("Limit of enrollment of new event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("Response content type")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("Description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("Date time of begin enrollment of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("Date time of close enrollment of new event"),
                                fieldWithPath("beginEventDateTime").description("Date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("Date time of close of new event"),
                                fieldWithPath("location").description("Location of new event"),
                                fieldWithPath("basePrice").description("Base Price of new event"),
                                fieldWithPath("maxPrice").description("MaxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("Limit of enrollment of new event"),
                                fieldWithPath("offline").description("it tells if this event is free"),
                                fieldWithPath("free").description("it tells if this event is offline"),
                                fieldWithPath("eventStatus").description("eventStatus of new event"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-event.href").description("link to query an event"),
                                fieldWithPath("_links.update-event.href").description("link to update an existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }

    @Test
    @TestDescription("입력값이 유효하지 못한 요청의 Bad Request처리 시 Body 유무 확인")
    public void create_EventAPI_badRequest_Test() throws Exception {
        Event event = Event.builder()
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

        String urlTemplate = "/api/events";
        mockMvc.perform(post(urlTemplate)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$[0].objectName").exists())
//                .andExpect(jsonPath("$[0].defaultMessage").exists())
//                .andExpect(jsonPath("$[0].code").exists())
//                .andExpect(jsonPath("$[0].field").exists())
//                .andExpect(jsonPath("$[0].rejectedValue").exists())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("Spring HATEOAS,Spring REST DOCS를 이용한 Mocking TC 결과에 API 응답, 전이가능한 Link정보, API Docs 생성 유무 확인")
    public void get_EventListAPI_Test() throws Exception {
        // Given
//        IntStream.range(0, 30).forEach(i -> this.generatedEvent(i));
        IntStream.range(0, 30).forEach(this::generatedEvent);

        // When
        String urlTemplate = "/api/events";
        ResultActions resultActions = mockMvc.perform(get(urlTemplate)
                .param("page", "1")
                .param("size", "2")
                .param("sort", "name,DESC")
                .characterEncoding(StandardCharsets.UTF_8.name())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
        );

        // Then
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links").exists())
                .andExpect(jsonPath("_links.first").exists())
                .andExpect(jsonPath("_links.prev").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.next").exists())
                .andExpect(jsonPath("_links.last").exists())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("page.size").exists())
                .andExpect(jsonPath("page.totalElements").exists())
                .andExpect(jsonPath("page.totalPages").exists())
                .andExpect(jsonPath("page.number").exists())
                .andDo(document("query-events"))

        ;

    }

    private void generatedEvent(int index) {
        Event event = Event.builder()
                .name("generated event name " + index)
                .description("test event info " + index)
                .build();

        eventRepository.save(event);
    }

    @Test
    @TestDescription("입력값이 유효하지 못한 요청의 Bad Request 처리 시 Body 유무 확인")
    public void get_EventListAPI_badRequest_Test(){

    }
}
