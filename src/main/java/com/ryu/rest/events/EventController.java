package com.ryu.rest.events;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }



    /**
     * 수업일지
     * ■ Event -> EventDto 변경
     * - 왜? 유효성검사(입력값 제한)를 하기 위함이다. Dto를 만들고 값을 제한하는 어노테이션을 만들면 쉽게 입력값을 제한 할 수 있다.
     *
     * - EventDto로 변경하였더니 기존 eventRepository.save(event) event 객체를 받고 있었는데 다 수정해야 하나??
     * - 아니다! ModelMapper 라이브러리를 추가하면 Event 도메인클래스를 EventDto로 변경 할 수 있다.
     *
     * ■ Json Object로 받고 싶다면? Json 문자열을 Serilalize를 해줘야함
     */
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if (errors.hasErrors()) {

            /**
             * 수업일지 (제목: Bad Request 응답, 시간:05:05)
             *
             * ■ Errors 객체를 body담으면 오류 나는 이유
             *
             * 에러 정보를 body에 담아 리턴하고 싶은데 아무 조치 하지 않으면 Json Paser Error가 발생 한다.
             * Eveent 객체는 리턴하면 Json으로 변환 되는데 왜 Errors 객체는 오류가 발생 할까??
             *
             * 그 이유는, Json으로 변환해 주는 과정이 serilalize 라고 하는데 바로 ObjectMapper가 해준다.
             * 그 ObjectMapper에 여러 seriliaizer에 등록 되어 있지만 Event 객체는 BeanSerializer로 등록되어 있지만 Errors객체는 동록되어 있지 않아 발생하는 오류이다.
             *
             * 그래서 수동으로 ObjectMapper에 serialize 할 수 있게 해주는 클래스가 ErrorsSerializer.java 이다.
             */
            return ResponseEntity.badRequest().body(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Event event = modelMapper.map(eventDto, Event.class);
        Event newEvent = this.eventRepository.save(event);



        /**
         * 수업일지 (제목: 스프링 HATEOAS 소개, 시간:04:40)
         *
         * ■ HATEOAS 란
         * 어플리케이션 상태에 따른 URI/URL 정보를 클라이언트에게 제공하며
         * 클라이언트는 어플리케이션 상태에 따른 URI/URL 정보를 받아 볼 수 있다.
         *
         * 예를들어, 인증이 되지 않았을 때와 인증이 되었을 때 HATEOAS 정보는 다르다.
        **/
        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createdUri).build();
    }
}
