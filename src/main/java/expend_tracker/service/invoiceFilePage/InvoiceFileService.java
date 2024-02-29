package expend_tracker.service.invoiceFilePage;

import expend_tracker.dto.invoiceFilePage.InvoiceFileDto;
import expend_tracker.model.Invoice;
import expend_tracker.model.InvoiceFile;
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
 * Service for managing invoice files.
 */
@Service
public class InvoiceFileService {

    private static final Logger log = LogManager.getLogger(InvoiceFileService.class);
    private final InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceFileService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Retrieves invoice files for a given user and date range.
     *
     * @param userId    The user's ID
     * @param startDate Start date for the invoice period
     * @param endDate   End date for the invoice period
     * @return List of InvoiceFileDto
     */
    public List<InvoiceFileDto> getInvoiceFiles(String userId, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Retrieving invoice files for user: {} between {} and {}", userId, startDate, endDate);
            List<Invoice> invoices = fetchInvoices(userId, startDate, endDate);
            return invoices.stream()
                    .map(invoice -> mapToDto(invoice.getInvoiceFile()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving invoice files for user: {}", userId, e);
            return Collections.emptyList();
        }
    }

    private List<Invoice> fetchInvoices(String userId, LocalDate startDate, LocalDate endDate) {
        return startDate != null && endDate != null
                ? invoiceRepository.findByUserIdAndInvoiceDateBetween(userId, startDate, endDate)
                : invoiceRepository.findByUserId(userId);
    }

    /**
     * Converts an InvoiceFile entity to an InvoiceFileDto.
     *
     * @param invoiceFile The InvoiceFile entity
     * @return The InvoiceFileDto
     */
    private InvoiceFileDto mapToDto(InvoiceFile invoiceFile) {
        InvoiceFileDto dto = new InvoiceFileDto();
        if (invoiceFile != null) {
            dto.setId(invoiceFile.getId());
            dto.setUploadSuccessful(invoiceFile.isUploadSuccessful());
            dto.setUploadDate(invoiceFile.getUploadDate());
            dto.setUploadTime(invoiceFile.getUploadTime());
            dto.setInvoiceUrl(invoiceFile.getInvoiceUrl());
        }
        return dto;
    }
}