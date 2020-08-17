package io.api.event.controller;

import io.api.event.domain.dto.event.EventDto;
import io.api.event.domain.entity.event.Event;
import io.api.event.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        log.info("event03 start");
        Event createdEvent = this.eventRepository.save(event);
        URI createdUri = linkTo(methodOn(EventController.class).createEvent03(event)).slash(createdEvent.getId()).toUri();
        return ResponseEntity.created(createdUri).body(event);
    }

    private final ModelMapper modelMapper;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper){
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
    }

    /* ModelMapper를 이용한 javaBean mapping */
    @PostMapping(value = "/event04")
    ResponseEntity createEvent04(@RequestBody EventDto eventDto){
        log.info("event04 start");
        //EventDto객체를 이용하여 입력 파라미터를 수신 후 Event객체의 setter를이용하여 값을 옮기는 방법을 대체 할 modelMapper
        Event event = modelMapper.map(eventDto, Event.class);
        log.info(event.toString());
        Event createdEvent = eventRepository.save(event);
        log.info(createdEvent.toString());
        URI createdUri = linkTo(methodOn(EventController.class).createEvent04(eventDto)).slash(createdEvent.getId()).toUri();
        return ResponseEntity.created(createdUri).body(createdEvent);
    }
}
