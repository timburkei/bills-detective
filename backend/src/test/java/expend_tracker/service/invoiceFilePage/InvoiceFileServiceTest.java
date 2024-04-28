package expend_tracker.service.invoiceFilePage;

import expend_tracker.dto.invoiceFilePage.InvoiceFileDto;
import expend_tracker.model.Invoice;
import expend_tracker.model.InvoiceFile;
import expend_tracker.repositories.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class InvoiceFileServiceTest {

    @InjectMocks
    private InvoiceFileService invoiceFileService;

    @Mock
    private InvoiceRepository invoiceRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetInvoiceFiles() {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now();

        InvoiceFile invoiceFile = new InvoiceFile();
        invoiceFile.setId(1L);
        invoiceFile.setUploadSuccessful(true);
        invoiceFile.setUploadDate(LocalDate.now());
        invoiceFile.setUploadTime(Time.valueOf("12:00:00"));
        invoiceFile.setInvoiceUrl("https://localhost:8080/invoice/invoice.png");

        Invoice invoice = new Invoice();
        invoice.setInvoiceFile(invoiceFile);

        when(invoiceRepository.findByUserIdAndInvoiceDateBetween(userId, startDate, endDate))
                .thenReturn(Arrays.asList(invoice));

        List<InvoiceFileDto> result = invoiceFileService.getInvoiceFiles(userId, startDate, endDate);

        assertEquals(1, result.size());
        InvoiceFileDto dto = result.get(0);
        assertEquals(invoiceFile.getId(), dto.getId());
        assertEquals(invoiceFile.isUploadSuccessful(), dto.isUploadSuccessful());
        assertEquals(invoiceFile.getUploadDate(), dto.getUploadDate());
        assertEquals(invoiceFile.getUploadTime(), dto.getUploadTime());
        assertEquals(invoiceFile.getInvoiceUrl(), dto.getInvoiceUrl());
    }
}