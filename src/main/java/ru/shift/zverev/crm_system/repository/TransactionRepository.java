package ru.shift.zverev.crm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import ru.shift.zverev.crm_system.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySellerId(Long id);
    Long countBySellerId(Long id);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.seller.id = :sellerId")
    BigDecimal getTotalAmountBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT t.seller.id, SUM(t.amount) as total " +
            "FROM Transaction t " +
            "WHERE t.transactionDate BETWEEN :start AND :end " +
            "GROUP BY t.seller.id " +
            "ORDER BY total DESC")
    List<Object[]> findTopSellersByPeriod(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    @Query("SELECT t FROM Transaction t WHERE t.seller.id = :sellerId AND t.transactionDate BETWEEN :start AND :end")
    List<Transaction> findBySellerIdAndDateRange(@Param("sellerId") Long sellerId,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.seller.id = :sellerId AND t.transactionDate BETWEEN :start AND :end")
    BigDecimal getTotalAmountBySellerIdAndPeriod(@Param("sellerId") Long sellerId,
                                                  @Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);

    @Query("SELECT t FROM Transaction t WHERE t.seller.id = :sellerId")
    List<Transaction> getAllTransactionsBySellerId(@Param("sellerId") Long sellerId);

}
