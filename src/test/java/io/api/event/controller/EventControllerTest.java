package io.api.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.api.event.entity.event.Event;
import io.api.event.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
@WebMvcTest
//@AutoConfigureMockMvc
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
    @MockBean
    EventRepository eventRepository;

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
        Mockito.when(eventRepository.save(event)).thenReturn(event); //mcokBean객체를 통한 후처리가 필요하므로 stubbing

        mockMvc.perform(post("/api/event03")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION)) // HeaderResultMatchers.header() : 응답 header내 항목 확인
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
        ;
    }

}