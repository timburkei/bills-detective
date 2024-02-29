package expend_tracker.controller;

import expend_tracker.dto.expensesPage.*;
import expend_tracker.service.expensesPage.*;
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

import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
public class ExpenditurePageControllerTest {

    @Mock
    private InvoiceExpensesService invoiceExpensesService;
    @Mock
    private InvoiceItemExpensesService invoiceItemExpensesService;
    @Mock
    private ProductExpenseService productExpenseService;

    @Mock ChainTagExpenseService chainTagExpenseService;


    @InjectMocks
    private ExpenditurePageController controller;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testGetInvoicesByUser() throws Exception {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        given(invoiceExpensesService.getInvoicesByUser(userId, startDate, endDate, null, null))
                .willReturn(Collections.singletonList(new InvoiceExpensesDto(1L, LocalDate.of(2024, 1, 1), "Edeka", "Musterstraße 1", "123", "12345", "Test City", 1L)));

        mockMvc.perform(get("/api/expenditure-page/{userId}/invoices/", userId)
                        .param("startDate", "01.01.2024")
                        .param("endDate", "31.01.2024"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1))) // Erwartete Größe der Liste auf 1 setzen
                .andExpect(jsonPath("$[0].chainName").value("Edeka"))
                .andExpect(jsonPath("$[0].street").value("Musterstraße 1"))
                .andExpect(jsonPath("$[0].zip").value("12345"))
                .andExpect(jsonPath("$[0].city").value("Test City"));
    }

    @Test
    public void testGetInvoiceItemsByUserAndInvoice() throws Exception {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        Long invoiceId = 1L;
        List<InvoiceItemExpensesDto> mockItems = Arrays.asList(
                new InvoiceItemExpensesDto(11L, "Apfelsaft Bittenfelder", "Apfelsaft", 10.00,  290L, 18L, "Fruchtsäfte")
        );

        given(invoiceItemExpensesService.getInvoiceItemsByUserAndInvoice(any(), any())).willReturn(mockItems);

        mockMvc.perform(get("/api/expenditure-page/{userId}/invoices/{invoiceId}/items/", userId, invoiceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].id").value(11L))
                .andExpect(jsonPath("$[0].name").value("Apfelsaft Bittenfelder"))
                .andExpect(jsonPath("$[0].productName").value("Apfelsaft"))
                .andExpect(jsonPath("$[0].productId").value(290L))
                .andExpect(jsonPath("$[0].tagId").value(18L))
                .andExpect(jsonPath("$[0].tagName").value("Fruchtsäfte"));
    }

    @Test
    public void testGetProductExpenses() throws Exception {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        Long productId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        ProductExpensesDto mockProductExpenses = new ProductExpensesDto();
        List<InvoiceItemDto> invoiceItems = new ArrayList<>();
        InvoiceItemDto invoiceItem = new InvoiceItemDto();
        invoiceItem.setId(2L);
        invoiceItem.setName("Möhre");
        invoiceItem.setPrice(20.0);
        invoiceItem.setDate(LocalDate.of(2024, 1, 1));
        invoiceItem.setChainName("Edeka");
        invoiceItem.setStreet("Musterstraße 1");
        invoiceItem.setNumber("123");
        invoiceItem.setZip(12345);
        invoiceItem.setCity("Musterstadt");
        invoiceItems.add(invoiceItem);
        mockProductExpenses.setInvoiceItems(invoiceItems);

        Map<String, Double> invoiceItemExpenditure = new HashMap<>();
        invoiceItemExpenditure.put("01.2024", 60.0);
        mockProductExpenses.setInvoiceItemExpenditure(invoiceItemExpenditure);

        given(productExpenseService.calculateExpenses(productId, userId, startDate, endDate))
                .willReturn(mockProductExpenses);

        mockMvc.perform(get("/api/expenditure-page/{userId}/products/{productId}/expenses", userId, productId)
                        .param("startDate", "01.01.2024")
                        .param("endDate", "31.01.2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.invoiceItems", hasSize(1)))
                .andExpect(jsonPath("$.invoiceItems[0].id").value(2))
                .andExpect(jsonPath("$.invoiceItems[0].name").value("Möhre"))
                .andExpect(jsonPath("$.invoiceItems[0].price").value(20.0))
                .andExpect(jsonPath("$.invoiceItems[0].date", is(Arrays.asList(2024, 1, 1))))
                .andExpect(jsonPath("$.invoiceItems[0].chainName").value("Edeka"))
                .andExpect(jsonPath("$.invoiceItems[0].street").value("Musterstraße 1"))
                .andExpect(jsonPath("$.invoiceItems[0].number").value("123"))
                .andExpect(jsonPath("$.invoiceItems[0].zip").value(12345))
                .andExpect(jsonPath("$.invoiceItems[0].city").value("Musterstadt"))
                .andExpect(jsonPath("$.invoiceItemExpenditure").isMap());
    }

    @Test
    public void testGetChainTagExpensesByUser() throws Exception {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        List<Long> chainIds = Arrays.asList(1L, 2L);
        List<Long> tagIds = Arrays.asList(3L, 4L);

        ChainExpenseDto mockChainExpense = new ChainExpenseDto();
        mockChainExpense.setChainName("Edeka");

        given(chainTagExpenseService.calculateChainTagExpenses(userId, chainIds, tagIds, Optional.of(startDate), Optional.of(endDate)))
                .willReturn(Collections.singletonList(mockChainExpense));

        mockMvc.perform(get("/api/expenditure-page/{userId}/chain-tag-expenses", userId)
                        .param("chainIds", "1,2")
                        .param("tagIds", "3,4")
                        .param("startDate", "01.01.2024")
                        .param("endDate", "31.01.2024"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].chainName").value("Edeka"));
    }

   

}