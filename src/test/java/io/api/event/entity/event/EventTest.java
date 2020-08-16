package io.api.event.entity.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    /** Builder를 이용한 Event Entity 생성 Test
     * */
    @Test
    public void eventEntity_Builder_Test(){
        Event event = Event.builder().build();
        assertThat(event).isNotNull();
    }

    @Test
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

    /** Java bean을 이용한 Event Entity 생성 Test
     * */
    @Test
    public void eventEntity_JavaBean_Test(){
        Event event = new Event();
        assertThat(event).isNotNull();
    }

    /** Java bean을 이용한 Event Entity 생성 및 값 할당 Test
     * */
    @Test
    public void eventEntity_JavaBean_setValue_Test_01(){
        Event event = new Event();
        event.setName("Event");
        event.setDescription("SpringBoot");

        assertThat(event.getName()).isEqualTo("Event");
        assertThat(event.getDescription()).isEqualTo("SpringBoot");
    }

    /** 하드코딩 된 문자열 지역변수로 리펙토링
     * 지역변수 리펙토링 대상 영역 지정 후 -> ctrl + alt + v
     * 영역 블록 단축키 -> ctrl + w
     * */
    @Test
    public void eventEntity_JavaBean_setValue_Test_02(){
        Event event = new Event();
        String name = "Event";
        String description = "SpringBoot";

        event.setName(name);
        event.setDescription(description);

        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }
}