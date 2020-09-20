package io.api.event.controller;

import io.api.event.common.BaseControllerTest;
import io.api.event.domain.dto.event.EventDto;
import io.api.event.domain.entity.account.Account;
import io.api.event.domain.entity.account.AccountRole;
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
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTest extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @BeforeEach
    public void setUpRepository(){
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    @TestDescription("Spring HATEOAS, Spring REST DOCS를 이용한 API 응답, 전이 가능한 Link정보, Docs 생성 유무 확인")
    @DisplayName("Create Event API : 이벤트 생성 요청")
    public void createEventAPI_Test() throws Exception {
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("get-event-list").description("link to query ans event"),
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
                                fieldWithPath("manager").description("manager info of event"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.get-event-list.href").description("link to query an event"),
                                fieldWithPath("_links.update-event.href").description("link to update an existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
    @TestDescription("Spring HATEOAS, Spring REST DOCS를 이용한 API 응답, 전이 가능한 Link정보, Docs 생성 유무 확인")
    @DisplayName("Get Event List API : 이벤트 목록 조회 요청")
    public void getEventListAPI_Test() throws Exception {
        // Given
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
                .andDo(document("get-event-list"))
        ;
    }

    @Test
    @TestDescription("Spring HATEOAS, Spring REST DOCS를 이용한 API 응답, 전이 가능한 Link정보, Docs 생성 유무 확인")
    @DisplayName("Get Event API : 이벤트 조회 요청")
    public void getEventAPI_Test() throws Exception {
        // Given
        Event event = this.generatedEvent(100);

        // When
        String urlTemplate = "/api/events/{id}";
        ResultActions resultActions = mockMvc.perform(get(urlTemplate, event.getId())
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
                .andDo(document("get-an-event"))
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
    @TestDescription("Spring HATEOAS, Spring REST DOCS를 이용한 API 응답, 전이 가능한 Link정보, Docs 생성 유무 확인")
    @DisplayName("Update Event API : 이벤트 수정 요청")
    public void updateEventAPI_Test() throws Exception {
        // Given
        Event event =  this.generatedEvent(100);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String updatedEventName = "updated Event Name";
        eventDto.setName(updatedEventName);

        // When
        String urlTemplate = "/api/events/{id}";
        ResultActions resultActions = mockMvc.perform(put(urlTemplate, event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                .andDo(document("event-update"))
        ;
    }

    @Test
    @TestDescription("JSR303 Annotation을 이용한 입력값이 없는 요청의 400 Bad Request 처리")
    @DisplayName("Update Event API : 입력값이 없는 요청")
    public void updateEventAPI_EmptyRequest_Test() throws Exception {
        // Given
        Event event = this.generatedEvent(100);
        EventDto eventDto = new EventDto();

        // When
        String urlTemplate = "/api/events/{id}";
        ResultActions resultActions = mockMvc.perform(put(urlTemplate, event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
        Event event =  this.generatedEvent(100);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(10000);
        String updatedEventName = "updated Event Name";
        eventDto.setName(updatedEventName);

        // When
        String urlTemplate = "/api/events/{id}";
        ResultActions resultActions = mockMvc.perform(put(urlTemplate, event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
        Event event =  this.generatedEvent(100);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String updatedEventName = "updated Event Name";
        eventDto.setName(updatedEventName);

        // When
        String urlTemplate = "/api/events/123124";
        ResultActions resultActions = mockMvc.perform(put(urlTemplate)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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

    private Event generatedEvent(int index) {
        Event event = Event.builder()
                .name("루나소프트 생활 체육회 : " + index)
                .description("제 2회 루나 배 풋살 대회 : " + index)
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 6, 9, 30 ))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 8, 7, 9, 30 ))
                .beginEventDateTime(LocalDateTime.of(2020, 8, 13, 19, 0))
                .endEventDateTime(LocalDateTime.of(2020, 8, 13, 22, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(0)
                .location("서울시 강남구 일원동 마루공원 풋살장 1면")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        Event createdEvent = eventRepository.save(event);
        return createdEvent;
    }


    private String getBearerToken() throws Exception {
        return "Bearer " + this.getAccessToken();
    }

    private String getAccessToken() throws Exception {
        // Given
        String email = "rcn115@naver.com";
        String password = "chldydtjr1!";
        Set<AccountRole> roles = Set.of(AccountRole.ADMIN, AccountRole.USER);

        Account account = Account.builder()
                .email(email)
                .password(password)
                .roles(roles)
                .build();

        Account savedAccount = this.accountService.saveAccount(account);

        String clientId = "myApp";
        String clientSecret = "pass";

        // When
        String urlTemplate = "/oauth/token";
        ResultActions resultActions = this.mockMvc.perform(post(urlTemplate)
                .with(httpBasic(clientId, clientSecret)) // clientId와 clientSecret를 이용한 basicOath Header 생성
                .param("username", email)
                .param("password", password)
                .param("grant_type", "password")
                .characterEncoding(StandardCharsets.UTF_8.name())
                .accept(MediaTypes.HAL_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser jackson2JsonParser = new Jackson2JsonParser();
        return jackson2JsonParser.parseMap(responseBody).get("access_token").toString();
    }
}