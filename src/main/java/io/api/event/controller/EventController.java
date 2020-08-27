package io.api.event.controller;

import io.api.event.config.CustomMediaTypes;
import io.api.event.domain.dto.event.EventDto;
import io.api.event.domain.dto.event.EventResource;
import io.api.event.domain.dto.event.EventResourceWithEntityModel;
import io.api.event.domain.entity.event.Event;
import io.api.event.repository.EventRepository;
import io.api.event.util.event.EventValidator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.methodOn;

@RequestMapping(value = "/api", produces = CustomMediaTypes.HAL_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Slf4j
public class EventController {

    @PostMapping(value = "/event")
    ResponseEntity createEvent(){

        /** Loaction URI 생성
         * 생성 요청에 대한 응답 객체로 ResponseEntity.created를 사용할 경우 uri를 필수로 return 해야한다.
         * Spring Hateoas의 ControllerLinkBuilder의 linkTo와 methodOn을 이용하여 쉽게 URI를 생성한다.
         * */
        log.info("Create Event Request");
        URI createdUri = linkTo(methodOn(EventController.class).createEvent()).slash("{id}").toUri();
        return ResponseEntity.created(createdUri).build();
    }

    @PostMapping(value = "/event02")
    ResponseEntity createEvent02(@RequestBody Event event){
        URI createdUri = linkTo(methodOn(EventController.class).createEvent02(event)).slash("{id}").toUri();
        event.setId(10);
        return ResponseEntity.created(createdUri).body(event);
    }

    /*@Autowired annotation을 이용한 주입*/
//    @Autowired
//    EventRepository eventRepository;

    /* 생성자를 이용한 Bean 주입 -> @Autowired annotaion을 이용하여 Bean을 주입하거나 생성자를 이용하여 bean을 주입*/
    private final EventRepository eventRepository;
//    public EventController(EventRepository eventRepository){
//        this.eventRepository = eventRepository;
//    }

    @PostMapping(value = "/event03")
    ResponseEntity createEvent03(@RequestBody Event event){
        Event createdEvent = this.eventRepository.save(event);
        URI createdUri = linkTo(methodOn(EventController.class).createEvent03(event)).slash(createdEvent.getId()).toUri();
        return ResponseEntity.created(createdUri).body(event);
    }

    private final ModelMapper modelMapper;

//    public EventController(EventRepository eventRepository, ModelMapper modelMapper){
//        this.eventRepository = eventRepository;
//        this.modelMapper = modelMapper;
//    }

    /* ModelMapper를 이용한 javaBean mapping */
    @PostMapping(value = "/event04")
    ResponseEntity createEvent04(@RequestBody EventDto eventDto){
        //EventDto객체를 이용하여 입력 파라미터를 수신 후 Event객체의 setter를이용하여 값을 옮기는 방법을 대체 할 modelMapper
        Event event = modelMapper.map(eventDto, Event.class);
        log.info(event.toString());
        Event createdEvent = eventRepository.save(event);
        log.info(createdEvent.toString());
        URI createdUri = linkTo(methodOn(EventController.class).createEvent04(eventDto)).slash(createdEvent.getId()).toUri();
        return ResponseEntity.created(createdUri).body(createdEvent);
    }

    /* JSR303을 이용한 요청 파라미터의 기본 유효성 검사
    * @Valid 다음에 유효성검사를 진행할 객체를 위치 시키고, 그다음 객체에 Erros객체를 위치 시켜,
    * 유효성검사에 대한 에러 값을 담는다.
    * */
    @PostMapping(value = "/event05")
    ResponseEntity createEvent05(@RequestBody @Valid EventDto eventDto, Errors errors){
        // @Valid를 통해 특정 객체에 값 Binding시 유효성 검사를 진행 하면서 error가 발생한 경우 errors객체에 error가 담긴다.
        if(errors.hasErrors()){
            // error가 있는 경우 badReqeust 처리
            return ResponseEntity.badRequest().build();
        }
        //EventDto객체를 이용하여 입력 파라미터를 수신 후 Event객체의 setter를이용하여 값을 옮기는 방법을 대체 할 modelMapper
        Event event = modelMapper.map(eventDto, Event.class);
        Event createdEvent = eventRepository.save(event);
        URI createdUri = linkTo(methodOn(EventController.class).createEvent04(eventDto)).slash(createdEvent.getId()).toUri();
        return ResponseEntity.created(createdUri).body(createdEvent);
    }

    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator){
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    /** EventValidator를 이용하여 catch한 errors에 담긴 항목을 bdoy에 담아서 리턴할 수 없는 이유
     * Errors 객체는 javaBean spec을 만족하지 못한 객체 이므로, BeanSerializer 오류 발생
     * 해당 Controller가 hal-json타입을 리턴하므로 errors 객체를 반환할 경우 BeanSerializer가 객체->json으로 변환을
     * 시도 하지만 Errors객체가 JavaBean Spec을 만족하지 못하므로 jackson.databind.InvalidDefinitionException 발생
     * 결론 : Errors객체 내 담긴 오류 내용을 Bad Reqeust의 body로 반환하기 위해서
     * */
    @PostMapping(value = "/event06")
    ResponseEntity createEvent06(@RequestBody @Valid EventDto eventDto, Errors errors){
        // @Valid를 통해 특정 객체에 값 Binding시 유효성 검사를 진행 하면서 error가 발생한 경우 errors객체에 error가 담긴다.
        if(errors.hasErrors()){
            // error가 있는 경우 badReqeust 처리
            return ResponseEntity.badRequest().body(errors);
        }

        eventValidator.validate(eventDto, errors);

        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        //EventDto객체를 이용하여 입력 파라미터를 수신 후 Event객체의 setter를이용하여 값을 옮기는 방법을 대체 할 modelMapper
        Event event = modelMapper.map(eventDto, Event.class);
        Event createdEvent = eventRepository.save(event);
        URI createdUri = linkTo(methodOn(EventController.class).createEvent04(eventDto)).slash(createdEvent.getId()).toUri();
        return ResponseEntity.created(createdUri).body(createdEvent);
    }


    @PostMapping(value = "/event07")
    ResponseEntity createEvent07(@RequestBody @Valid EventDto eventDto, Errors errors){
        // @Valid를 통해 특정 객체에 값 Binding시 유효성 검사를 진행 하면서 error가 발생한 경우 errors객체에 error가 담긴다.
        if(errors.hasErrors()){
            // error가 있는 경우 badReqeust 처리
            return ResponseEntity.badRequest().body(errors);
        }

        eventValidator.validate(eventDto, errors);

        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        //EventDto객체를 이용하여 입력 파라미터를 수신 후 Event객체의 setter를이용하여 값을 옮기는 방법을 대체 할 modelMapper
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event createdEvent = eventRepository.save(event);
        URI createdUri = linkTo(methodOn(EventController.class).createEvent04(eventDto)).slash(createdEvent.getId()).toUri();
        return ResponseEntity.created(createdUri).body(createdEvent);
    }

    //Spring HATEOAS의 RepresentationModel을 통한 ResponseBody에 link정보 포함하기
    @PostMapping(value = "/event08")
    ResponseEntity createEvent08(@RequestBody @Valid EventDto eventDto, Errors errors){
        // @Valid를 통해 특정 객체에 값 Binding시 유효성 검사를 진행 하면서 error가 발생한 경우 errors객체에 error가 담긴다.
        if(errors.hasErrors()){
            // error가 있는 경우 badReqeust 처리
            return ResponseEntity.badRequest().body(errors);
        }

        eventValidator.validate(eventDto, errors);

        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        //EventDto객체를 이용하여 입력 파라미터를 수신 후 Event객체의 setter를이용하여 값을 옮기는 방법을 대체 할 modelMapper
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event createdEvent = eventRepository.save(event);

        ControllerLinkBuilder selfLinkBuilder = linkTo(methodOn(EventController.class).createEvent08(eventDto, errors));
        URI createdUri = selfLinkBuilder.toUri();

        EventResource eventResource = new EventResource(event);
        eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(selfLinkBuilder.slash(createdEvent.getId()).withRel("query-event"));
        eventResource.add(selfLinkBuilder.slash(createdEvent.getId()).withRel("update-event"));

        return ResponseEntity.created(createdUri).body(eventResource);
    }

    //Spring HATEOAS의 EntityModel 통한 ResponseBody에 link정보 포함하기
    @PostMapping(value = "/event09")
    public ResponseEntity createEvent09(@RequestBody @Valid EventDto eventDto, Errors errors){
        // @Valid를 통해 특정 객체에 값 Binding시 유효성 검사를 진행 하면서 error가 발생한 경우 errors객체에 error가 담긴다.
        if(errors.hasErrors()){
            // error가 있는 경우 badReqeust 처리
            return ResponseEntity.badRequest().body(errors);
        }

        eventValidator.validate(eventDto, errors);

        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        //EventDto객체를 이용하여 입력 파라미터를 수신 후 Event객체의 setter를이용하여 값을 옮기는 방법을 대체 할 modelMapper
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event createdEvent = eventRepository.save(event);

        ControllerLinkBuilder selfLinkBuilder = linkTo(methodOn(EventController.class).createEvent09(eventDto, errors));
        URI createdUri = selfLinkBuilder.toUri();

        EventResourceWithEntityModel eventResourceWithEntityModel = new EventResourceWithEntityModel(event);
        eventResourceWithEntityModel.add(selfLinkBuilder.slash(createdEvent.getId()).withRel("query-event"));
        eventResourceWithEntityModel.add(selfLinkBuilder.slash(createdEvent.getId()).withRel("update-event"));

        return ResponseEntity.created(createdUri).body(eventResourceWithEntityModel);
    }
}

