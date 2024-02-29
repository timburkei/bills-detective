package expend_tracker.controller;

import expend_tracker.dto.invoiceFilePage.InvoiceFileDto;
import expend_tracker.service.invoiceFilePage.InvoiceFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.hamcrest.Matchers.is;




import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;


import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
public class InvoiceFileControllerTest {

    @Mock
    private InvoiceFileService invoiceFileService;

    @InjectMocks
    private InvoiceFileController invoiceFileController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(invoiceFileController).build();
    }

    @Test
    public void getInvoiceFilesTest() throws Exception {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        List<InvoiceFileDto> invoiceFiles = Arrays.asList(
                new InvoiceFileDto(1L, true, LocalDate.of(2024, 1, 6), Time.valueOf(LocalTime.of(16, 29, 4)), "/invoices_1704554944610-50bf217f-1dd9-4643-8974-4e61cbaf1b6b.png")
        );

        given(invoiceFileService.getInvoiceFiles(userId, startDate, endDate)).willReturn(invoiceFiles);

        mockMvc.perform(get("/api/invoicesFiles/getUploadInformation/{userId}/", userId)
                        .param("startDate", "01.01.2024")
                        .param("endDate", "31.01.2024"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(invoiceFiles.size())))
                .andExpect(jsonPath("$[0].id").value(invoiceFiles.get(0).getId()))
                .andExpect(jsonPath("$[0].uploadSuccessful").value(true))
                .andExpect(jsonPath("$[0].uploadDate[0]", is(2024)))
                .andExpect(jsonPath("$[0].uploadDate[1]", is(1)))
                .andExpect(jsonPath("$[0].uploadDate[2]", is(6)))
                .andExpect(jsonPath("$[0].uploadTime").value("16:29:04"))
                .andExpect(jsonPath("$[0].invoiceUrl").value("/invoices_1704554944610-50bf217f-1dd9-4643-8974-4e61cbaf1b6b.png"));
    }

}
