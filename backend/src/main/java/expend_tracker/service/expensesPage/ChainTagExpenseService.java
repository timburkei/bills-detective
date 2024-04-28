package expend_tracker.service.expensesPage;

import expend_tracker.dto.expensesPage.ChainExpenseDto;
import expend_tracker.dto.expensesPage.TagExpenseDto;
import expend_tracker.model.InvoiceItem;
import expend_tracker.repositories.ChainRepository;
import expend_tracker.repositories.InvoiceItemRepository;
import expend_tracker.repositories.TagRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import java.util.stream.Collectors;

/**
 * Service for calculating chain tag expenses.
 * Provides methods for calculating expenses for each tag in each chain.
 */
@Service
public class ChainTagExpenseService {

    private static final Logger log = LogManager.getLogger(ChainTagExpenseService.class);

    private final InvoiceItemRepository invoiceItemRepository;
    private final ChainRepository chainRepository;
    private final TagRepository tagRepository;

    @Autowired
    public ChainTagExpenseService(InvoiceItemRepository invoiceItemRepository, ChainRepository chainRepository, TagRepository tagRepository) {
        this.invoiceItemRepository = invoiceItemRepository;
        this.chainRepository = chainRepository;
        this.tagRepository = tagRepository;
    }

    /**
     * Calculates the total expense for each tag in each chain for a specified user within a given date range.
     *
     * @param userId    The ID of the user.
     * @param chainIds  List of chain IDs for filtering expenses.
     * @param tagIds    List of tag IDs for filtering expenses.
     * @param startDate Optional start date for filtering expenses.
     * @param endDate   Optional end date for filtering expenses.
     * @return List of {@link ChainExpenseDto} for the user.
     */
    /*public List<ChainExpenseDto> calculateChainTagExpenses(String userId, List<Long> chainIds, List<Long> tagIds, Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
        log.info("calculateChainTagExpenses called with userId: " + userId + ", chainIds: " + chainIds + ", tagIds: " + tagIds + ", startDate: " + startDate + ", endDate: " + endDate);

        List<InvoiceItem> invoiceItems;
        try {
            if (startDate.isPresent() && endDate.isPresent()) {
                invoiceItems = invoiceItemRepository.findByUserIdAndDateBetweenAndStoreChainIdInAndProductTagIdIn(userId, startDate.get(), endDate.get(), chainIds, tagIds);
            } else {
                invoiceItems = invoiceItemRepository.findByInvoiceUserIdAndStoreChainIdInAndProductTagIdIn(userId, chainIds, tagIds);
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching InvoiceItems: " + e.getMessage());
            throw e;
        }

        // Group InvoiceItems by chainId and tagId and calculate the total expense for each tag in each chain
        Map<Long, Map<Long, Double>> chainTagExpenses = invoiceItems.stream()
                .collect(Collectors.groupingBy(item -> item.getStore().getChain().getId(),
                        Collectors.groupingBy(item -> item.getProduct().getTag().getId(),
                                Collectors.summingDouble(InvoiceItem::getPrice))));

        // Create ChainExpenseDto and TagExpenseDto objects
        List<ChainExpenseDto> chainExpenseDtos = new ArrayList<>();
        for (Long chainId : chainIds) {
            ChainExpenseDto chainExpenseDto = new ChainExpenseDto();
            chainExpenseDto.setChainName(chainRepository.findById(chainId).get().getName());

            List<TagExpenseDto> tagExpenseDtos = new ArrayList<>();
            for (Long tagId : tagIds) {
                TagExpenseDto tagExpenseDto = new TagExpenseDto();
                tagExpenseDto.setTagName(tagRepository.findById(tagId).get().getName());
                tagExpenseDto.setExpense(chainTagExpenses.getOrDefault(chainId, new HashMap<>()).getOrDefault(tagId, 0.0));

                tagExpenseDtos.add(tagExpenseDto);
            }

            chainExpenseDto.setTagExpenses(tagExpenseDtos);
            chainExpenseDtos.add(chainExpenseDto);
        }

        log.info("calculateChainTagExpenses completed successfully.");
        return chainExpenseDtos;
    }*/

    public List<ChainExpenseDto> calculateChainTagExpenses(String userId, List<Long> chainIds, List<Long> tagIds, Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
        log.info("calculateChainTagExpenses called with userId: " + userId + ", chainIds: " + chainIds + ", tagIds: " + tagIds + ", startDate: " + startDate + ", endDate: " + endDate);

        List<InvoiceItem> invoiceItems;
        try {
            if (startDate.isPresent() && endDate.isPresent()) {
                invoiceItems = invoiceItemRepository.findByUserIdAndDateBetweenAndStoreChainIdInAndProductTagIdIn(userId, startDate.get(), endDate.get(), chainIds, tagIds);
            } else {
                invoiceItems = invoiceItemRepository.findByInvoiceUserIdAndStoreChainIdInAndProductTagIdIn(userId, chainIds, tagIds);
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching InvoiceItems: " + e.getMessage());
            throw e;
        }

        Map<Long, Map<Long, Double>> chainTagExpenses = invoiceItems.stream()
                .collect(Collectors.groupingBy(item -> item.getStore().getChain().getId(),
                        Collectors.groupingBy(item -> item.getProduct().getTag().getId(),
                                Collectors.summingDouble(InvoiceItem::getPrice))));

        chainTagExpenses.forEach((chainId, tagExpenses) -> tagExpenses.replaceAll((tagId, expense) -> roundToTwoDecimalPlaces(expense)));

        List<ChainExpenseDto> chainExpenseDtos = new ArrayList<>();
        for (Long chainId : chainIds) {
            ChainExpenseDto chainExpenseDto = new ChainExpenseDto();
            chainExpenseDto.setChainName(chainRepository.findById(chainId).get().getName());

            List<TagExpenseDto> tagExpenseDtos = new ArrayList<>();
            for (Long tagId : tagIds) {
                TagExpenseDto tagExpenseDto = new TagExpenseDto();
                tagExpenseDto.setTagName(tagRepository.findById(tagId).get().getName());
                tagExpenseDto.setExpense(chainTagExpenses.getOrDefault(chainId, new HashMap<>()).getOrDefault(tagId, 0.0));

                tagExpenseDtos.add(tagExpenseDto);
            }

            chainExpenseDto.setTagExpenses(tagExpenseDtos);
            chainExpenseDtos.add(chainExpenseDto);
        }

        log.info("calculateChainTagExpenses completed successfully.");
        return chainExpenseDtos;
    }

    private double roundToTwoDecimalPlaces(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}