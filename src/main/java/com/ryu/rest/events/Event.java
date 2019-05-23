package com.ryu.rest.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 수업일지
 * ■ EventDto 분리
 * 왜? 입력값을 제한하는 어노테이션을 추가해야 하는데 어노테이션이 너무 많아 헷갈린다.
 * 입력값을 받는 Dto를 분리하여 복잡성을 줄인다.
 * 대신, 단점으로 중복이 생긴다.
 *
 */
@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")   // 상호참조 때문에 스택오버플로우 발생 방지
@Entity
public class Event {

    @Id @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING)    // ORDINAL로 하게 되면 Enum 타입에 값이 꼬일수 있으므로
    private EventStatus eventStatus;

}