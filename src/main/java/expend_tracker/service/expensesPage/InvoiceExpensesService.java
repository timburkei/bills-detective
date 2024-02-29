package expend_tracker.service.expensesPage;

import expend_tracker.dto.expensesPage.InvoiceExpensesDto;
import expend_tracker.model.*;
import expend_tracker.repositories.InvoiceRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling operations related to invoice expenses.
 */
@Service
public class InvoiceExpensesService {

    private static final Logger log = LogManager.getLogger(InvoiceExpensesService.class);
    private final InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceExpensesService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Retrieves a list of InvoiceExpensesDto for a specified user and date range.
     *
     * @param userId    The user's ID
     * @param startDate The start date of the invoice period, can be null
     * @param endDate   The end date of the invoice period, can be null
     * @param chainIds  The IDs of the chains to filter by, can be null
     * @param productIds The IDs of the products to filter by, can be null
     * @return List of InvoiceExpensesDto
     */
    public List<InvoiceExpensesDto> getInvoicesByUser(String userId, LocalDate startDate, LocalDate endDate, List<Long> chainIds, List<Long> productIds) {
        try {
            log.info("Retrieving invoices for user: {} from {} to {} with chainIds: {} and productIds: {}", userId, startDate, endDate, chainIds, productIds);
            List<Invoice> invoices = fetchInvoices(userId, startDate, endDate, chainIds, productIds);
            return invoices.stream().map(this::convertToInvoiceDto).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving invoices for user: {}", userId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Fetches invoices for a specific user based on provided criteria.
     *
     * @param userId    The user's ID
     * @param startDate The start date of the invoice period, can be null
     * @param endDate   The end date of the invoice period, can be null
     * @param chainIds  The chain IDs for filtering invoices, can be null or empty
     * @param tagIds    The tag IDs for filtering invoices, can be null or empty
     * @return List of invoices matching the provided criteria
     */
    private List<Invoice> fetchInvoices(String userId, LocalDate startDate, LocalDate endDate, List<Long> chainIds, List<Long> tagIds) {
        try {
            log.info("Fetching invoices for user: {} from {} to {} with chainIds: {} and tagIds: {}", userId, startDate, endDate, chainIds, tagIds);

            if (startDate != null && endDate != null) {
                if (chainIds != null && !chainIds.isEmpty() && tagIds != null && !tagIds.isEmpty()) {
                    return invoiceRepository.findByUserIdAndInvoiceDateBetweenAndStoreChainIdInAndTagIdIn(userId, startDate, endDate, chainIds, tagIds);
                } else if (chainIds != null && !chainIds.isEmpty()) {
                    return invoiceRepository.findByUserIdAndInvoiceDateBetweenAndStoreChainIdIn(userId, startDate, endDate, chainIds);
                } else if (tagIds != null && !tagIds.isEmpty()) {
                    return invoiceRepository.findByUserIdAndInvoiceDateBetweenAndTagIdIn(userId, startDate, endDate, tagIds);
                } else {
                    return invoiceRepository.findByUserIdAndInvoiceDateBetween(userId, startDate, endDate);
                }
            } else {
                if (chainIds != null && !chainIds.isEmpty() && tagIds != null && !tagIds.isEmpty()) {
                    return invoiceRepository.findByUserIdAndStoreChainIdInAndTagIdIn(userId, chainIds, tagIds);
                } else if (chainIds != null && !chainIds.isEmpty()) {
                    return invoiceRepository.findByUserIdAndStoreChainIdIn(userId, chainIds);
                } else if (tagIds != null && !tagIds.isEmpty()) {
                    return invoiceRepository.findByUserIdAndTagIdIn(userId, tagIds);
                } else {
                    return invoiceRepository.findByUserId(userId);
                }
            }
        } catch (Exception e) {
            log.error("Error fetching invoices for user: {}", userId, e);
            throw e;
        }
    }

    /**
     * Converts an Invoice entity to an InvoiceExpensesDto.
     *
     * @param invoice The Invoice entity
     * @return The InvoiceExpensesDto
     */
    private InvoiceExpensesDto convertToInvoiceDto(Invoice invoice) {
        InvoiceExpensesDto dto = new InvoiceExpensesDto();
        dto.setId(invoice.getId());
        dto.setDate(invoice.getInvoiceDate());

        Store store = invoice.getStore();
        if (store != null) {
            Chain chain = store.getChain();
            if (chain != null) {
                dto.setChainName(chain.getName());
            }

            Location location = store.getLocation();
            if (location != null) {
                dto.setStreet(location.getStreet());
                dto.setNumber(location.getNumber());
                dto.setZip(Integer.toString(location.getZip()));
                dto.setCity(location.getCity());
            }
        }

        InvoiceFile invoiceFile = invoice.getInvoiceFile();
        if (invoiceFile != null) {
            dto.setInvoiceFileId(invoiceFile.getId());
        }

        return dto;
    }
}