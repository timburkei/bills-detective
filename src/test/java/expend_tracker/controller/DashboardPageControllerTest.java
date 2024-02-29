package expend_tracker.controller;

import expend_tracker.dto.dashboardPage.ChainExpenseDto;
import expend_tracker.dto.dashboardPage.MonthlyExpenseDto;
import expend_tracker.dto.dashboardPage.TagExpenseDto;
import expend_tracker.dto.dashboardPage.UserShoppingPatternDto;
import expend_tracker.service.dashboardPage.ChainExpenseService;
import expend_tracker.service.dashboardPage.MonthlyExpenseService;
import expend_tracker.service.dashboardPage.TagExpenseService;
import expend_tracker.service.dashboardPage.UserShoppingPatternService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
public class DashboardPageControllerTest {

    @Mock
    private MonthlyExpenseService monthlyExpenseService;

    @Mock
    private ChainExpenseService chainExpenseService;

    @Mock
    private TagExpenseService tagExpenseService;

    @Mock
    private UserShoppingPatternService userShoppingPatternService;


    @InjectMocks
    private DashboardPageController dashboardPageController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardPageController).build();
    }

    @Test
    public void testGetMonthlyExpenses() throws Exception {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        YearMonth yearMonth = YearMonth.from(startDate);
        given(monthlyExpenseService.calculateMonthlyExpenses(any(), any(), any()))
                .willReturn(Arrays.asList(
                        new MonthlyExpenseDto(YearMonth.of(2024, 1), BigDecimal.valueOf(360))
                ));

        mockMvc.perform(get("/api/dashboard-page/monthly-expenditure/{userId}", userId)
                        .param("startDate", "01.01.2024")
                        .param("endDate", "31.01.2024"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].month[0]").value(2024))
                .andExpect(jsonPath("$[0].month[1]").value(1))
                .andExpect(jsonPath("$[0].totalAmount").value(360));

    }

    @Test
    public void testGetChainExpenses() throws Exception {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        given(chainExpenseService.calculateExpenses(userId, startDate, endDate))
                .willReturn(Collections.singletonList(new ChainExpenseDto(1L, "Edeka", 200.0)));

        mockMvc.perform(get("/api/dashboard-page/chain-expenditure/{userId}", userId)
                        .param("startDate", "01.01.2024")
                        .param("endDate", "31.01.2024"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].chainName").value("Edeka"))
                .andExpect(jsonPath("$[0].totalExpense").value(200.0));
    }

    @Test
    public void testGetTagExpenses() throws Exception {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        given(tagExpenseService.calculateTagExpenses(userId, startDate, endDate))
                .willReturn(Collections.singletonList(new TagExpenseDto("Obst", 150.0)));

        mockMvc.perform(get("/api/dashboard-page/tag-expenditure/{userId}", userId)
                        .param("startDate", "01.01.2024")
                        .param("endDate", "31.01.2024"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].categoryName").value("Obst"))
                .andExpect(jsonPath("$[0].totalExpense").value(150.0));
    }

    @Test
    public void testGetUserShoppingPattern() throws Exception {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        UserShoppingPatternDto userShoppingPatternDto = new UserShoppingPatternDto();
        userShoppingPatternDto.setTimeGroup("06:01-12:00");
        userShoppingPatternDto.setWeekday("Montag");
        userShoppingPatternDto.setChainId(1L);
        userShoppingPatternDto.setChainName("Edeka");

        given(userShoppingPatternService.calculateUserShoppingPattern(any(), any(), any()))
                .willReturn(userShoppingPatternDto);

        mockMvc.perform(get("/api/dashboard-page/user-shopping-pattern/{userId}", userId)
                        .param("startDate", "01.01.2024")
                        .param("endDate", "31.01.2024"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.timeGroup").value("06:01-12:00"))
                .andExpect(jsonPath("$.weekday").value("Montag"))
                .andExpect(jsonPath("$.chainId").value(1))
                .andExpect(jsonPath("$.chainName").value("Edeka"));
    }


}