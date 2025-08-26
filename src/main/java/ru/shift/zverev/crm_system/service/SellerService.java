package ru.shift.zverev.crm_system.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.val;
import org.antlr.v4.runtime.misc.Pair;
import org.hibernate.dialect.function.array.H2ArraySetFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.shift.zverev.crm_system.dto.SellerDto;
import ru.shift.zverev.crm_system.model.Seller;
import ru.shift.zverev.crm_system.model.Transaction;
import ru.shift.zverev.crm_system.repository.SellerRepository;
import ru.shift.zverev.crm_system.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class SellerService implements SellerServiceInterface{

    private final SellerRepository sellerRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public SellerService(SellerRepository sellerRepository, TransactionRepository transactionRepository) {
        this.sellerRepository = sellerRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Seller> getAll() {
        return sellerRepository.findAll();
    }

    @Override
    public Optional<Seller> getById(Long id) {
        return sellerRepository.findById(id);
    }

    @Override
    public Seller create(Seller seller) {
        if (!validateSeller(seller)) {
            throw new IllegalArgumentException("Invalid seller data");
        }
        return sellerRepository.save(seller);
    }

    @Override
    public Seller update(Long id, Seller sellerDetails) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found with id: " + id));
        
        seller.setName(sellerDetails.getName());
        seller.setContactInfo(sellerDetails.getContactInfo());
        
        if (!validateSeller(seller)) {
            throw new IllegalArgumentException("Invalid seller data");
        }
        
        return sellerRepository.save(seller);
    }

    @Override
    public void delete(Long id) {
        if (!sellerRepository.existsById(id)) {
            throw new EntityNotFoundException("Seller not found with id: " + id);
        }
        sellerRepository.deleteById(id);
    }

    @Override
    public Seller getMostProductiveSellerOfAllTime() {
        List<Object[]> results = transactionRepository.findTopSellersByPeriod(
                LocalDateTime.of(1900, 1, 1, 0, 0), 
                LocalDateTime.now()
        );
        
        if (results.isEmpty()) {
            return null;
        }
        
        Long sellerId = (Long) results.get(0)[0];
        return sellerRepository.findById(sellerId).orElse(null);
    }

    @Override
    public Seller getMostProductiveSellerByPeriod(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        LocalDateTime endDate = LocalDateTime.now();
        
        List<Object[]> results = transactionRepository.findTopSellersByPeriod(startDate, endDate);
        
        if (results.isEmpty()) {
            return null;
        }
        
        Long sellerId = (Long) results.get(0)[0];
        return sellerRepository.findById(sellerId).orElse(null);
    }

    @Override
    public List<Seller> getSellersAmountLessThan(BigDecimal limit) {
        return sellerRepository.findWithTotalSalesLessThan(limit);
    }

    @Override
    public List<Seller> getSellersAmountLessThanAndPeriod(BigDecimal limit, LocalDateTime startDate, LocalDateTime endDate) {
        return sellerRepository.findWithTotalSalesLessThan(limit);
    }

    @Override
    public Pair<LocalDate, LocalDate> getMostProductiveTimeById(Long id, Long days) {
        List<Transaction> allSellerTransactions = transactionRepository.findBySellerId(id);

        if (allSellerTransactions.isEmpty()) {
            return null;
        }

        Map<LocalDate, Integer> dailyTransactions = new HashMap<>();

        for (Transaction transaction : allSellerTransactions) {
            LocalDate transactionDate = transaction.getTransactionDate().toLocalDate();
            dailyTransactions.put(transactionDate, dailyTransactions.getOrDefault(transactionDate, 0) + 1);
        }

        List<LocalDate> sortedDates = new ArrayList<>(dailyTransactions.keySet());
        Collections.sort(sortedDates);

        // если транзакций меньше ччем просят диапазон - вернуть все
        if (sortedDates.size() <= days) {
            LocalDate startDate = sortedDates.get(0);
            LocalDate endDate = sortedDates.get(sortedDates.size() - 1);
            return new Pair<>(startDate, endDate);
        }

        int maxSum = 0;
        int currentSum = 0;
        int bestStartIndex = 0;

        for (int i = 0; i < days; i++) {
            currentSum += dailyTransactions.getOrDefault(sortedDates.get(i), 0);
        }
        maxSum = currentSum;

        for (int i = days.intValue(); i < sortedDates.size(); i++) {
            currentSum = currentSum
                    - dailyTransactions.getOrDefault(sortedDates.get(i - days.intValue()), 0)
                    + dailyTransactions.getOrDefault(sortedDates.get(i), 0);

            if (currentSum > maxSum) {
                maxSum = currentSum;
                bestStartIndex = i - days.intValue() + 1;
            }
        }

        LocalDate startDate = sortedDates.get(bestStartIndex);
        LocalDate endDate = sortedDates.get(bestStartIndex + days.intValue() - 1);

        return new Pair<>(startDate, endDate);
    }

    @Override
    public boolean validateSeller(Seller seller) {
        if (seller == null) {
            return false;
        }
        if (seller.getName() == null || seller.getName().trim().isEmpty()) {
            return false;
        }
        if (seller.getName().length() > 100) {
            return false;
        }
        if (seller.getContactInfo() != null && seller.getContactInfo().length() > 255) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isSellerExists(Long sellerId) {
        return sellerRepository.existsById(sellerId);
    }

    public static SellerDto toDto(Seller seller) {
        if (seller == null) {
            return null;
        }
        
        SellerDto sellerDto = new SellerDto();
        sellerDto.setId(seller.getId());
        sellerDto.setName(seller.getName());
        sellerDto.setContactInfo(seller.getContactInfo());
        sellerDto.setRegistrationDate(seller.getRegistrationDate());
        return sellerDto;
    }
}
