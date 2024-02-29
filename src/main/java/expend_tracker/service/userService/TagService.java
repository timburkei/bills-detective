package expend_tracker.service.userService;

import expend_tracker.dto.userService.TagDto;
import expend_tracker.model.InvoiceItem;
import expend_tracker.model.Product;
import expend_tracker.model.Tag;
import expend_tracker.repositories.InvoiceItemRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagService {

    private static final Logger log = LogManager.getLogger(TagService.class);

    private final InvoiceItemRepository invoiceItemRepository;

    @Autowired
    public TagService(InvoiceItemRepository invoiceItemRepository) {
        this.invoiceItemRepository = invoiceItemRepository;
    }

    /**
     * Retrieves all tags linked to the invoices of the specified user within a given date range.
     *
     * @param userId    The unique identifier of the user whose tags are to be retrieved.
     * @param startDate The start date of the range within which to retrieve the tags. This parameter is optional.
     * @param endDate   The end date of the range within which to retrieve the tags. This parameter is optional.
     * @return A list of {@link TagDto} representing the tags linked to the user's invoices.
     */
    public List<TagDto> getTagsForUser(String userId, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Retrieving tags for user: {} with date range: {} - {}", userId, startDate, endDate);
            List<InvoiceItem> invoiceItems;
            if (startDate != null && endDate != null) {
                invoiceItems = invoiceItemRepository.findByInvoiceUserIdAndInvoiceInvoiceDateBetween(userId, startDate, endDate);
            } else {
                invoiceItems = invoiceItemRepository.findByInvoiceUserId(userId);
            }
            List<TagDto> tags = new ArrayList<>();
            for (InvoiceItem item : invoiceItems) {
                Product product = item.getProduct();
                Tag tag = product.getTag();
                TagDto tagDto = new TagDto();
                tagDto.setId(tag.getId());
                tagDto.setName(tag.getName());
                tags.add(tagDto);
            }
            log.info("Retrieved {} tags for user: {}", tags.size(), userId);
            return tags.stream().distinct().collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to retrieve tags for user: {}", userId, e);
            throw new RuntimeException("Failed to retrieve tags for user: " + userId, e);
        }
    }
}