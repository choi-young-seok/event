package io.api.event.domain.entity.event;

import io.api.event.util.common.TestDescription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    /** Builder를 이용한 Event Entity 생성 Test
     * */
    @Test
    @TestDescription("Builder를 이용한 Event Entity 생성 Test")
    @DisplayName("Evnet Entity : Builder 객체 생성")
    public void eventEntity_Builder_Test(){
        Event event = Event.builder().build();
        assertThat(event).isNotNull();
    }

    @Test
    @TestDescription("Java bean을 이용한 Event Entity 생성 Test")
    @DisplayName("Evnet Entity : Builder 값 할당")
    public void eventEntity_Builder_setValue_Test(){
        // Given
        String name = "Event";
        String description = "SpringBoot";
        Event event = Event.builder()
                .name(name)
                .description(description)
                .build();

        assertThat(event).isNotNull();
    }

    @Test
    @TestDescription("Java bean을 이용한 Event Entity 생성 Test")
    @DisplayName("Evnet Entity : Java Bean 객체 생성")
    public void eventEntity_JavaBean_Test(){
        Event event = new Event();
        assertThat(event).isNotNull();
    }

    @Test
    @TestDescription("Java bean을 이용한 Event Entity 생성 및 값 할당 Test")
    @DisplayName("Evnet Entity : Java Bean 값 할당")
    public void eventEntity_JavaBean_setValue_Test(){
        Event event = new Event();
        String name = "Event";
        String description = "SpringBoot";

        event.setName(name);
        event.setDescription(description);

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @ParameterizedTest
    @MethodSource("setIsFreeTestParameters")
    @TestDescription("jUnit5 ParameterizedTest를 이용한 Event.basePrice/Event.maxPrice 필드 입력 값에 따른 Event.free true/false 설정 유무 확인")
    @DisplayName("Evnet Entity : 가격 항목 입력에 따른 free 항목 결정")
    public void isFree_Test(int basePrice, int maxPrice, boolean isFree){
        // Given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        // When
        event.update();

        // Then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    private static Stream<Arguments> setIsFreeTestParameters(){
        return Stream.of(
                Arguments.of(0, 0, true),
                Arguments.of(100, 0, false),
                Arguments.of(0, 100, false),
                Arguments.of(100, 200, false)
        );
    }

    @ParameterizedTest
    @MethodSource("setIsOfflineTestParameters")
    @TestDescription("jUnit5 ParameterizedTest를 이용한 Event.location항목 입력값에 따른 Event.offline true/false 설정 유무 확인")
    @DisplayName("Evnet Entity : 위치 항목 입력에 따른 offline 항목 결정")
    public void isOffline_Test(String location, boolean isOffline){
        // Given
        Event event = Event.builder()
                .location(location)
                .build();

        // When
        event.update();

        // Then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    private static Stream<Arguments> setIsOfflineTestParameters(){
        return Stream.of(
            Arguments.of("서울특별시 서초구 반포동", true),
            Arguments.of(null, false),
            Arguments.of("   ", false)
        );
    }
}