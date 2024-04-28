package expend_tracker.service.expensesPage;
import expend_tracker.dto.expensesPage.InvoiceItemExpensesDto;
import expend_tracker.model.InvoiceItem;
import expend_tracker.model.Product;
import expend_tracker.model.Tag;
import expend_tracker.repositories.InvoiceItemRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling invoice item expenses.
 */
@Service
public class InvoiceItemExpensesService {

    private static final Logger log = LogManager.getLogger(InvoiceItemExpensesService.class);
    private final InvoiceItemRepository invoiceItemRepository;

    @Autowired
    public InvoiceItemExpensesService(InvoiceItemRepository invoiceItemRepository) {
        this.invoiceItemRepository = invoiceItemRepository;
    }

    /**
     * Retrieves a list of InvoiceItemExpensesDto for a given user and invoice.
     *
     * @param userId    The user's ID
     * @param invoiceId The invoice's ID
     * @return List of InvoiceItemExpensesDto
     */
    @Transactional(readOnly = true)
    public List<InvoiceItemExpensesDto> getInvoiceItemsByUserAndInvoice(String userId, Long invoiceId) {
        try {
            log.info("Retrieving invoice items for user: {} and invoice: {}", userId, invoiceId);
            List<InvoiceItem> invoiceItems = invoiceItemRepository.findByInvoiceIdAndInvoiceUserId(invoiceId, userId);
            return invoiceItems.stream().map(this::convertToDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving invoice items for user: {} and invoice: {}", userId, invoiceId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Converts an InvoiceItem entity to an InvoiceItemExpensesDto.
     *
     * @param invoiceItem The InvoiceItem entity
     * @return The InvoiceItemExpensesDto
     */
    private InvoiceItemExpensesDto convertToDto(InvoiceItem invoiceItem) {
        InvoiceItemExpensesDto dto = new InvoiceItemExpensesDto();
        dto.setId(invoiceItem.getId());
        dto.setName(invoiceItem.getName());
        Product product = invoiceItem.getProduct();
        if (product != null) {
            dto.setProductName(product.getName());
            double price = invoiceItem.getPrice();
            price = Math.round(price * 100.0) / 100.0;
            dto.setPrice(price);
            dto.setProductId(product.getId());
            Tag tag = product.getTag();
            if (tag != null) {
                dto.setTagId(tag.getId());
                dto.setTagName(tag.getName());
            }
        }
        return dto;
    }
}