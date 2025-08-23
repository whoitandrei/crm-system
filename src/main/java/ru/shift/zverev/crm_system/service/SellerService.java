package ru.shift.zverev.crm_system.service;

import ru.shift.zverev.crm_system.model.Seller;
import ru.shift.zverev.crm_system.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public class SellerService implements SellerServiceInterface{

    @Override
    public List<Transaction> getAll() {
        return List.of();
    }

    @Override
    public Optional<Transaction> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public Transaction create(Transaction transaction) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Seller getMostProductiveSellerOfAllTime() {
        return null;
    }

    @Override
    public Seller getMostProductiveSellerByPeriod(int period) {
        return null;
    }

    @Override
    public List<Seller> getSellersAmountLessThan(BigDecimal limit) {
        return List.of();
    }

    @Override
    public List<Seller> getSellersAmountLessThanAndPeriod(BigDecimal limit, LocalDateTime startDate, LocalDateTime EndDate) {
        return List.of();
    }

    @Override
    public boolean validateSeller(Seller seller) {
        return false;
    }

    @Override
    public boolean isSellerExists(Long sellerId) {
        return false;
    }
}
