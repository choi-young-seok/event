package io.api.event.controller.event;

import io.api.event.common.BaseControllerTest;
import io.api.event.controller.event.docs.EventDocumentGenerator;
import io.api.event.domain.dto.event.EventDto;
import io.api.event.domain.entity.event.Event;
import io.api.event.domain.entity.event.EventStatus;
import io.api.event.repository.EventRepository;
import io.api.event.repository.account.AccountRepository;
import io.api.event.service.account.AccountService;
import io.api.event.util.common.TestDescription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static io.api.event.common.document.DocumentFormatGenerator.DATETIME_FORMAT;
import static io.api.event.common.document.DocumentFormatGenerator.getDateTimeFormat;
import static io.api.event.controller.event.docs.EventDocumentGenerator.*;
import static io.api.event.util.common.constant.DocsInfo.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@AutoConfigureRestDocs()
@Import(EventDocumentGenerator.class)
public class EventControllerSuccessTest extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    public void setUpRepository(){
        eventRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @TestDescription("Spring HATEOAS, Spring REST DOCS를 이용한 API 응답, 전이 가능한 Link정보, Docs 생성 유무 확인")
    @DisplayName("Create Event API : 이벤트 생성 요청")
    public void createEventApi() throws Exception {
        // Given
        EventDto eventDto = EventDto.builder()
                .name("루나소프트 생활 체육회")
                .description("제 2회 루나 배 풋살 대회")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 6, 9, 30 ))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 8, 7, 9, 30 ))
                .beginEventDateTime(LocalDateTime.of(2020, 8, 13, 19, 0))
                .endEventDateTime(LocalDateTime.of(2020, 8, 13, 22, 0))
                .basePrice(100)
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
                .content(objectMapper.writeValueAsString(eventDto))
        );

        /** Then : Check list
         * - 응답 코드 확인 : 201 Created
         * - 응답 헤더 확인 : Location, Content-Type Info
         * - 응답 바디 확인 : success response or error response
         * - 응답 내 링크 항목 확인 : _links.self + @
         * - 요청/응답 항목을 이용한 REST DOCS document 작성
         *   - 요청 항목(Header/Body) 문서화
         *   - 응답 항목(Header/Body) 문서화
         *   - Link info 문서화 (self, get-event-list, update-event, profile)
         */
        resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().exists(HttpHeaders.CONTENT_TYPE))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.get-event-list").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(createEventDocument())
        ;
    }

    @Test
    @TestDescription("Spring HATEOAS, Spring REST DOCS를 이용한 API 응답, 전이 가능한 Link정보, Docs 생성 유무 확인")
    @DisplayName("Get Event List API : 이벤트 목록 조회 요청")
    public void getEventListApi() throws Exception {
        // Given
        IntStream.range(0, 30).forEach(eventDomainGenerator::generatedEvent);

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

        /** Then : Check list
         * - 응답 코드 확인 : 200 Ok
         * - 응답 헤더 확인 : Location, Content-Type
         * - 응답 바디 확인 : paging 처리 관련 정보 (total Count, per Page, total Page, current Page)
         * - 응답 내 링크 항목 확인 : _links -> self, first, prev, self, next, last, profile
         * - 요청/응답 항목을 이용한 REST DOCS document 작성
         *   - 요청 항목(Header/Body) 문서화
         *   - 응답 항목(Header/Body) 문서화
         *   - Link info 문서화 (self, get-event-list, update-event, profile)
         */
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
                .andDo(getEventList())
        ;
    }

    /**
     * Rest Docs의 pathParameters를 사용하기 위해 MockMvcBuilders.get -> RestDocumentationRequestBuilders.get 수정
     * 참조 URL : https://java.ihoney.pe.kr/517
     */
    @Test
    @TestDescription("Spring HATEOAS, Spring REST DOCS를 이용한 API 응답, 전이 가능한 Link정보, Docs 생성 유무 확인")
    @DisplayName("Get Event API : 이벤트 조회 요청")
    public void getAnEventApi() throws Exception {
        // Given
        Event event = eventDomainGenerator.generatedEvent(100);

        // When :
        String urlTemplate = "/api/events/{id}";
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.get(urlTemplate, event.getId())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
        );

        /** Then : Check list
         * - 응답 코드 확인 : 200 Ok
         * - 응답 헤더 확인 : Location, Content-Type
         * - 응답 바디 확인 : paging 처리 관련 정보 (total Count, per Page, total Page, current Page)
         * - 응답 내 링크 항목 확인 : _links -> self
         * - 요청/응답 항목을 이용한 REST DOCS document 작성
         *   - 요청 항목(Header/Body) 문서화
         *   - 응답 항목(Header/Body) 문서화
         *   - Link info 문서화 (self, update-event, profile)
         */
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links").exists())
                .andDo(getAnEvent())
        ;
    }

    @Test
    @TestDescription("Spring HATEOAS, Spring REST DOCS를 이용한 API 응답, 전이 가능한 Link정보, Docs 생성 유무 확인")
    @DisplayName("Update Event API : 이벤트 수정 요청")
    public void updateEventApi() throws Exception {
        // Given
        Event event =  eventDomainGenerator.generatedEvent(100);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String updatedEventName = "updated Event Name";
        eventDto.setName(updatedEventName);

        // When
        String urlTemplate = "/api/events/{id}";
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.put(urlTemplate, event.getId())
                .header(HttpHeaders.AUTHORIZATION, authInfoGenerator.getBearerToken())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(this.objectMapper.writeValueAsString(eventDto))
        );

        /** Then : Check list
         * - 응답 코드 확인 : 201 Created
         * - 응답 헤더 확인 : Location, Content-Type Info
         * - 응답 바디 확인 : success response or error response
         * - 응답 내 링크 항목 확인 : _links.self + @
         * - 요청/응답 항목을 이용한 REST DOCS document 작성
         *   - 요청 항목(Header/Body) 문서화
         *   - 응답 항목(Header/Body) 문서화
         *   - Link info 문서화 (self, get-an-event, profile)
         */
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value(updatedEventName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(updateEvent())
        ;
    }

}