package expend_tracker.service.expensesPage;
import expend_tracker.model.*;
import expend_tracker.service.expensesPage.ProductExpenseService;
import expend_tracker.repositories.InvoiceItemRepository;
import expend_tracker.dto.expensesPage.ProductExpensesDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductExpenseServiceTest {

    private InvoiceItemRepository invoiceItemRepository;
    private ProductExpenseService productExpenseService;

    @BeforeEach
    void setUp() {
        invoiceItemRepository = mock(InvoiceItemRepository.class);
        productExpenseService = new ProductExpenseService(invoiceItemRepository);
    }

    @Test
    void testCalculateExpenses() {
        Tag mockTag = new Tag("Mock");
        Product mockProduct = new Product("Mockprodukt", mockTag);
        Store mockStore = new Store();
        Invoice mockInvoice = new Invoice();

        InvoiceItem mockInvoiceItem1 = new InvoiceItem("Item 1", 100.0, LocalDate.of(2023, 1, 15), mockStore, mockInvoice, mockProduct);
        InvoiceItem mockInvoiceItem2 = new InvoiceItem("Item 2", 200.0, LocalDate.of(2023, 1, 20), mockStore, mockInvoice, mockProduct);

        List<InvoiceItem> mockInvoiceItems = Arrays.asList(mockInvoiceItem1, mockInvoiceItem2);

        Long productId = 1L;
        String userId = "auth0|65630d5317b4bdb501144ab5";
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        when(invoiceItemRepository.findByProductIdAndUserIdAndDateBetween(productId, userId, startDate, endDate)).thenReturn(mockInvoiceItems);

        ProductExpensesDto result = productExpenseService.calculateExpenses(productId, userId, startDate, endDate);

        assertNotNull(result);
        assertEquals(2, result.getInvoiceItems().size());

        assertEquals("Mockprodukt", result.getInvoiceItems().get(0).getName());
        assertEquals(100.0, result.getInvoiceItems().get(0).getPrice());
        assertEquals("Mockprodukt", result.getInvoiceItems().get(1).getName());
        assertEquals(200.0, result.getInvoiceItems().get(1).getPrice());

        Map<String, Double> monthlyExpenditures = result.getInvoiceItemExpenditure();
        assertNotNull(monthlyExpenditures);
        assertTrue(monthlyExpenditures.containsKey("01.2023"));
        assertEquals(300.0, monthlyExpenditures.get("01.2023"));

        verify(invoiceItemRepository, times(1)).findByProductIdAndUserIdAndDateBetween(productId, userId, startDate, endDate);
    }
}
