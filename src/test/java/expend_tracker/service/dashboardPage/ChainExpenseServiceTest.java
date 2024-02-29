package expend_tracker.service.dashboardPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import expend_tracker.repositories.InvoiceRepository;
import expend_tracker.dto.dashboardPage.ChainExpenseDto;
import expend_tracker.service.dashboardPage.ChainExpenseService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ChainExpenseServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private ChainExpenseService chainExpenseService;

    @Test
    void calculateExpenses_Success() {
        String userId = "testUserId";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(30);
        List<ChainExpenseDto> mockExpenses = Arrays.asList(
                new ChainExpenseDto(1L, "Chain 1", 150.0),
                new ChainExpenseDto(2L, "Chain 2", 200.0)
        );

        when(invoiceRepository.calculateExpensesForUserAndPeriod(userId, startDate, endDate))
                .thenReturn(mockExpenses);

        List<ChainExpenseDto> expenses = chainExpenseService.calculateExpenses(userId, startDate, endDate);

        assertEquals(mockExpenses.size(), expenses.size());
        assertEquals(mockExpenses, expenses);
    }

    @Test
    void calculateExpenses_Error() {
        String userId = "testUserId";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(30);

        when(invoiceRepository.calculateExpensesForUserAndPeriod(userId, startDate, endDate))
                .thenThrow(new RuntimeException("Database error"));

        List<ChainExpenseDto> expenses = chainExpenseService.calculateExpenses(userId, startDate, endDate);

        assertTrue(expenses.isEmpty());
    }
}