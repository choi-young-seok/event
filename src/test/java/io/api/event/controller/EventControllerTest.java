package io.api.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.api.event.domain.dto.event.EventDto;
import io.api.event.domain.entity.event.Event;
import io.api.event.domain.entity.event.EventStatus;
import io.api.event.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
/** SpringBoot Slice Test
 * - @WebMvcTest
 *  - MockMvc 빈을 자동 설정
 *  - 웹 관련 빈만 등록 한다. (slice)
 * */
//@WebMvcTest
@SpringBootTest
@AutoConfigureMockMvc // @SpringBootTest annotation을 이용한 통합테스트 진행 시 해당 TC내에서 MockMvc를 주입하기위한 annotation
class EventControllerTest {

    /** SpringMVC Test 핵심 클래스
     * web 서버를 띄우지 않고, DispatcherServlet이 요청을 처리하는 과정을 Test할 수 있다.
     * Controller Test용으로 자주 쓰임
     * */
    @Autowired
    MockMvc mockMvc;

    /** EventController.createEvent TEST CASE #01 : EventController 통신 및 응답코드 확인
     * - Test List
     *  - POST Request에 201(HttpStatus.created()) 응답 확인
     *      - RequestHeader.contentType -> "application/json"
     *      - ResponseHeader.contentType -> "application/hal_json"
     *  - Location header에 생성된 이벤트를 조회할 수 있는 URL 응답 확인
     * */
    @Test
    public void createEvent_TEST_01() throws Exception {
        mockMvc.perform(post("/api/event/")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaTypes.HAL_JSON)
                )
                .andDo(print()) // MockMvcResultHandlers.print() : 요청/응답 원문 출력
                .andExpect(status().isCreated()) // MockMvcResultMatchers.status() : 응답 코드를 TypeSafe 하게 비교
        ;
    }

    @Autowired
    ObjectMapper objectMapper;
    /** EventController.createEvent TEST CASE #02 : EventController 응답 데이터 확인
     * - Test List
     *  - RequestBody에 생성 요청 객체를 포함한 요청의 응답 내 id항목이 DB적재 후 auto increment된 값 인지 확인
     * */
    @Test
    public void createEvent_TEST_02() throws Exception {
        Event event = Event.builder()
                .name("루나소프트 생활 체육회")
                .description("제 2회 루나 배 풋살 대회")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 06, 9, 30 ))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 8, 06, 9, 30 ))
                .beginEventDateTime(LocalDateTime.of(2020, 8, 13, 19, 00))
                .endEventDateTime(LocalDateTime.of(2020, 8, 13, 22, 00))
                .basePrice(0)
                .maxPrice(0)
                .limitOfEnrollment(0)
                .location("서울시 강남구 일원동 마루공원 풋살장 1면")
                .build();

        mockMvc.perform(post("/api/event02")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaTypes.HAL_JSON_VALUE)
                    .characterEncoding(StandardCharsets.UTF_8.name())
                    .content(objectMapper.writeValueAsString(event))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
        ;
    }

    /** @WebMvcTest annotation은 Web관련 Bean만 등록 하므로, Repository관련 Bean은 등록되지 않아 해당 TC실행 시 NoSuchBeanDefinitionException 발생
     * 오류_01) : NoSuchBeanDefinitionException: No qualifying bean of type 'io.api.event.repository.EventRepository' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}
     * 해결 : @McokBean annotation을 이용하면 Bean을 mocking 할 수 있으므로, Test에 필요한 Repository관련 Bean을 등록 하여 해결
     * 오류_02) : @MockBean을 이용해 생성한 객체를 이용하여 Test를 진행 하는 경우, Mock 객체 이므로 return되는 값이 null
     *  따라서 해당 Mock객체의 결과값으로 추가 로직을 구성하는 경우 NullPointException이 발생 할 수 있다.
     *  해결 : stubbing을 통해 추가 로직을 구성하기 위해 필요한 행동을 지정
     *   -> EX) Mockito.when(eventRepository.save(event)).thenReturn(event)
     *   -> eventRepository.save(event)가 호출 되면 event를 Return하라는 stubbing을 지정
    */
//    @MockBean
//    EventRepository eventRepository;

    /** EventController.createEvent TEST CASE #03 : EventRepository의 save 호출 후 EventController의 응답 내 생성된 객체의 ID값 응답 여부 확인
     * - Test List
     *  - RequestBody에 생성 요청 객체를 포함한 요청의 응답 내 id항목이 DB적재 후 auto increment된 값 인지 확인
     * */
    @Test
    public void createEvent_TEST_03() throws Exception {
        Event event = Event.builder()
                .name("루나소프트 생활 체육회")
                .description("제 2회 루나 배 풋살 대회")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 06, 9, 30 ))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 8, 06, 9, 30 ))
                .beginEventDateTime(LocalDateTime.of(2020, 8, 13, 19, 00))
                .endEventDateTime(LocalDateTime.of(2020, 8, 13, 22, 00))
                .basePrice(0)
                .maxPrice(0)
                .limitOfEnrollment(0)
                .location("서울시 강남구 일원동 마루공원 풋살장 1면")
                .build();

        // Database에 save된 후 id값이 autoincrement로 10이 생성되었다고 가정
        event.setId(10);
