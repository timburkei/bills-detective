package expend_tracker.service.invoicePage;

import expend_tracker.dto.invoice.InvoiceData;
import expend_tracker.dto.invoice.InvoiceItemData;
import expend_tracker.model.*;
import expend_tracker.repositories.*;
import expend_tracker.service.FileManagerService;
import expend_tracker.service.matchService.ChainMatchService;
import expend_tracker.service.matchService.ProductMatchService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import java.io.File;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class InvoiceService {
    private static final Logger log = LogManager.getLogger(InvoiceService.class);

    @Autowired
    private InvoiceFileRepository invoiceFileRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ChainRepository chainRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private InvoiceItemRepository invoiceItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ProductMatchService productMatchService;
    @Autowired
    private ChainMatchService chainMatchService;
    @Autowired
    private FileManagerService fileManagerService;

    @Value("${spring.servlet.multipart.location}")
    private String invoiceDirectory;

    @Value("${allowed.file.types}")
    private String allowedFileTypes;

    @Value("${ai.api.key}")
    private String apiKey;

    @Transactional
    public void processInvoiceData(InvoiceData invoiceData) {
        try {
            invoiceData = validateAndSetDefaultValues(invoiceData);

            log.info("Processing invoice data for file ID: {}", invoiceData.getInvoiceFileId());
            InvoiceFile invoiceFile = updateInvoiceFile(invoiceData.getInvoiceFileId());
            Location location = processLocation(invoiceData);
            Chain chain = processChain(invoiceData);
            Store store = processStore(location, chain);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate parsedInvoiceDate = LocalDate.parse(invoiceData.getInvoiceDate(), formatter);

            Invoice invoice = new Invoice();
            invoice.setInvoiceDate(parsedInvoiceDate);
            invoice.setInvoiceTime(Time.valueOf(invoiceData.getInvoiceTime()));
            invoice.setWeekday(Invoice.DayOfWeek.fromJavaTimeDayOfWeek(parsedInvoiceDate.getDayOfWeek()));
            invoice.setTotalAmount(Math.max(invoiceData.getTotalAmount(), 0));
            invoice.setTaxes7Amount(Math.max(invoiceData.getTaxes7Amount(), 0));
            invoice.setTaxes19Amount(Math.max(invoiceData.getTaxes19Amount(), 0));
            invoice.setDiscountAmount(Math.max(invoiceData.getDiscountsAmount(), 0));
            invoice.setInvoiceFile(invoiceFile);
            invoice.setStore(store);
            invoice.setUserId(invoiceData.getUserId());
            invoice = invoiceRepository.save(invoice);

            processInvoiceItems(invoiceData, invoice, store);
        } catch (Exception e) {
            log.error("Error processing invoice data for file ID: {}", invoiceData.getInvoiceFileId(), e);
            throw e;
        }
    }


    /**
     * Validates the invoice data and sets default values for missing fields.
     *
     * @param invoiceData
     * @return The validated invoice data.
     */
    public InvoiceData validateAndSetDefaultValues(InvoiceData invoiceData) {

        try {
            invoiceData.setStreetStore(invoiceData.getStreetStore());
        } catch (Exception e) {
            invoiceData.setStreetStore("Amselweg");
            log.warn("Set Default value for street");
        }

        try {
            invoiceData.setNumberStore(invoiceData.getNumberStore());
        } catch (Exception e) {
            invoiceData.setNumberStore("16");
            log.warn("Set Default value for house number");
        }

        try {
            invoiceData.setZipStore(invoiceData.getZipStore());
        } catch (Exception e) {
            invoiceData.setZipStore(71364);
            log.warn("Set Default value for zip code");
        }

        try {
            invoiceData.setCityStore(invoiceData.getCityStore());
        } catch (Exception e) {
            invoiceData.setCityStore("Berlin");
            log.warn("Set Default value for city");
        }

        try {
            invoiceData.setNameStore(invoiceData.getNameStore());
        } catch (Exception e) {
            invoiceData.setNameStore("FrischeWelt");
            log.warn("Set Default value for store name");
        }

        try {
            invoiceData.setUserId(invoiceData.getUserId());
        } catch (Exception e) {
            invoiceData.setUserId("auth0|65630d5317b4bdb501144ab5");
            log.warn("Set Default value for user id");
        }

        // Validate and parse date and time
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        try {
            LocalDate.parse(invoiceData.getInvoiceDate(), dateFormatter);
        } catch (DateTimeParseException e) {
            invoiceData.setInvoiceDate("31.12.2023");
            log.warn("Set Default value for invoice date");
        }

        if (invoiceData.getInvoiceTime() == null) {
            invoiceData.setInvoiceTime(LocalTime.of(0, 0, 0));
            log.warn("Set Default value for invoice time");
        }

        try {
            invoiceData.setTotalAmount(invoiceData.getTotalAmount());
        } catch (NumberFormatException e) {
            invoiceData.setTotalAmount(0.0);
            log.warn("Set Default value for total amount");
        }

        try {
            invoiceData.setTaxes7Amount(invoiceData.getTaxes7Amount());
        } catch (NumberFormatException e) {
            invoiceData.setTaxes7Amount(0.0);
            log.warn("Set Default value for 7% tax amount");
        }

        try {
            invoiceData.setTaxes19Amount(invoiceData.getTaxes19Amount());
        } catch (NumberFormatException e) {
            invoiceData.setTaxes19Amount(0.0);
            log.warn("Set Default value for 19% tax amount");
        }

        try {
            invoiceData.setDiscountsAmount(invoiceData.getDiscountsAmount());
        } catch (NumberFormatException e) {
            invoiceData.setDiscountsAmount(0.0);
            log.warn("Set Default value for discounts amount");
        }

        // Validate invoice items
        try {
            if (invoiceData.getInvoiceItems() == null || invoiceData.getInvoiceItems().isEmpty()) {
                invoiceData.setInvoiceItems(new ArrayList<>());
                InvoiceItemData defaultItem = new InvoiceItemData();
                defaultItem.setName("Default Product");
                defaultItem.setPrice(0.0);
                invoiceData.getInvoiceItems().add(defaultItem);
                log.warn("Set Default value for invoice items");
            } else {
                for (InvoiceItemData item : invoiceData.getInvoiceItems()) {
                    try {
                        if (item.getName() == null || item.getName().trim().isEmpty()) {
                            item.setName("Der Gerät");  // Setzen Sie den Standardnamen, wenn der aktuelle Name leer oder null ist.
                            log.warn("Set default value 'Der Gerät' for invoice item name");
                        } else {
                            item.setName(item.getName());  // Beibehalten des vorhandenen Namens, wenn er nicht leer oder null ist.
                        }
                    } catch (Exception e) {
                        item.setName("Default Product");
                        log.warn("Set Default value for invoice item name");
                    }
                    try {
                        item.setPrice(item.getPrice());
                    } catch (Exception e) {
                        item.setPrice(1.00);
                        log.warn("Set Default value for invoice item price");
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error setting invoice item values", e);
        }

        return invoiceData;
    }

    /***
     * Updates the invoice file.
     * @param invoiceFileId The ID of the invoice file to be updated.
     * @return The updated invoice file.
     */
    private InvoiceFile updateInvoiceFile(Long invoiceFileId) {
        InvoiceFile invoiceFile = invoiceFileRepository.findById(invoiceFileId)
                .orElseThrow(() -> new EntityNotFoundException("InvoiceFile not found"));
        invoiceFile.setUploadSuccessful(true);
        return invoiceFileRepository.save(invoiceFile);
    }

    private Location processLocation(InvoiceData invoiceData) {
        return locationRepository.findByStreetAndNumberAndZipAndCity(
                        invoiceData.getStreetStore(), invoiceData.getNumberStore(),
                        invoiceData.getZipStore(), invoiceData.getCityStore())
                .orElseGet(() -> {
                    Location newLocation = new Location();
                    newLocation.setStreet(invoiceData.getStreetStore());
                    newLocation.setNumber(invoiceData.getNumberStore());
                    newLocation.setZip(invoiceData.getZipStore());
                    newLocation.setCity(invoiceData.getCityStore());
                    return locationRepository.save(newLocation);
                });
    }

    /**
     * Processes the chain.
     *
     * @param invoiceData The invoice data.
     * @return The chain.
     */
    private Chain processChain(InvoiceData invoiceData) {
        Long chainId = chainMatchService.matchChain(invoiceData.getNameStore());
        return chainRepository.findById(chainId)
                .orElseGet(() -> {
                    Chain newChain = new Chain();
                    newChain.setName(invoiceData.getNameStore());
                    return chainRepository.save(newChain);
                });
    }

    /**
     * Processes the store.
     *
     * @param location The location.
     * @param chain    The chain.
     * @return The store.
     */
    private Store processStore(Location location, Chain chain) {
        return storeRepository.findByLocationAndChain(location, chain)
                .orElseGet(() -> {
                    Store newStore = new Store();
                    newStore.setLocation(location);
                    newStore.setChain(chain);
                    return storeRepository.save(newStore);
                });
    }

    /**
     * Processes the invoice items.
     *
     * @param invoiceData The invoice data.
     * @param invoice     The invoice.
     * @param store       The store.
     */
    private void processInvoiceItems(InvoiceData invoiceData, Invoice invoice, Store store) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate parsedInvoiceDate = LocalDate.parse(invoiceData.getInvoiceDate(), formatter);

        for (InvoiceItemData itemData : invoiceData.getInvoiceItems()) {
            InvoiceItem item = new InvoiceItem();
            item.setName(itemData.getName());
            item.setPrice(itemData.getPrice());
            item.setDate(parsedInvoiceDate); //
            item.setInvoice(invoice);
            item.setStore(store);

            Long productId = productMatchService.findMostSimilarProductId(itemData.getName());
            Product product;

            if (productId != null) {
                product = productRepository.findById(productId)
                        .orElseGet(() -> createNewProduct(itemData.getName()));
            } else {
                product = createNewProduct(itemData.getName());
            }

            item.setProduct(product);
            invoiceItemRepository.save(item);
        }
    }

    /**
     * Creates a new product with the given name.
     *
     * @param productName The name of the product.
     * @return The created product.
     */
    private Product createNewProduct(String productName) {
        Product newProduct = new Product();
        newProduct.setName(productName);
        newProduct.setTag(tagRepository.findTagById(27L));
        return productRepository.save(newProduct);
    }

    /**
     * Uploads an invoice file.
     *
     * @param file   The invoice file to be uploaded.
     * @param userId The ID of the user uploading the file.
     * @return ResponseEntity with the upload status.
     */
    public String uploadInvoiceFile(MultipartFile file, String userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Error: Invoice is empty");
        }

        if (!isValidFileType(file)) {
            throw new IllegalArgumentException("Error: Invalid file type. Only PNG, JPG and JPEG are allowed.");
        }

        String fileName = fileManagerService.createUniqueFileName(file);

        InvoiceFile invoiceFile = new InvoiceFile();
        invoiceFile.setInvoiceUrl(fileName);
        invoiceFile.setUploadSuccessful(false);
        invoiceFile.setUploadTime(java.sql.Time.valueOf(LocalTime.now()));
        invoiceFile.setUploadDate(LocalDate.now());
        invoiceFileRepository.save(invoiceFile);


        String newInvoiceUrl = invoiceFile.getId() + "_" + fileName;
        invoiceFile.setInvoiceUrl(newInvoiceUrl);

        invoiceFileRepository.save(invoiceFile);

        String path = invoiceDirectory + "/" + newInvoiceUrl;


        fileManagerService.saveFile(file, path);
        System.out.println("Path:" + path);

        File savedFile = new File(path);
        uploadFileToEndpoint(savedFile, apiKey, userId, invoiceFile.getId());

        return "Invoice uploaded successfully";
    }


    public void uploadFileToEndpoint(File file, String apiKey, String userId, Long invoiceFileId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-API-Key", apiKey);

        String base64Image;
        try {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            base64Image = Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("base64Image:");
        System.out.println(base64Image);

        Map<String, Object> body = new HashMap<>();
        body.put("imageBase64", base64Image);
        body.put("userId", userId);
        body.put("invoiceFileId", invoiceFileId);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        System.out.println("Jawollo!!!");

        ResponseEntity<String> response = restTemplate.exchange(
                "http://ai-logic-et:8000/upload",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        System.out.println("Jawollo!!!2");

        System.out.println("Response code: " + response.getStatusCode());
    }



















    /*public String uploadInvoiceFile(MultipartFile file, String userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Error: Invoice is empty");
        }

        if (!isValidFileType(file)) {
            throw new IllegalArgumentException("Error: Invalid file type. Only PNG, JPG, JPEG, and PDF are allowed.");
        }

        String fileName = fileManagerService.createUniqueFileName(file);

        InvoiceFile invoiceFile = new InvoiceFile();
        invoiceFile.setInvoiceUrl(fileName);
        invoiceFile.setUploadSuccessful(false);
        invoiceFile.setUploadTime(java.sql.Time.valueOf(LocalTime.now()));
        invoiceFile.setUploadDate(LocalDate.now());
        invoiceFileRepository.save(invoiceFile);

        String newInvoiceUrl = invoiceFile.getId() + "_" + fileName;
        invoiceFile.setInvoiceUrl(newInvoiceUrl);

        invoiceFileRepository.save(invoiceFile);

        // Use MultipartFile directly instead of saving and reading from a file path
        //uploadFileToEndpoint(file, apiKey, userId, invoiceFile.getId());

        return "Invoice uploaded successfully";
    }

    *//**
     * Uploads a file to the given endpoint using MultipartForm.
     *
     * @param file          The MultipartFile to be uploaded.
     * @param apiKey        The API key.
     * @param userId        The ID of the user uploading the file.
     * @param invoiceFileId The ID of the invoice file.
     *//*
    public void uploadFileToEndpoint(MultipartFile file, String apiKey, String userId, Long invoiceFileId) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("X-API-Key", apiKey);

        // Convert MultipartFile to a Resource
        Resource fileResource = file.getResource();

        // Create a MultiValueMap for the request body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);
        body.add("userId", userId);
        body.add("invoiceFileId", invoiceFileId);

        // Create a HttpEntity with headers and body
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Make the request and get the response
        ResponseEntity<String> response = restTemplate.exchange(
                "http://ai-logic-et:8000/upload",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        System.out.println("Response code: " + response.getStatusCode());
    }*/


    /**
     * Checks if the given file is a valid file type.
     *
     * @param file The file to be checked.
     * @return True if the file is a valid file type, false otherwise.
     */
    private boolean isValidFileType(MultipartFile file) {
        try {
            Tika tika = new Tika();
            String detectedType = tika.detect(IOUtils.toByteArray(file.getInputStream()));

            List<String> allowedContentTypes = Arrays.asList(allowedFileTypes.split(","));

            return allowedContentTypes.contains(detectedType);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file content", e);
        }
    }
}
