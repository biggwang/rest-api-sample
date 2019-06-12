package com.ryu.rest.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryu.rest.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
//@WebMvcTest // 웹과 관련된 빈들이 등록, 하지만 JpaRepository 는 등록 안 됨 웹용이 아니기 때문에
@SpringBootTest // Mocking한 DispatcherServlect 사용
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc; // mocking 되어 있는 dispatherServlet 사용

    @Autowired
    ObjectMapper objectMapper;  // json으로 바꾸어줌

    // @WebMvcTest에서 웹 관련된 빈들만 등록하지 JpaRespository 가 Bean으로 등록되지 않기 때문에 Mocking 하였음
    // 하지만 객체를 사용 할 순있어도 Mocking 하였기 때문에 Null로 나온다 이때 Stub 개념이 필요 함
    // 즉, 어떻게 나오게 해라 지정을 하는 것
    @MockBean
    EventRepository eventRepository;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
              EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 05, 16, 00, 11))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 05, 16, 00, 11))
                .beginEventDateTime(LocalDateTime.of(2019, 05, 16, 00, 11))
                .endEventDateTime(LocalDateTime.of(2019, 05, 17, 00, 11))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        /**
         * EventController > createEvent 부분에서 Event -> EventDto로 변경하면서 해당 Mocking 부분은 제거한다.
         * 제거하지 않으면 위에서 Mocking에 사용되는 Event 객체와서 EventController > createEvent 메소드내에서 사용한 Event객체가 달라서
         * event 객체가 null이 되어서 NullPointerException 오류가 발생 되기 때문이다.
         */
        //Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.PUBLISHED))
        ;
    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 05, 16, 00, 11))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 05, 16, 00, 11))
                .beginEventDateTime(LocalDateTime.of(2019, 05, 16, 00, 11))
                .endEventDateTime(LocalDateTime.of(2019, 05, 17, 00, 11))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest()) // EventDto에 정의된 값 외 값들이 request로 올 때 에러를 내고 싶으면 on-unknown-properties=true 설정 필요 (이건 선택임)
        ;
    }

    // 파라미터 값이 유효하지 않을 때 Bad Request를 발생시킨다.
    @Test
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 05, 16, 00, 11))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 05, 16, 00, 11))
                .beginEventDateTime(LocalDateTime.of(2019, 05, 19, 00, 11))
                .endEventDateTime(LocalDateTime.of(2019, 05, 17, 00, 11))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 D2 스타텁 팩토리")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

}
