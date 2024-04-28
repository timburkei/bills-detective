package expend_tracker.service.userService;


import expend_tracker.dto.userService.ChainDto;
import expend_tracker.model.Chain;
import expend_tracker.model.Store;
import expend_tracker.model.Invoice;
import expend_tracker.repositories.ChainRepository;
import expend_tracker.repositories.InvoiceRepository;
import expend_tracker.repositories.StoreRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChainService {

    private static final Logger log = LogManager.getLogger(ChainService.class);

    private final InvoiceRepository invoiceRepository;
    private final StoreRepository storeRepository;

    public ChainService(InvoiceRepository invoiceRepository, StoreRepository storeRepository) {
        this.invoiceRepository = invoiceRepository;
        this.storeRepository = storeRepository;
    }

    @Transactional(readOnly = true)
    public Set<ChainDto> getChainsForUser(String userId, LocalDate startDate, LocalDate endDate) {
        try {
            List<Invoice> invoices;
            if (startDate != null && endDate != null) {
                invoices = invoiceRepository.findByUserIdAndInvoiceDateBetween(userId, startDate, endDate);
            } else {
                invoices = invoiceRepository.findByUserId(userId);
            }
            Set<Long> storeIds = invoices.stream()
                    .map(Invoice::getStore)
                    .map(Store::getId)
                    .collect(Collectors.toSet());

            Set<Chain> chains = new HashSet<>();
            for (Long storeId : storeIds) {
                Store store = storeRepository.findById(storeId).orElse(null);
                if (store != null) {
                    Chain chain = store.getChain();
                    chains.add(chain);
                }
            }

            return chains.stream().map(this::convertToDto).collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("Error getting chains for user: {}", userId, e);
            throw new ServiceException("Error getting chains for user", e);
        }
    }


    private ChainDto convertToDto(Chain chain) {
        ChainDto dto = new ChainDto();
        dto.setId(chain.getId());
        dto.setName(chain.getName());
        return dto;
    }
}
