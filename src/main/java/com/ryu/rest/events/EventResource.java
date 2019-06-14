package com.ryu.rest.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

// Refactoring 전
/*public class EventResource extends ResourceSupport {

    // 이렇게 하지 않으면
    // Json 결과가
    // {
    //      "event": {
    //             ......
    // 이렇게 event로 wrapping 되어 진다. 쓸데없이
    @JsonUnwrapped
    private Event event;

    public EventResource(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}*/

// Refactoring 후
// 이미 Resource 객체 안에 @JsonUnwrapped 되어 있기 때문에 이렇게 사용하는게 더 깔끔하다.
// 객체를 new 해서 생성하고 bean으로 관리하지 않는다느고 말하는데 왜 일까??
public class EventResource extends Resource<Event> {

    public EventResource(Event event, Link... links) {
        super(event, links);
        // self-relation은 항상 나오기 때문에 매번 코드를 생성하지 않고 여기다가 일괄적으로 생성한다.
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
}