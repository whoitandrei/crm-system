package ru.shift.zverev.crm_system.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shift.zverev.crm_system.model.Seller;
import ru.shift.zverev.crm_system.model.Transaction;
import ru.shift.zverev.crm_system.repository.SellerRepository;
import ru.shift.zverev.crm_system.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction testTransaction;
    private Seller testSeller;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.now();
        testSeller = new Seller("Тест Продавец", testDate);
        testSeller.setId(1L);
        
        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setSeller(testSeller);
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setPaymentType(Transaction.PaymentType.CARD);
        testTransaction.setTransactionDate(testDate);
    }

    @Test
    void testGetAll() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.getAll();

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("100.00"), result.get(0).getAmount());
        verify(transactionRepository).findAll();
    }

    @Test
    void testGetById_Found() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        Optional<Transaction> result = transactionService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("100.00"), result.get().getAmount());
        verify(transactionRepository).findById(1L);
    }

    @Test
    void testCreate_ValidTransaction() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        Transaction result = transactionService.create(testTransaction);

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void testCreate_InvalidAmount() {
        testTransaction.setAmount(new BigDecimal("-10.00"));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> transactionService.create(testTransaction)
        );
        assertEquals("Invalid transaction amount", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testCreate_InvalidDate() {
        testTransaction.setTransactionDate(LocalDateTime.now().plusDays(1));

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> transactionService.create(testTransaction)
        );
        assertEquals("Invalid transaction date", exception.getMessage());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testUpdate_ExistingTransaction() {
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);
        
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setAmount(new BigDecimal("200.00"));
        updatedTransaction.setPaymentType(Transaction.PaymentType.CASH);
        updatedTransaction.setSeller(testSeller);

        Transaction result = transactionService.update(1L, updatedTransaction);

        assertNotNull(result);
        verify(transactionRepository).findById(1L);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testDelete_ExistingTransaction() {
        when(transactionRepository.existsById(1L)).thenReturn(true);

        transactionService.delete(1L);

        verify(transactionRepository).existsById(1L);
        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void testDelete_NonExistingTransaction() {
        when(transactionRepository.existsById(999L)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class, 
            () -> transactionService.delete(999L)
        );
        assertEquals("Transaction not found with id: 999", exception.getMessage());
        verify(transactionRepository).existsById(999L);
        verify(transactionRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetTransactionsBySellerId() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findBySellerId(1L)).thenReturn(transactions);

        List<Transaction> result = transactionService.getTransactionsBySellerId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getSeller().getId());
        verify(transactionRepository).findBySellerId(1L);
    }

    @Test
    void testGetTotalAmountBySellerId() {
        BigDecimal totalAmount = new BigDecimal("500.00");
        when(transactionRepository.getTotalAmountBySellerId(1L)).thenReturn(totalAmount);

        BigDecimal result = transactionService.getTotalAmountBySellerId(1L);

        assertEquals(totalAmount, result);
        verify(transactionRepository).getTotalAmountBySellerId(1L);
    }

    @Test
    void testGetSalesStatisticsByPeriod() {
        Object[] result1 = {1L, new BigDecimal("300.00")};
        Object[] result2 = {2L, new BigDecimal("200.00")};
        List<Object[]> results = Arrays.asList(result1, result2);
        
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        
        when(transactionRepository.findTopSellersByPeriod(start, end)).thenReturn(results);

        Map<String, BigDecimal> statistics = transactionService.getSalesStatisticsByPeriod(start, end);

        assertNotNull(statistics);
        assertEquals(new BigDecimal("500.00"), statistics.get("totalSales"));
        assertEquals(BigDecimal.valueOf(2), statistics.get("transactionCount"));
        verify(transactionRepository).findTopSellersByPeriod(start, end);
    }

    @Test
    void testValidateTransactionAmount_Valid() {
        BigDecimal validAmount = new BigDecimal("50.00");

        boolean result = transactionService.validateTransactionAmount(validAmount);

        assertTrue(result);
    }

    @Test
    void testValidateTransactionAmount_Invalid() {
        BigDecimal invalidAmount = new BigDecimal("-10.00");

        boolean result = transactionService.validateTransactionAmount(invalidAmount);

        assertFalse(result);
    }

    @Test
    void testValidateTransactionAmount_Null() {
        boolean result = transactionService.validateTransactionAmount(null);

        assertFalse(result);
    }

    @Test
    void testIsTransactionDateValid_Valid() {
        LocalDateTime validDate = LocalDateTime.now().minusHours(1);

        boolean result = transactionService.isTransactionDateValid(validDate);

        assertTrue(result);
    }

    @Test
    void testIsTransactionDateValid_Future() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);

        boolean result = transactionService.isTransactionDateValid(futureDate);

        assertFalse(result);
    }

    @Test
    void testToDto() {
        var result = TransactionService.toDto(testTransaction);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getSellerId());
        assertEquals("Тест Продавец", result.getSellerName());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        assertEquals("CARD", result.getPaymentType());
        assertEquals(testDate, result.getTransactionDate());
    }
}
