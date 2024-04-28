package expend_tracker.service.expensesPage;

import expend_tracker.dto.expensesPage.ChainExpenseDto;
import expend_tracker.model.InvoiceItem;
import expend_tracker.model.Store;
import expend_tracker.model.Chain;
import expend_tracker.model.Product;
import expend_tracker.model.Tag;
import expend_tracker.repositories.ChainRepository;
import expend_tracker.repositories.InvoiceItemRepository;
import expend_tracker.repositories.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ChainTagExpenseServiceTest {

    @Mock
    private InvoiceItemRepository invoiceItemRepository;

    @Mock
    private ChainRepository chainRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private ChainTagExpenseService chainTagExpenseService;

    @BeforeEach
    public void setup() {
        // Mocking chain and tag repositories
        when(chainRepository.findById(any())).thenReturn(Optional.of(new Chain()));
        when(tagRepository.findById(any())).thenReturn(Optional.of(new Tag()));

        // Mocking invoice item repository
        InvoiceItem mockInvoiceItem = new InvoiceItem();
        mockInvoiceItem.setPrice(100.0);
        Store store = new Store();
        Chain chain = new Chain();
        chain.setId(1L);
        store.setChain(chain);
        mockInvoiceItem.setStore(store);
        Product product = new Product();
        Tag tag = new Tag();
        tag.setId(1L);
        product.setTag(tag);
        mockInvoiceItem.setProduct(product);
        when(invoiceItemRepository.findByUserIdAndDateBetweenAndStoreChainIdInAndProductTagIdIn(any(), any(), any(), any(), any()))
                .thenReturn(Arrays.asList(mockInvoiceItem));
    }

    @Test
    public void testCalculateChainTagExpenses() {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        List<Long> chainIds = Arrays.asList(1L);
        List<Long> tagIds = Arrays.asList(1L);
        Optional<LocalDate> startDate = Optional.of(LocalDate.of(2024, 1, 1));
        Optional<LocalDate> endDate = Optional.of(LocalDate.of(2024, 1, 31));

        List<ChainExpenseDto> result = chainTagExpenseService.calculateChainTagExpenses(userId, chainIds, tagIds, startDate, endDate);

        assertEquals(1, result.size());
        assertEquals(100.0, result.get(0).getTagExpenses().get(0).getExpense());
    }
}