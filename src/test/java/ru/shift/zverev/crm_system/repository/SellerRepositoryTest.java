package ru.shift.zverev.crm_system.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;
import ru.shift.zverev.crm_system.model.Seller;
import ru.shift.zverev.crm_system.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
class SellerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SellerRepository sellerRepository;

    private Seller testSeller1;
    private Seller testSeller2;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.now();
        
        testSeller1 = new Seller("Иван Иванов", testDate.minusMonths(6));
        testSeller1.setContactInfo("ivan@example.com");
        
        testSeller2 = new Seller("Мария Петрова", testDate.minusMonths(3));
        testSeller2.setContactInfo("maria@example.com");

        entityManager.persistAndFlush(testSeller1);
        entityManager.persistAndFlush(testSeller2);

        Transaction transaction1 = new Transaction(testSeller1, new BigDecimal("150.00"), 
                Transaction.PaymentType.CARD, testDate.minusDays(10));
        Transaction transaction2 = new Transaction(testSeller1, new BigDecimal("250.00"), 
                Transaction.PaymentType.CASH, testDate.minusDays(5));
        Transaction transaction3 = new Transaction(testSeller2, new BigDecimal("75.00"), 
                Transaction.PaymentType.CARD, testDate.minusDays(3));
        
        entityManager.persist(transaction1);
        entityManager.persist(transaction2);
        entityManager.persist(transaction3);
        entityManager.flush();
    }

    @Test
    void testFindAll() {
        
        List<Seller> sellers = sellerRepository.findAll();

        
        assertEquals(2, sellers.size());
        assertTrue(sellers.stream().anyMatch(s -> "Иван Иванов".equals(s.getName())));
        assertTrue(sellers.stream().anyMatch(s -> "Мария Петрова".equals(s.getName())));
    }

    @Test
    void testFindById() {
        
        Seller found = sellerRepository.findById(testSeller1.getId()).orElse(null);

        
        assertNotNull(found);
        assertEquals("Иван Иванов", found.getName());
        assertEquals("ivan@example.com", found.getContactInfo());
    }

    @Test
    void testFindByName_Found() {
        
        List<Seller> sellers = sellerRepository.findByName("Иван Иванов");

        
        assertEquals(1, sellers.size());
        assertEquals("Иван Иванов", sellers.get(0).getName());
        assertEquals("ivan@example.com", sellers.get(0).getContactInfo());
    }

    @Test
    void testFindByName_NotFound() {
        
        List<Seller> sellers = sellerRepository.findByName("Несуществующий Продавец");

        
        assertTrue(sellers.isEmpty());
    }

    @Test
    void testExistsByName_True() {
        
        boolean exists = sellerRepository.existsByName("Иван Иванов");

        
        assertTrue(exists);
    }

    @Test
    void testExistsByName_False() {
        
        boolean exists = sellerRepository.existsByName("Несуществующий Продавец");

        
        assertFalse(exists);
    }

    @Test
    void testFindByRegistrationPeriod() {
        LocalDateTime start = testDate.minusMonths(4);
        LocalDateTime end = testDate.minusMonths(2);

        
        List<Seller> sellers = sellerRepository.findByRegistrationPeriod(start, end);

        
        assertEquals(1, sellers.size());
        assertEquals("Мария Петрова", sellers.get(0).getName());
    }

    @Test
    void testFindWithTotalSalesLessThan() {
        BigDecimal limit = new BigDecimal("100.00");

        
        List<Seller> sellers = sellerRepository.findWithTotalSalesLessThan(limit);

        
        assertEquals(1, sellers.size());
        assertEquals("Мария Петрова", sellers.get(0).getName());
    }

    @Test
    void testSave() {
        Seller newSeller = new Seller("Петр Сидоров", testDate);
        newSeller.setContactInfo("petr@example.com");

        
        Seller saved = sellerRepository.save(newSeller);

        
        assertNotNull(saved.getId());
        assertEquals("Петр Сидоров", saved.getName());
        assertEquals("petr@example.com", saved.getContactInfo());
        
        Seller found = entityManager.find(Seller.class, saved.getId());
        assertNotNull(found);
        assertEquals("Петр Сидоров", found.getName());
    }

    @Test
    void testUpdate() {
        testSeller1.setName("Иван Петрович");
        testSeller1.setContactInfo("ivan.petrovich@example.com");

        
        Seller updated = sellerRepository.save(testSeller1);

        
        assertEquals("Иван Петрович", updated.getName());
        assertEquals("ivan.petrovich@example.com", updated.getContactInfo());
        
        entityManager.flush();
        entityManager.clear();
        Seller found = entityManager.find(Seller.class, testSeller1.getId());
        assertEquals("Иван Петрович", found.getName());
    }

    @Test
    void testDelete() {
        Long sellerId = testSeller1.getId();

        entityManager.getEntityManager()
                .createNativeQuery("DELETE FROM transactions WHERE seller_id = :sellerId")
                .setParameter("sellerId", sellerId)
                .executeUpdate();

        entityManager.getEntityManager()
                .createNativeQuery("DELETE FROM sellers WHERE id = :sellerId")
                .setParameter("sellerId", sellerId)
                .executeUpdate();
        entityManager.flush();

        assertFalse(sellerRepository.existsById(sellerId));
    }

    @Test
    void testCount() {
        long count = sellerRepository.count();

        assertEquals(2, count);
    }
}
