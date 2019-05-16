package com.ryu.rest.events;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> { // 이렇게만 해도 빈이 등록된다.

}
