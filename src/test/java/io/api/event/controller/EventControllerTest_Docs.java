package io.api.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.api.event.config.RestDocsConfiguration;
import io.api.event.domain.dto.event.EventDto;
import io.api.event.domain.entity.event.EventStatus;
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
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc // @SpringBootTest annotation을 이용한 통합테스트 진행 시 해당 TC내에서 MockMvc를 주입하기위한 annotation
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class EventControllerTest_Docs {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @TestDescription("Spring REST DOCS를 이용한 Mocking TC로 API Docs 생성 유무 테스트")
    public void create_API_Docs_By_SpringRestDocs_() throws Exception {
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

        mockMvc.perform(post("/api/event09")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
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
                        /** relaxedResponseFields
                         * Spring RestDocs는 Test 수행 결과로 수신한 ResponseBody의 각 항목 전체를 문서로 기술 하도록 강제 하고 있다.
                         * 원하는 필드만 문서로 기술해야 하는 경우 responseFields가 아닌 relaxedResponseFields를 사용한다.
                         * - 장점 : 문서 일부분만 테스트 할 수 있다.
                         * - 단점 : 정확한 문서를 생성하지 못한다.
                         * */
//                    relaxedResponseFields(
//                            fieldWithPath("id").description("identifier of new event"),
//                            fieldWithPath("name").description("Name of new event"),
//                            fieldWithPath("description").description("Description of new event"),
//                            fieldWithPath("beginEnrollmentDateTime").description("Date time of begin enrollment of new event"),
//                            fieldWithPath("closeEnrollmentDateTime").description("Date time of close enrollment of new event"),
//                            fieldWithPath("beginEventDateTime").description("Date time of begin of new event"),
//                            fieldWithPath("endEventDateTime").description("Date time of close of new event"),
//                            fieldWithPath("location").description("Location of new event"),
//                            fieldWithPath("basePrice").description("Base Price of new event"),
//                            fieldWithPath("maxPrice").description("MaxPrice of new event"),
//                            fieldWithPath("limitOfEnrollment").description("Limit of enrollment of new event"),
//                            fieldWithPath("offline").description("it tells if this event is free"),
//                            fieldWithPath("free").description("it tells if this event is offline"),
//                            fieldWithPath("eventStatus").description("eventStatus of new event")
//                    )
                ))
        ;
    }
}
