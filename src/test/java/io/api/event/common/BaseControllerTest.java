package io.api.event.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.api.event.config.RestDocsConfiguration;
import io.api.event.config.TestConstants;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 2020-09-15
 *  - Controller Test Code에서 반복적으로 사용되는 설정 부를 추출 후 해당 클래스를 상속 받아 코드 중복 제거
 *
 */
@SpringBootTest // 통합테스트 : Application 설정을 이용하여 Test 환경 구성
@AutoConfigureMockMvc // @SpringBootTest annotation을 이용한 통합테스트 진행 시 해당 TC내에서 MockMvc를 주입하기위한 annotation
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles(TestConstants.TEST) // Test Application 환경 설정
@Disabled // jUnit4의 @Ignore 대체 annotation : Test를 가지고 있는 class로 간주되지 않도록 설정
@TestMethodOrder(MethodOrderer.Alphanumeric.class) // Test method name을 이용한 실행순서 정렬
public class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ModelMapper modelMapper;

}
