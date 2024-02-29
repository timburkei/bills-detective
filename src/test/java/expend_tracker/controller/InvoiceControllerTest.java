package expend_tracker.controller;

import expend_tracker.dto.invoice.InvoiceData;
import expend_tracker.model.InvoiceItem;
import expend_tracker.service.FileManagerService;
import expend_tracker.service.invoicePage.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
public class InvoiceControllerTest {

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private FileManagerService fileManagerService;

    @InjectMocks
    private InvoiceController invoiceController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(invoiceController).build();
    }

    @Test
    public void uploadImageTest() throws Exception {
        String userId = "auth0|65630d5317b4bdb501144ab5";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "filename.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "hello world".getBytes()
        );

        when(invoiceService.uploadInvoiceFile(any(MultipartFile.class), eq(userId)))
                .thenReturn("Invoice uploaded successfully");

        mockMvc.perform(multipart("/api/invoice/" + userId + "/upload")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Invoice uploaded successfully"));

        verify(invoiceService, times(1)).uploadInvoiceFile(any(MultipartFile.class), eq(userId));
    }

}
