package com.ryu.rest.events;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
    }


    /**
     * 수업일지
     * ■ Event -> EventDto 변경
     * - 왜? 유효성검사(입력값 제한)를 하기 위함이다. Dto를 만들고 값을 제한하는 어노테이션을 만들면 쉽게 입력값을 제한 할 수 있다.
     *
     * - EventDto로 변경하였더니 기존 eventRepository.save(event) event 객체를 받고 있었는데 다 수정해야 하나??
     * - 아니다! ModelMapper 라이브러리를 추가하면 Event 도메인클래스를 EventDto로 변경 할 수 있다.
     *
     */
    @PostMapping
    public ResponseEntity createEvent(@RequestBody EventDto eventDto) {
        Event event = modelMapper.map(eventDto, Event.class);
        Event newEvent = this.eventRepository.save(event);
        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        return ResponseEntity.created(createdUri).build();
    }
}
