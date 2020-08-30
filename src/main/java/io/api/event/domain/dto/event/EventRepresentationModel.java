package io.api.event.domain.dto.event;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.api.event.domain.entity.event.Event;
import org.springframework.hateoas.RepresentationModel;

/**
 * SpringBoot 및 HATEOAS 버전 업그레이드에 따른 클래스명 변경
 *  - ResourceSupport changed to RepresentationModel
 *  - Resource changed to EntityModel
 *  - Resources changed to CollectionModel
 *  - PagedResources changed to PagedModel
 *  - ResourceAssembler changed to RepresentationModelAssembler
 * official docs URL : https://docs.spring.io/spring-hateoas/docs/current/reference/html/
 * 참고 URL : https://stackoverflow.com/questions/25352764/hateoas-methods-not-found
 * */
/**
 * Spring에서 ObjectMapper가 BeanSerializr를 이용하여 Object를 Json으로 Serialization할 때
 * BeanSerializr는 기본적으로 Compose객체(다른 필드들을 포함하는 객체ex:vo,dto등의 domain객체)명으로 하위 필드들을 감싸서
 * 직렬화 하므로 compose객체의 class명으로 감싸진 형태의 json형태로 직렬화 한다.
 *
 * 이슈 : 응답내 json항목에 class명을 제외하기 위해서 @JsonUnwrapped를 사용해서 직렬화시 객체의 class명을 제외한다.
 *
 * */
public class EventRepresentationModel extends RepresentationModel {

    @JsonUnwrapped
    private Event event;

    public EventRepresentationModel(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
