package expend_tracker.repositories;

import expend_tracker.model.InvoiceItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceItemRepository extends CrudRepository<InvoiceItem, Long> {

    List<InvoiceItem> findByInvoiceIdAndInvoiceUserId(Long invoiceId, String userId);

    List<InvoiceItem> findByInvoiceUserId(String userId);

    List<InvoiceItem> findByInvoiceUserIdAndInvoiceInvoiceDateBetween(String userId, LocalDate start, LocalDate end);

    List<InvoiceItem> findByInvoiceUserIdAndDateBetween(String userId, LocalDate startDate, LocalDate endDate);

    List<InvoiceItem> findByInvoiceUserIdAndStoreChainIdInAndProductTagIdIn(String userId, List<Long> chainIds, List<Long> tagIds);

    /**
     * This query is used to calculate the total expenses for a user in a given period.
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     */
    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.product.id = :productId AND ii.invoice.userId = :userId AND ii.date BETWEEN :startDate AND :endDate")
    List<InvoiceItem> findByProductIdAndUserIdAndDateBetween(
            @Param("productId") Long productId,
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * This query is used to calculate the total expenses for a user in a given period.
     * @param userId
     * @return
     */
    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.product.id = :productId AND ii.invoice.userId = :userId")
    List<InvoiceItem> findByProductIdAndUserId(
            @Param("productId") Long productId,
            @Param("userId") String userId);

    @Query("SELECT i FROM InvoiceItem i WHERE i.invoice.userId = :userId AND i.date BETWEEN :startDate AND :endDate AND i.store.chain.id IN :chainIds AND i.product.tag.id IN :tagIds")
    List<InvoiceItem> findByUserIdAndDateBetweenAndStoreChainIdInAndProductTagIdIn(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("chainIds") List<Long> chainIds,
            @Param("tagIds") List<Long> tagIds);
}