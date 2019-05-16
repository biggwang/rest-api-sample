package com.ryu.rest.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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
@WebMvcTest // 웹과 관련된 빈들이 등록, 하지만 JpaRepository 는 등록 안됨 웹용이 아니기 때문에
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc; // mocking 되어 있는 dispatherServlet 사용

    @Autowired
    ObjectMapper objectMapper;  // json으로 바꾸어줌

    // @WebMvcTest에서 JpaRespository 가 Bean으로 등록되지 않기 때문에 Mocking 하였음
    // 하지만 객체를 사용 할 순있어도 값을 Null로 나오기 때문에 이때 Stub 개념이 필요 함
    // 즉, 어떻게 나오게 해라 지정을 하는 것
    @MockBean
    EventRepository eventRepository;

    @Test
    public void createEvent() throws Exception {
        Event event = Event.builder()
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
        event.setId(10);
        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
        ;
    }
}
