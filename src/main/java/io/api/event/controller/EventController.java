package io.api.event.controller;

import io.api.event.config.CustomMediaTypes;
import io.api.event.domain.dto.event.EventDto;
import io.api.event.domain.dto.event.EventEntityModel;
import io.api.event.domain.entity.event.Event;
import io.api.event.repository.EventRepository;
import io.api.event.util.common.ErrorEntityModel;
import io.api.event.util.event.EventValidator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

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
        if(errors.hasErrors()){
            return badReqeust(errors);
        }

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()){
            return badReqeust(errors);
        }

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

//    @GetMapping
//    public ResponseEntity queryEvents(Pageable pageable){
//        Page<Event> page = this.eventRepository.findAll(pageable);
//        return ResponseEntity.ok(page);
//    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler pagedResourcesAssembler){

        Page<Event> page = this.eventRepository.findAll(pageable);

//        PagedModel pagedResources = pagedResourcesAssembler.toModel(page);
        var pagedResources = pagedResourcesAssembler.toModel(page, entity -> new EventEntityModel((Event) entity));
        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(pagedResources);
    }

    private ResponseEntity badReqeust(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorEntityModel(errors));
    }
}

