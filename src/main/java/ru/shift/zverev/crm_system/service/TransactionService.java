package ru.shift.zverev.crm_system.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.shift.zverev.crm_system.dto.TransactionDto;
import ru.shift.zverev.crm_system.model.Transaction;
import ru.shift.zverev.crm_system.repository.TransactionRepository;
import ru.shift.zverev.crm_system.repository.SellerRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TransactionService implements TransactionServiceInterface{

    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, SellerRepository sellerRepository) {
        this.transactionRepository = transactionRepository;
        this.sellerRepository = sellerRepository;
    }

    @Override
    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }

    @Override
    public Optional<Transaction> getById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public Transaction create(Transaction transaction) {
        if (!validateTransactionAmount(transaction.getAmount())) {
            throw new IllegalArgumentException("Invalid transaction amount");
        }
        if (!isTransactionDateValid(transaction.getTransactionDate())) {
            throw new IllegalArgumentException("Invalid transaction date");
        }
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction update(Long id, Transaction transactionDetails) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + id));
        
        transaction.setAmount(transactionDetails.getAmount());
        transaction.setPaymentType(transactionDetails.getPaymentType());
        transaction.setSeller(transactionDetails.getSeller());
        
        if (!validateTransactionAmount(transaction.getAmount())) {
            throw new IllegalArgumentException("Invalid transaction amount");
        }
        
        return transactionRepository.save(transaction);
    }

    @Override
    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    @Override
    public List<Transaction> getTransactionsBySellerId(Long id) {
        return transactionRepository.findBySellerId(id);
    }

    @Override
    public List<Transaction> getTransactionsBySellerIdAndPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findBySellerIdAndDateRange(id, startDate, endDate);
    }

    @Override
    public BigDecimal getTotalAmountBySellerId(Long id) {
        return transactionRepository.getTotalAmountBySellerId(id);
    }

    @Override
    public BigDecimal getTotalAmountBySellerIdAndPeriod(Long id, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.getTotalAmountBySellerIdAndPeriod(id, startDate, endDate);
    }

    @Override
    public Map<String, BigDecimal> getSalesStatisticsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = transactionRepository.findTopSellersByPeriod(startDate, endDate);
        Map<String, BigDecimal> statistics = new HashMap<>();
        
        BigDecimal totalSales = BigDecimal.ZERO;
        for (Object[] result : results) {
            BigDecimal amount = (BigDecimal) result[1];
            totalSales = totalSales.add(amount);
        }
        
        statistics.put("totalSales", totalSales);
        statistics.put("transactionCount", BigDecimal.valueOf(results.size()));
        
        return statistics;
    }

    @Override
    public boolean validateTransactionAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public boolean isTransactionDateValid(LocalDateTime transactionDate) {
        return transactionDate != null && !transactionDate.isAfter(LocalDateTime.now());
    }

    public static TransactionDto toDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        
        return new TransactionDto(
                transaction.getId(),
                transaction.getSeller().getId(),
                transaction.getSeller().getName(),
                transaction.getAmount(),
                transaction.getPaymentType().toString(),
                transaction.getTransactionDate()
        );
    }
}
