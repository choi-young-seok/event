package io.api.event.controller;

import io.api.event.domain.dto.event.EventDto;
import io.api.event.domain.entity.event.Event;
import io.api.event.repository.EventRepository;
import io.api.event.util.event.EventValidator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
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

@RequestMapping(value = "/api", produces = MediaTypes.HAL_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(value = "/event06")
    ResponseEntity createEvent06(@RequestBody @Valid EventDto eventDto, Errors errors){
        // @Valid를 통해 특정 객체에 값 Binding시 유효성 검사를 진행 하면서 error가 발생한 경우 errors객체에 error가 담긴다.
        if(errors.hasErrors()){
            // error가 있는 경우 badReqeust 처리
            return ResponseEntity.badRequest().build();
        }

        eventValidator.validate(eventDto, errors);

        if(errors.hasErrors()){
            return ResponseEntity.badRequest().build();
        }

        //EventDto객체를 이용하여 입력 파라미터를 수신 후 Event객체의 setter를이용하여 값을 옮기는 방법을 대체 할 modelMapper
        Event event = modelMapper.map(eventDto, Event.class);
        Event createdEvent = eventRepository.save(event);
        URI createdUri = linkTo(methodOn(EventController.class).createEvent04(eventDto)).slash(createdEvent.getId()).toUri();
        return ResponseEntity.created(createdUri).body(createdEvent);
    }
}
