package expend_tracker.repositories;

import expend_tracker.dto.dashboardPage.ChainExpenseDto;
import expend_tracker.model.Invoice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, Long> {

        /**
         * Fetches all invoices for a specific user within a given date range.
         *
         * @param userId    The ID of the user.
         * @param startDate The start date of the invoice period.
         * @param endDate   The end date of the invoice period.
         * @return List of invoices matching the provided criteria.
         */
        List<Invoice> findByUserIdAndInvoiceDateBetween(String userId, LocalDate startDate, LocalDate endDate);

        /**
         * Fetches all invoices for a specific user.
         *
         * @param userId The ID of the user.
         * @return List of invoices for the user.
         */
        List<Invoice> findByUserId(String userId);

        /**
         * Fetches all invoices for a specific user within a given date range and with specific tags.
         *
         * @param userId    The ID of the user.
         * @param startDate The start date of the invoice period.
         * @param endDate   The end date of the invoice period.
         * @param tagIds    The IDs of the tags.
         * @return List of invoices matching the provided criteria.
         */
        @Query("SELECT i FROM Invoice i JOIN InvoiceItem ii ON i.id = ii.invoice.id WHERE i.userId = :userId AND i.invoiceDate BETWEEN :startDate AND :endDate AND ii.product.tag.id IN :tagIds")
        List<Invoice> findByUserIdAndInvoiceDateBetweenAndTagIdIn(@Param("userId") String userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("tagIds") List<Long> tagIds);

        /**
         * Fetches all invoices for a specific user within a given date range, with specific tags and from specific store chains.
         *
         * @param userId    The ID of the user.
         * @param startDate The start date of the invoice period.
         * @param endDate   The end date of the invoice period.
         * @param chainIds  The IDs of the store chains.
         * @param tagIds    The IDs of the tags.
         * @return List of invoices matching the provided criteria.
         */
        @Query("SELECT i FROM Invoice i JOIN InvoiceItem ii ON i.id = ii.invoice.id WHERE i.userId = :userId AND i.invoiceDate BETWEEN :startDate AND :endDate AND i.store.chain.id IN :chainIds AND ii.product.tag.id IN :tagIds")
        List<Invoice> findByUserIdAndInvoiceDateBetweenAndStoreChainIdInAndTagIdIn(@Param("userId") String userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("chainIds") List<Long> chainIds, @Param("tagIds") List<Long> tagIds);

        /**
         * Fetches all invoices for a specific user from specific store chains.
         *
         * @param userId   The ID of the user.
         * @param chainIds The IDs of the store chains.
         * @return List of invoices matching the provided criteria.
         */
        @Query("SELECT i FROM Invoice i WHERE i.userId = :userId AND i.store.chain.id IN :chainIds")
        List<Invoice> findByUserIdAndStoreChainIdIn(@Param("userId") String userId, @Param("chainIds") List<Long> chainIds);

        /**
         * Fetches all invoices for a specific user with specific tags.
         *
         * @param userId The ID of the user.
         * @param tagIds The IDs of the tags.
         * @return List of invoices matching the provided criteria.
         */
        @Query("SELECT i FROM Invoice i JOIN InvoiceItem ii ON i.id = ii.invoice.id WHERE i.userId = :userId AND ii.product.tag.id IN :tagIds")
        List<Invoice> findByUserIdAndTagIdIn(@Param("userId") String userId, @Param("tagIds") List<Long> tagIds);

        /**
         * Fetches all invoices for a specific user from specific store chains and with specific tags.
         *
         * @param userId   The ID of the user.
         * @param chainIds The IDs of the store chains.
         * @param tagIds   The IDs of the tags.
         * @return List of invoices matching the provided criteria.
         */
        @Query("SELECT i FROM Invoice i JOIN InvoiceItem ii ON i.id = ii.invoice.id WHERE i.userId = :userId AND i.store.chain.id IN :chainIds AND ii.product.tag.id IN :tagIds")
        List<Invoice> findByUserIdAndStoreChainIdInAndTagIdIn(@Param("userId") String userId, @Param("chainIds") List<Long> chainIds, @Param("tagIds") List<Long> tagIds);

        /**
         * Fetches all invoices for a specific user within a given date range and from specific store chains.
         *
         * @param userId    The ID of the user.
         * @param startDate The start date of the invoice period.
         * @param endDate   The end date of the invoice period.
         * @param chainIds  The IDs of the store chains.
         * @return List of invoices matching the provided criteria.
         */
        @Query("SELECT i FROM Invoice i WHERE i.userId = :userId AND i.invoiceDate BETWEEN :startDate AND :endDate AND i.store.chain.id IN :chainIds")
        List<Invoice> findByUserIdAndInvoiceDateBetweenAndStoreChainIdIn(@Param("userId") String userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("chainIds") List<Long> chainIds);


        /**
         * This query is used to calculate the total expenses for a user in a given
         * period.
         * 
         * @param userId
         * @param startDate
         * @param endDate
         * @return
         */
        @Query("SELECT new expend_tracker.dto.dashboardPage.ChainExpenseDto(c.id, c.name, SUM(ii.price)) " +
                        "FROM Invoice i JOIN i.store s JOIN s.chain c JOIN InvoiceItem ii ON ii.invoice.id = i.id " +
                        "WHERE i.userId = :userId " +
                        "AND (cast(:startDate as date) IS NULL OR i.invoiceDate >= :startDate) " +
                        "AND (cast(:endDate as date) IS NULL OR i.invoiceDate <= :endDate) " +
                        "GROUP BY c.id, c.name")
        List<ChainExpenseDto> calculateExpensesForUserAndPeriod(@Param("userId") String userId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

}