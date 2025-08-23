package ru.shift.zverev.crm_system.service;

import ru.shift.zverev.crm_system.model.Seller;
import ru.shift.zverev.crm_system.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface TransactionServiceInterface {
    List<Transaction> getAll();
    Optional<Transaction> getById(Long id);
    Transaction create(Transaction transaction);
    Transaction update(Long id, Transaction transactionDetails);
    void delete(Long id);

    List<Transaction> getTransactionsBySellerId(Long id);
    List<Transaction> getTransactionsBySellerIdAndPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate);

    BigDecimal getTotalAmountBySellerId(Long id);
    BigDecimal getTotalAmountBySellerIdAndPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate);

    Map<String, BigDecimal> getSalesStatisticsByPeriod(LocalDateTime startDate, LocalDateTime endDate);

    boolean validateTransactionAmount(BigDecimal amount);
    boolean isTransactionDateValid(LocalDateTime transactionDate);
}
