package io.api.event;

import io.api.event.util.common.TestDescription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class EventApplicationTests {

    @Test
    @TestDescription("Test Active Propiles설정을 이용한 EventApplication 구동 Test")
    @DisplayName("EventApplication : Active Propiles 설정")
    void contextLoads() {
    }

}
