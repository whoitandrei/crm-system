package ru.shift.zverev.crm_system.repository;


import org.junit.jupiter.api.BeforeEach;
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
class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransactionRepository transactionRepository;

    private Seller testSeller1;
    private Seller testSeller2;
    private Transaction testTransaction1;
    private Transaction testTransaction2;
    private Transaction testTransaction3;
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

        testTransaction1 = new Transaction(testSeller1, new BigDecimal("150.00"), 
                Transaction.PaymentType.CARD, testDate.minusDays(10));
        testTransaction2 = new Transaction(testSeller1, new BigDecimal("250.00"), 
                Transaction.PaymentType.CASH, testDate.minusDays(5));
        testTransaction3 = new Transaction(testSeller2, new BigDecimal("75.00"), 
                Transaction.PaymentType.CARD, testDate.minusDays(3));
        
        entityManager.persist(testTransaction1);
        entityManager.persist(testTransaction2);
        entityManager.persist(testTransaction3);
        entityManager.flush();
    }

    @Test
    void testFindAll() {
        
        List<Transaction> transactions = transactionRepository.findAll();

        
        assertEquals(3, transactions.size());
    }

    @Test
    void testFindById() {
        
        Transaction found = transactionRepository.findById(testTransaction1.getId()).orElse(null);

        
        assertNotNull(found);
        assertEquals(new BigDecimal("150.00"), found.getAmount());
        assertEquals(Transaction.PaymentType.CARD, found.getPaymentType());
        assertEquals(testSeller1.getId(), found.getSeller().getId());
    }

    @Test
    void testFindBySellerId() {
        
        List<Transaction> transactions = transactionRepository.findBySellerId(testSeller1.getId());

        
        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().allMatch(t -> t.getSeller().getId().equals(testSeller1.getId())));

        assertTrue(transactions.stream().anyMatch(t -> t.getAmount().equals(new BigDecimal("150.00"))));
        assertTrue(transactions.stream().anyMatch(t -> t.getAmount().equals(new BigDecimal("250.00"))));
    }

    @Test
    void testCountBySellerId() {
        
        Long count1 = transactionRepository.countBySellerId(testSeller1.getId());
        Long count2 = transactionRepository.countBySellerId(testSeller2.getId());

        
        assertEquals(2L, count1);
        assertEquals(1L, count2);
    }

    @Test
    void testGetTotalAmountBySellerId() {
        
        BigDecimal total1 = transactionRepository.getTotalAmountBySellerId(testSeller1.getId());
        BigDecimal total2 = transactionRepository.getTotalAmountBySellerId(testSeller2.getId());

        
        assertEquals(new BigDecimal("400.00"), total1);
        assertEquals(new BigDecimal("75.00"), total2);
    }

    @Test
    void testGetTotalAmountBySellerId_NoTransactions() {
        Seller emptySeller = new Seller("Пустой Продавец", testDate);
        entityManager.persistAndFlush(emptySeller);

        
        BigDecimal total = transactionRepository.getTotalAmountBySellerId(emptySeller.getId());

        
        assertEquals(BigDecimal.ZERO, total);
    }

    @Test
    void testFindTopSellersByPeriod() {
        LocalDateTime start = testDate.minusDays(15);
        LocalDateTime end = testDate;

        
        List<Object[]> results = transactionRepository.findTopSellersByPeriod(start, end);

        
        assertEquals(2, results.size());

        Object[] topSeller = results.get(0);
        assertEquals(testSeller1.getId(), topSeller[0]);
        assertEquals(new BigDecimal("400.00"), topSeller[1]);

        Object[] secondSeller = results.get(1);
        assertEquals(testSeller2.getId(), secondSeller[0]);
        assertEquals(new BigDecimal("75.00"), secondSeller[1]);
    }

    @Test
    void testFindBySellerIdAndDateRange() {
        LocalDateTime start = testDate.minusDays(12);
        LocalDateTime end = testDate.minusDays(4);

        
        List<Transaction> transactions = transactionRepository.findBySellerIdAndDateRange(
                testSeller1.getId(), start, end);

        
        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().allMatch(t -> t.getSeller().getId().equals(testSeller1.getId())));
        assertTrue(transactions.stream().allMatch(t -> 
                t.getTransactionDate().isAfter(start.minusSeconds(1)) && 
                t.getTransactionDate().isBefore(end.plusSeconds(1))));
    }

    @Test
    void testGetTotalAmountBySellerIdAndPeriod() {
        LocalDateTime start = testDate.minusDays(7);
        LocalDateTime end = testDate;

        
        BigDecimal total1 = transactionRepository.getTotalAmountBySellerIdAndPeriod(
                testSeller1.getId(), start, end);
        BigDecimal total2 = transactionRepository.getTotalAmountBySellerIdAndPeriod(
                testSeller2.getId(), start, end);

        
        assertEquals(new BigDecimal("250.00"), total1);
        assertEquals(new BigDecimal("75.00"), total2);
    }

    @Test
    void testSave() {
        Transaction newTransaction = new Transaction(testSeller2, new BigDecimal("100.00"), 
                Transaction.PaymentType.CASH, testDate);

        
        Transaction saved = transactionRepository.save(newTransaction);

        
        assertNotNull(saved.getId());
        assertEquals(new BigDecimal("100.00"), saved.getAmount());
        assertEquals(Transaction.PaymentType.CASH, saved.getPaymentType());
        assertEquals(testSeller2.getId(), saved.getSeller().getId());

        Transaction found = entityManager.find(Transaction.class, saved.getId());
        assertNotNull(found);
        assertEquals(new BigDecimal("100.00"), found.getAmount());
    }

    @Test
    void testUpdate() {
        testTransaction1.setAmount(new BigDecimal("200.00"));
        testTransaction1.setPaymentType(Transaction.PaymentType.CASH);

        
        Transaction updated = transactionRepository.save(testTransaction1);

        
        assertEquals(new BigDecimal("200.00"), updated.getAmount());
        assertEquals(Transaction.PaymentType.CASH, updated.getPaymentType());

        entityManager.flush();
        entityManager.clear();
        Transaction found = entityManager.find(Transaction.class, testTransaction1.getId());
        assertEquals(new BigDecimal("200.00"), found.getAmount());
        assertEquals(Transaction.PaymentType.CASH, found.getPaymentType());
    }

    @Test
    void testDelete() {
        Long transactionId = testTransaction1.getId();

        
        transactionRepository.deleteById(transactionId);

        
        assertFalse(transactionRepository.existsById(transactionId));
        assertNull(entityManager.find(Transaction.class, transactionId));
    }

    @Test
    void testCount() {
        
        long count = transactionRepository.count();

        
        assertEquals(3, count);
    }
}
