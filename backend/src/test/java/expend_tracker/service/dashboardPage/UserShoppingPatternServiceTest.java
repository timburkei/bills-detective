package expend_tracker.service.dashboardPage;

import expend_tracker.dto.dashboardPage.ChainExpenseDto;
import expend_tracker.dto.dashboardPage.UserShoppingPatternDto;
import expend_tracker.model.Invoice;
import expend_tracker.repositories.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Time;
import java.time.LocalTime;
import expend_tracker.model.Invoice.DayOfWeek;

@ExtendWith(MockitoExtension.class)
public class UserShoppingPatternServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private UserShoppingPatternService userShoppingPatternService;

    @BeforeEach
    public void setup() {
        Invoice invoice1 = new Invoice();
        invoice1.setInvoiceTime(Time.valueOf(LocalTime.of(10, 0)));
        invoice1.setWeekday(DayOfWeek.fromJavaTimeDayOfWeek(java.time.DayOfWeek.MONDAY));
        Invoice invoice2 = new Invoice();
        invoice2.setInvoiceTime(Time.valueOf(LocalTime.of(11, 0)));
        invoice2.setWeekday(DayOfWeek.fromJavaTimeDayOfWeek(java.time.DayOfWeek.TUESDAY));

        given(invoiceRepository.findByUserIdAndInvoiceDateBetween(any(), any(), any()))
                .willReturn(Arrays.asList(invoice1, invoice2));
        given(invoiceRepository.calculateExpensesForUserAndPeriod(any(), any(), any()))
                .willReturn(Collections.singletonList(new ChainExpenseDto(1L, "Edeka", 200.0)));
    }

    @Test
    public void testCalculateUserShoppingPattern() {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        UserShoppingPatternDto userShoppingPatternDto = userShoppingPatternService.calculateUserShoppingPattern(userId, startDate, endDate);

        assertEquals("06:01-12:00", userShoppingPatternDto.getTimeGroup());
        assertEquals("Montag", userShoppingPatternDto.getWeekday());
        assertEquals(1L, userShoppingPatternDto.getChainId());
        assertEquals("Edeka", userShoppingPatternDto.getChainName());
    }
}