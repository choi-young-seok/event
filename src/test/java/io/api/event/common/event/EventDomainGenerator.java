package io.api.event.common.event;

import io.api.event.domain.entity.event.Event;
import io.api.event.domain.entity.event.EventStatus;
import io.api.event.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class EventDomainGenerator {

    @Autowired
    EventRepository eventRepository;

    public Event generatedEvent(int index) {
        Event event = Event.builder()
                .name("루나소프트 생활 체육회 : " + index)
                .description("제 2회 루나 배 풋살 대회 : " + index)
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 8, 6, 9, 30 ))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 8, 7, 9, 30 ))
                .beginEventDateTime(LocalDateTime.of(2020, 8, 13, 19, 0))
                .endEventDateTime(LocalDateTime.of(2020, 8, 13, 22, 0))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(0)
                .location("서울시 강남구 일원동 마루공원 풋살장 1면")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        Event createdEvent = eventRepository.save(event);
        return createdEvent;
    }
}