//        Mockito.when(eventRepository.save(event)).thenReturn(event); //mcokBean객체를 통한 후처리가 필요하므로 stubbing

        mockMvc.perform(post("/api/event03")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION)) // HeaderResultMatchers.header() : 응답 header내 항목 확인
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
        ;
    }

    /** 입력값을 통해 계산 혹은 결정되어야 되는 항목의 입력값 제한
     * 구현#1: EventDto를 이용하여 입력에 필요한 값만 수신 후 EventEntity와 객체 Mapping
     * 객체 Mappping 구현 방법
     *  - #1 : ModelMapper를 이용한 객체 Mapping (java Reflection기반 이므로 성능에 영향을 줄 수 있다.)
     *  - #2 : Reflection기반이 아닌 javaBean의 getter/setter를 이용한 값 mapping
     *  - #3 : Jackson Library내 @ignore등의 annotaion을 이용한 값 mapping
     *     - 단점 : 객체에 너무 많은 annotaion이 추가되므로 객체 관리에 용이 하지 않음
     *  객체 관리에 용이 하지 않다.
     *  목표 : 입력값을 통해 계산 혹은 결정 되어야 하는 항목의 입력값이 제한되었는지 Test
     *      - Event.id -> DB를 통해 생성된 값
     *      - Event.free -> Event.basePrice 항목에 따라 free 여부를 결정
     *      - Event.offline -> Event.location 항목에 따라 offline 여부를 결정
     *  구현#2 : SpringBoot에서 제공하는 jackson.deserialization.fail-on-unknown-properties=true 속성을 이용한 java bean binding대상에 미포함 항목 체크 및 BaeRequest 처리
     * */
    @Test
    public void createEvent_TEST_04() throws Exception {
        Event event = Event.builder()
                .id(100) //입력되면 안되는 값을 요청 파라미터에 설정하여 제한여부를 Test한다.
                .name("루나소프트 생활 체육회")
                .description("제 2회 루나 배 풋살 대회")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 06, 9, 30 ))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 8, 06, 9, 30 ))
                .beginEventDateTime(LocalDateTime.of(2020, 8, 13, 19, 00))
                .endEventDateTime(LocalDateTime.of(2020, 8, 13, 22, 00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(0)
                .location("서울시 강남구 일원동 마루공원 풋살장 1면")
                .free(true) //입력되면 안되는 값을 요청 파라미터에 설정하여 제한여부를 Test한다.
                .offline(false) //입력되면 안되는 값을 요청 파라미터에 설정하여 제한여부를 Test한다.
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        // eventRepository를 mocking한 후 eventRepository.save가 호출 될때의 Test파일에서 넘겨준 객체event와
        // EventController.createEvent04()에서 EventDto객체의 값을 mapping하기위해 생성한 event는 다른 객체이므로
        // createEvent04()의 하기 코드 중 createdEvent.getId()에서 NullPointException이 발생한다.
        // URI createdUri = linkTo(methodOn(EventController.class).createEvent04(eventDto)).slash(createdEvent.getId()).toUri();
        // 결론 : 따라서 @WebMvcTest annotation을 이용하여 필요한 Bean을 Mocking하여 진행한 slice Test가 아닌
        // @SpringBootTest annotation을 이용하여 mocking된 repositort가 아닌 실제 repository를 이용하여
        // 객체를 저장하고 반환한 객체를 중심으로 Test를 재 진행한다.
//        Mockito.when(eventRepository.save(event)).thenReturn(event); //mcokBean객체를 통한 후처리가 필요하므로 stubbing

        mockMvc.perform(post("/api/event04")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .characterEncoding(StandardCharsets.UTF_8.name())
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION)) // HeaderResultMatchers.header() : 응답 header내 항목 확인
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                /** 입력값 제한 여부 Test */
                .andExpect(jsonPath("id").value(Matchers.not(100))) //hamcreset.Matchers를 통해 응답 항목 확인
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
        ;
    }

    /** #입력받는 항목의 기본 유효성 검사
     * 구현 : @Valid와 BindingResult (또는 Errors)를 통한 기본 유효성 검사 진행 및 유효하지 못할 시 BadReqeust 처리
     *  - 유의사항 : @Valid annotation을 이용하여 유효성검사를 진행할 경우, 대상 Binding 객체는 항상 @Valid 바로 다음 인자로 사용해야한다.(Spring MVC rule)
     * */
    @Test
    public void createEventAPI_Input_Value_Validation_TEST() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        mockMvc.perform(post("/api/event05")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaTypes.HAL_JSON_VALUE)
                    .characterEncoding(StandardCharsets.UTF_8.name())
                    .content(objectMapper.writeValueAsString(eventDto))
                )
                .andExpect(status().isBadRequest());
    }

}