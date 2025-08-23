package ru.shift.zverev.crm_system.service;

import ru.shift.zverev.crm_system.model.Seller;
import ru.shift.zverev.crm_system.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface SellerServiceInterface {
    List<Seller> getAll();
    Optional<Seller> getById(Long id);
    Seller create(Seller seller);
    Seller update(Long id, Seller sellerDetails);
    void delete(Long id);

    Seller getMostProductiveSellerOfAllTime();
    Seller getMostProductiveSellerByPeriod(int period);

    List<Seller> getSellersAmountLessThan(BigDecimal limit);
    List<Seller> getSellersAmountLessThanAndPeriod(BigDecimal limit, LocalDateTime startDate, LocalDateTime EndDate);

    boolean validateSeller(Seller seller);
    boolean isSellerExists(Long sellerId);
}
