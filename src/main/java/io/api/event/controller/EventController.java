package io.api.event.controller;

import io.api.event.config.CustomMediaTypes;
import io.api.event.domain.dto.event.EventDto;
import io.api.event.domain.dto.event.EventEntityModel;
import io.api.event.domain.entity.event.Event;
import io.api.event.repository.EventRepository;
import io.api.event.util.event.EventValidator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

//import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;
//import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.methodOn;

@RequestMapping(value = "/api/events", produces = CustomMediaTypes.HAL_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Slf4j
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator){
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){
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


        WebMvcLinkBuilder selfLinkBuilder = linkTo(methodOn(EventController.class).createEvent(eventDto, errors));
        URI createdUri = selfLinkBuilder.toUri();

        EventEntityModel eventEntityModel = new EventEntityModel(event);
        eventEntityModel.add(selfLinkBuilder.slash(createdEvent.getId()).withRel("query-event"));
        eventEntityModel.add(selfLinkBuilder.slash(createdEvent.getId()).withRel("update-event"));
        eventEntityModel.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));

        return ResponseEntity.created(createdUri).body(eventEntityModel);
    }
}

