package expend_tracker.service;

import expend_tracker.repositories.InvoiceFileRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service for handling file management operations.
 */
@Service
public class FileManagerService {
    private static final Logger log = LogManager.getLogger(FileManagerService.class);
    private final InvoiceFileRepository invoiceFileRepository;

    public FileManagerService(InvoiceFileRepository invoiceFileRepository) {
        this.invoiceFileRepository = invoiceFileRepository;
    }

    /**
     * Saves a file to a specified path.
     *
     * @param file The file to be saved.
     * @param path The path where the file will be saved.
     * @return The name of the saved file.
     * @throws IOException If an error occurs during file saving.
     */
    public String saveFile(MultipartFile file, String path) throws IOException {
        try {
            String fileName = createUniqueFileName(file);
            Path fullPath = Paths.get(path);
            Files.write(fullPath, file.getBytes());
            log.info("File saved successfully: {}", fullPath);
            return fileName;
        } catch (IOException e) {
            log.error("Error saving file: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Creates a unique file name for a given file.
     *
     * @param file The file for which a unique name will be created.
     * @return The unique file name.
     * @throws IOException If the file name cannot be created.
     */
    public String createUniqueFileName(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            log.error("No file type found for file: {}", originalFilename);
            throw new IOException("No file type found");
        }

        String fileExtension = originalFilename.substring(lastDotIndex + 1).toLowerCase();
        String fileName = "invoices_" + System.currentTimeMillis() + "-" + UUID.randomUUID() + "." + fileExtension;

        log.info("Unique file name created: {}", fileName);
        return fileName;
    }
}