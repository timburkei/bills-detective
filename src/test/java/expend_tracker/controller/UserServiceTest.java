package expend_tracker.controller;

import expend_tracker.dto.userService.ChainDto;
import expend_tracker.service.userService.ChainService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.*;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @Mock
    private ChainService chainService;

    @InjectMocks
    private UserServiceController controller;

     private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testGetChainsByUserIdWithDates() throws Exception {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        Set<ChainDto> chainDtos = new HashSet<>();
        chainDtos.add(new ChainDto(1L, "Edeka"));
        chainDtos.add(new ChainDto(2L, "Rewe"));

        given(chainService.getChainsForUser(userId, startDate, endDate)).willReturn(chainDtos);

        mockMvc.perform(get("/api/user-service/getChainFor/{userId}", userId)
                        .param("startDate", "01.01.2024")
                        .param("endDate", "31.12.2024"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Edeka", "Rewe")));
    }

    @Test
    public void testGetChainsByUserIdWithoutDates() throws Exception {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        Set<ChainDto> chainDtos = new HashSet<>();
        chainDtos.add(new ChainDto(1L, "Edeka"));
        chainDtos.add(new ChainDto(2L, "Rewe"));

        given(chainService.getChainsForUser(userId, null, null)).willReturn(chainDtos);

        mockMvc.perform(get("/api/user-service/getChainFor/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Edeka", "Rewe")));
    }

}
