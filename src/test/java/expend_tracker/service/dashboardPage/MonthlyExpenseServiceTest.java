package expend_tracker.service.dashboardPage;

import expend_tracker.model.Invoice;
import expend_tracker.model.Product;
import expend_tracker.model.Store;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import expend_tracker.repositories.InvoiceItemRepository;
import expend_tracker.dto.dashboardPage.MonthlyExpenseDto;
import expend_tracker.service.dashboardPage.MonthlyExpenseService;
import expend_tracker.model.InvoiceItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;


@ExtendWith(MockitoExtension.class)
class MonthlyExpenseServiceTest {

    @Mock
    private InvoiceItemRepository invoiceItemRepository;

    @InjectMocks
    private MonthlyExpenseService monthlyExpenseService;

    @Test
    void calculateMonthlyExpenses_Success() {
        String userId = "auth0|65630d5317b4bdb501144ab5";

        Store mockStore = mock(Store.class);
        Invoice mockInvoice = mock(Invoice.class);
        Product mockProduct = mock(Product.class);

        List<InvoiceItem> invoiceItems = Arrays.asList(
                new InvoiceItem("Bio Apfel", 100.0, LocalDate.of(2023, 1, 15), mockStore, mockInvoice, mockProduct),
                new InvoiceItem("Bio Gurke", 200.0, LocalDate.of(2023, 2, 15), mockStore, mockInvoice, mockProduct)
        );

        when(invoiceItemRepository.findByInvoiceUserId(userId)).thenReturn(invoiceItems);

        List<MonthlyExpenseDto> result = monthlyExpenseService.calculateMonthlyExpenses(userId, null, null);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.getMonth().equals(YearMonth.of(2023, 1)) && dto.getTotalAmount().compareTo(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_UP)) == 0));
        assertTrue(result.stream().anyMatch(dto -> dto.getMonth().equals(YearMonth.of(2023, 2)) && dto.getTotalAmount().compareTo(BigDecimal.valueOf(200.00).setScale(2, RoundingMode.HALF_UP)) == 0));
    }


    @Test
    void calculateMonthlyExpenses_Error() {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 31);

        when(invoiceItemRepository.findByInvoiceUserIdAndDateBetween(userId, startDate, endDate))
                .thenThrow(new RuntimeException("Database error"));

        List<MonthlyExpenseDto> result = monthlyExpenseService.calculateMonthlyExpenses(userId, startDate, endDate);

        assertTrue(result.isEmpty());
    }


}