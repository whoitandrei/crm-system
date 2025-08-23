package ru.shift.zverev.crm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.shift.zverev.crm_system.model.Seller;
import ru.shift.zverev.crm_system.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    List<Seller> findByName(String name);
    boolean existByName(String name);

    @Query("SELECT s FROM Seller s WHERE s.registrationDate BETWEEN :start AND :end")
    List<Seller> findByRegistrationPeriod(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    @Query("SELECT s FROM Seller s WHERE " +
            "(SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.seller = s) < :minAmount")
    List<Seller> findWithTotalSalesLessThan(@Param("minAmount") BigDecimal minAmount);

}
