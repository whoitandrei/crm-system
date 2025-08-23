package ru.shift.zverev.crm_system.service;

import ru.shift.zverev.crm_system.model.Seller;
import ru.shift.zverev.crm_system.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class TransactionService implements TransactionServiceInterface{
    @Override
    public List<Seller> getAll() {
        return List.of();
    }

    @Override
    public Optional<Seller> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public Seller create(Seller seller) {
        return null;
    }

    @Override
    public Seller update(Long id, Seller sellerDetails) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<Transaction> getTransactionsBySellerId(Long id) {
        return List.of();
    }

    @Override
    public List<Transaction> getTransactionsBySellerIdAndPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate) {
        return List.of();
    }

    @Override
    public BigDecimal getTotalAmountBySellerId(Long id) {
        return null;
    }

    @Override
    public BigDecimal getTotalAmountBySellerIdAndPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate) {
        return null;
    }

    @Override
    public Map<String, BigDecimal> getSalesStatisticsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return Map.of();
    }

    @Override
    public boolean validateTransactionAmount(BigDecimal amount) {
        return false;
    }

    @Override
    public boolean isTransactionDateValid(LocalDateTime transactionDate) {
        return false;
    }
}
