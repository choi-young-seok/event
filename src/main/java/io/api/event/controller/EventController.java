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
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

    /**
     * Event 생성 API
     * @param eventDto Event 생성 요청 객체
     * @param errors JSR303을 이용한 객체 유효성 검사 시 발생한 Error(Field/Global) 반환 객체
     * @return 201 Created
     * @apiNote events-create Document : {@link }
     */
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors){
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()){
            return badRequest(errors);
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

    /**
     * Event 조회 API
     * @param id 조회 요청 Event의 ID
     * @return 200 Ok
     * @apiNote events-get Document : {@link }
     */
    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()){
            return this.notFound();
        }
        Event event = optionalEvent.get();
        EventEntityModel eventEntityModel = new EventEntityModel(event);
        eventEntityModel.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        return ResponseEntity.ok(eventEntityModel);
    }

    /**
     * Event 목록 조회 API
     * @param pageable Event 목록 조회 페이지 요청 정보
     * @param pagedResourcesAssembler 조회한 목록 정보를 Resource로 변환
     * @apiNote events-list Document : {@link }
     * @return 200 Ok
     */
    @GetMapping
    public ResponseEntity getEventList(Pageable pageable, PagedResourcesAssembler pagedResourcesAssembler){
        Page<Event> page = this.eventRepository.findAll(pageable);

        var pagedResources = pagedResourcesAssembler.toModel(page, entity -> new EventEntityModel((Event) entity));
        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(pagedResources);
    }

    /**
     * Event 수정 API
     * @param id 수정 요청 Event의 ID
     * @param eventDto Event 수정 요청 객체
     * @param errors JSR303을 이용한 객체 유효성 검사 시 발생한 Error(Field/Global) 반환 객체
     * @return 200 Ok
     * @apiNote events-update Document : {@link }
     */
    @PutMapping("{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id, @RequestBody @Valid EventDto eventDto, Errors errors){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()){
            return this.notFound();
        }

        if(errors.hasErrors()){
            return this.badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()){
            return this.badRequest(errors);
        }

        Event existingEvent = optionalEvent.get();
        this.modelMapper.map(eventDto, existingEvent);
        Event event = this.modelMapper.map(eventDto, Event.class);
        Event updatedEvent = this.eventRepository.save(event);

        WebMvcLinkBuilder selfLinkBuilder = linkTo(methodOn(EventController.class).createEvent(eventDto, errors));
        EventEntityModel eventEntityModel = new EventEntityModel(updatedEvent);
        eventEntityModel.add(selfLinkBuilder.slash(updatedEvent.getId()).withRel("get-event"));
        eventEntityModel.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventEntityModel);
    }

    /**
     * 잘못된 요청의 Bad Request 응답 처리 객체
     * @param errors 유효성 검사 후 반환된 Error(Field/Global) 객체
     * @return 400 Bad Request
     */
    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorEntityModel(errors));
    }

    /**
     * 잘못된 요청의 Not Found 응답 처리 객체
     * @return 404 Not Found
     */
    private ResponseEntity notFound() {
        URI indexUri = linkTo(methodOn(IndexController.class).index()).toUri();
        return ResponseEntity.notFound().location(indexUri).build();
    }
}

