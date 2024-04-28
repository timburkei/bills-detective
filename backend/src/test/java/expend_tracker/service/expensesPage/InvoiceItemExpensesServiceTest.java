package expend_tracker.service.expensesPage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import expend_tracker.repositories.InvoiceItemRepository;
import expend_tracker.service.expensesPage.InvoiceItemExpensesService;
import expend_tracker.dto.expensesPage.InvoiceItemExpensesDto;
import expend_tracker.model.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class InvoiceItemExpensesServiceTest {

    @Mock
    private InvoiceItemRepository invoiceItemRepository;

    @InjectMocks
    private InvoiceItemExpensesService invoiceItemExpensesService;

    @Test
    void getInvoiceItemsByUserAndInvoice_Success() {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        Long invoiceId = 1L;

        InvoiceItem mockInvoiceItem = createMockInvoiceItem();
        when(invoiceItemRepository.findByInvoiceIdAndInvoiceUserId(invoiceId, userId))
                .thenReturn(Arrays.asList(mockInvoiceItem));

        List<InvoiceItemExpensesDto> result = invoiceItemExpensesService.getInvoiceItemsByUserAndInvoice(userId, invoiceId);

        assertEquals(1, result.size());
        InvoiceItemExpensesDto dto = result.get(0);
        assertEquals(mockInvoiceItem.getId(), dto.getId());
        assertEquals(mockInvoiceItem.getName(), dto.getName());
    }

    private InvoiceItem createMockInvoiceItem() {
        Product mockProduct = new Product("Apfel", new Tag("Obst"));
        InvoiceItem mockInvoiceItem = new InvoiceItem("Apfel", 100.0, LocalDate.now(), null, null, mockProduct);
        mockInvoiceItem.setId(1L);
        return mockInvoiceItem;
    }

    @Test
    void getInvoiceItemsByUserAndInvoice_Error() {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        Long invoiceId = 1L;

        when(invoiceItemRepository.findByInvoiceIdAndInvoiceUserId(invoiceId, userId))
                .thenThrow(new RuntimeException("Database error"));

        List<InvoiceItemExpensesDto> result = invoiceItemExpensesService.getInvoiceItemsByUserAndInvoice(userId, invoiceId);

        assertTrue(result.isEmpty());
    }
}