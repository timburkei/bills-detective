package expend_tracker.service.expensesPage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import expend_tracker.repositories.InvoiceRepository;
import expend_tracker.dto.expensesPage.InvoiceExpensesDto;
import expend_tracker.model.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class InvoiceExpensesServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoiceExpensesService invoiceExpensesService;

    @Test
    void getInvoicesByUser_Success() {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        Invoice invoice = createMockInvoice();
        when(invoiceRepository.findByUserIdAndInvoiceDateBetween(userId, startDate, endDate))
                .thenReturn(Arrays.asList(invoice));

        List<InvoiceExpensesDto> result = invoiceExpensesService.getInvoicesByUser(userId, startDate, endDate, null, null);

        assertEquals(1, result.size());
        InvoiceExpensesDto dto = result.get(0);
        assertEquals(invoice.getId(), dto.getId());
        assertEquals(invoice.getInvoiceDate(), dto.getDate());
    }

    private Invoice createMockInvoice() {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setInvoiceDate(LocalDate.of(2023, 1, 15));


        Chain mockChain = new Chain("Chain Name");
        Location mockLocation = new Location();
        Store mockStore = new Store(mockLocation, mockChain);

        invoice.setStore(mockStore);

        return invoice;
    }

    @Test
    void getInvoicesByUser_Error() {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        when(invoiceRepository.findByUserIdAndInvoiceDateBetween(userId, startDate, endDate))
                .thenThrow(new RuntimeException("Database error"));

        List<InvoiceExpensesDto> result = invoiceExpensesService.getInvoicesByUser(userId, startDate, endDate, null, null);

        assertTrue(result.isEmpty());
    }

}