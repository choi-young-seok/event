package io.api.event.controller.event.docs;

import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static io.api.event.common.document.DocumentFormatGenerator.DATETIME_FORMAT;
import static io.api.event.common.document.DocumentFormatGenerator.getDateTimeFormat;
import static io.api.event.util.common.constant.DocsInfo.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class EventDocumentGenerator {

    public static RestDocumentationResultHandler createEventDocument() {
        return document("{class-name}/{method-name}",
                links(
                        linkWithRel(SELF).description("link to self"),
                        linkWithRel(PROFILE).description("link to profile"),
                        linkWithRel(GET_EVENT_LIST).description("link to query ans event"),
                        linkWithRel(UPDATE_EVENT).description("link to update an existing event")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("content type haeder")
                ),
                requestFields(
                        fieldWithPath("name").description("Name of new event"),
                        fieldWithPath("description").description("Description of new event"),
                        fieldWithPath("beginEnrollmentDateTime").description("Date time of begin enrollment of new event").type(DATETIME_FORMAT).attributes(getDateTimeFormat()),
                        fieldWithPath("closeEnrollmentDateTime").description("Date time of close enrollment of new event").type(DATETIME_FORMAT).attributes(getDateTimeFormat()),
                        fieldWithPath("beginEventDateTime").description("Date time of begin of new event").type(DATETIME_FORMAT).attributes(getDateTimeFormat()),
                        fieldWithPath("endEventDateTime").description("Date time of close of new event").type(DATETIME_FORMAT).attributes(getDateTimeFormat()),
                        fieldWithPath("location").description("Location of new event").optional(),
                        fieldWithPath("basePrice").description("Base Price of new event"),
                        fieldWithPath("maxPrice").description("MaxPrice of new event"),
                        fieldWithPath("limitOfEnrollment").description("Limit of enrollment of new event")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.LOCATION).description("Location header"),
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("Response content type")
                ),
                relaxedResponseFields(
                        fieldWithPath("id").description("identifier of new event"),
                        fieldWithPath("name").description("Name of new event"),
                        fieldWithPath("description").description("Description of new event"),
                        fieldWithPath("beginEnrollmentDateTime").description("Date time of begin enrollment of new event"),
                        fieldWithPath("closeEnrollmentDateTime").description("Date time of close enrollment of new event"),
                        fieldWithPath("beginEventDateTime").description("Date time of begin of new event"),
                        fieldWithPath("endEventDateTime").description("Date time of close of new event"),
                        fieldWithPath("location").description("Location of new event"),
                        fieldWithPath("basePrice").description("Base Price of new event"),
                        fieldWithPath("maxPrice").description("MaxPrice of new event"),
                        fieldWithPath("limitOfEnrollment").description("Limit of enrollment of new event"),
                        fieldWithPath("offline").description("it tells if this event is free"),
                        fieldWithPath("free").description("it tells if this event is offline"),
                        fieldWithPath("eventStatus").description("eventStatus of new event"),
                        fieldWithPath("manager").description("manager info of event"),
                        fieldWithPath("_links.self.href").description("link to self"),
                        fieldWithPath("_links.get-event-list.href").description("link to query an event"),
                        fieldWithPath("_links.update-event.href").description("link to update an existing event"),
                        fieldWithPath("_links.profile.href").description("link to profile")
                )
        );
    }

    public static RestDocumentationResultHandler getEventList() {
        return document("{class-name}/{method-name}"
        );
    }

    public static RestDocumentationResultHandler getAnEvent() {
        return document("{class-name}/{method-name}",
                links(
                        linkWithRel(SELF).description("link to self"),
                        linkWithRel(PROFILE).description("link to profile")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("content type haeder")
                ),
                pathParameters(
                        parameterWithName("id").description("이벤트 ID")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("Response content type")
                ),
                responseFields(
                        fieldWithPath("id").description("identifier of new event"),
                        fieldWithPath("name").description("Name of new event"),
                        fieldWithPath("description").description("Description of new event"),
                        fieldWithPath("beginEnrollmentDateTime").description("Date time of begin enrollment of new event"),
                        fieldWithPath("closeEnrollmentDateTime").description("Date time of close enrollment of new event"),
                        fieldWithPath("beginEventDateTime").description("Date time of begin of new event"),
                        fieldWithPath("endEventDateTime").description("Date time of close of new event"),
                        fieldWithPath("location").description("Location of new event"),
                        fieldWithPath("basePrice").description("Base Price of new event"),
                        fieldWithPath("maxPrice").description("MaxPrice of new event"),
                        fieldWithPath("limitOfEnrollment").description("Limit of enrollment of new event"),
                        fieldWithPath("offline").description("it tells if this event is free"),
                        fieldWithPath("free").description("it tells if this event is offline"),
                        fieldWithPath("eventStatus").description("eventStatus of new event"),
                        fieldWithPath("manager").description("manager info of event"),
                        fieldWithPath("_links.self.href").description("link to self"),
                        fieldWithPath("_links.profile.href").description("link to profile")
                )
        );
    }

    public static RestDocumentationResultHandler updateEvent() {
        return document("{class-name}/{method-name}",
                links(
                        linkWithRel(SELF).description("link to self"),
                        linkWithRel(PROFILE).description("link to profile"),
                        linkWithRel(GET_AN_EVENT).description("link to query ans event")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("content type haeder")
                ),
                pathParameters(
                        parameterWithName("id").description("이벤트 ID")
                ),
                requestFields(
                        fieldWithPath("name").description("Name of new event"),
                        fieldWithPath("description").description("Description of new event"),
                        fieldWithPath("beginEnrollmentDateTime").description("Date time of begin enrollment of new event").type(DATETIME_FORMAT).attributes(getDateTimeFormat()),
                        fieldWithPath("closeEnrollmentDateTime").description("Date time of close enrollment of new event").type(DATETIME_FORMAT).attributes(getDateTimeFormat()),
                        fieldWithPath("beginEventDateTime").description("Date time of begin of new event").type(DATETIME_FORMAT).attributes(getDateTimeFormat()),
                        fieldWithPath("endEventDateTime").description("Date time of close of new event").type(DATETIME_FORMAT).attributes(getDateTimeFormat()),
                        fieldWithPath("location").description("Location of new event").optional(),
                        fieldWithPath("basePrice").description("Base Price of new event"),
                        fieldWithPath("maxPrice").description("MaxPrice of new event"),
                        fieldWithPath("limitOfEnrollment").description("Limit of enrollment of new event")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("Response content type")
                ),
                responseFields(
                        fieldWithPath("id").description("identifier of new event"),
                        fieldWithPath("name").description("Name of new event"),
                        fieldWithPath("description").description("Description of new event"),
                        fieldWithPath("beginEnrollmentDateTime").description("Date time of begin enrollment of new event"),
                        fieldWithPath("closeEnrollmentDateTime").description("Date time of close enrollment of new event"),
                        fieldWithPath("beginEventDateTime").description("Date time of begin of new event"),
                        fieldWithPath("endEventDateTime").description("Date time of close of new event"),
                        fieldWithPath("location").description("Location of new event"),
                        fieldWithPath("basePrice").description("Base Price of new event"),
                        fieldWithPath("maxPrice").description("MaxPrice of new event"),
                        fieldWithPath("limitOfEnrollment").description("Limit of enrollment of new event"),
                        fieldWithPath("offline").description("it tells if this event is free"),
                        fieldWithPath("free").description("it tells if this event is offline"),
                        fieldWithPath("eventStatus").description("eventStatus of new event"),
                        fieldWithPath("manager").description("manager info of event"),
                        fieldWithPath("_links.self.href").description("link to self"),
                        fieldWithPath("_links.get-an-event.href").description("link to query an event"),
                        fieldWithPath("_links.profile.href").description("link to profile")
                )
        );
    }

    public static RestDocumentationResultHandler deleteEvent() {
        return document("{class-name}/{method-name}",
                pathParameters(
                        parameterWithName("id").description("This is post id")
                )
        );
    }

}
