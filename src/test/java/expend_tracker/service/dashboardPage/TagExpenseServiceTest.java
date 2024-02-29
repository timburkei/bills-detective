package expend_tracker.service.dashboardPage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import expend_tracker.repositories.InvoiceItemRepository;
import expend_tracker.dto.dashboardPage.TagExpenseDto;
import expend_tracker.service.dashboardPage.TagExpenseService;
import expend_tracker.model.InvoiceItem;
import expend_tracker.model.Product;
import expend_tracker.model.Tag;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class TagExpenseServiceTest {

    @Mock
    private InvoiceItemRepository invoiceItemRepository;

    @InjectMocks
    private TagExpenseService tagExpenseService;

    @Test
    void calculateTagExpenses_Success() {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        Tag tag1 = new Tag("Obst");
        Tag tag2 = new Tag("Gemüse");
        Product product1 = new Product("Apfel", tag1);
        Product product2 = new Product("Gurke", tag2);

        InvoiceItem item1 = new InvoiceItem("Bio Apfel", 100.0, LocalDate.of(2023, 1, 15), null, null, product1);
        InvoiceItem item2 = new InvoiceItem("Gurke-Bio", 200.0, LocalDate.of(2023, 1, 20), null, null, product2);

        when(invoiceItemRepository.findByInvoiceUserIdAndInvoiceInvoiceDateBetween(userId, startDate, endDate))
                .thenReturn(Arrays.asList(item1, item2));

        List<TagExpenseDto> result = tagExpenseService.calculateTagExpenses(userId, startDate, endDate);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.getCategoryName().equals("Obst") && dto.getTotalExpense() == 100.0));
        assertTrue(result.stream().anyMatch(dto -> dto.getCategoryName().equals("Gemüse") && dto.getTotalExpense() == 200.0));
    }

    @Test
    void calculateTagExpenses_Error() {
        String userId = "testUserId";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        when(invoiceItemRepository.findByInvoiceUserIdAndInvoiceInvoiceDateBetween(userId, startDate, endDate))
                .thenThrow(new RuntimeException("Database error"));

        List<TagExpenseDto> result = tagExpenseService.calculateTagExpenses(userId, startDate, endDate);

        assertTrue(result.isEmpty());
    }
}