package ru.shift.zverev.crm_system.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.shift.zverev.crm_system.model.Seller;
import ru.shift.zverev.crm_system.repository.SellerRepository;
import ru.shift.zverev.crm_system.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private SellerService sellerService;

    private Seller testSeller;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDateTime.now();
        testSeller = new Seller("Тест Продавец", testDate);
        testSeller.setId(1L);
        testSeller.setContactInfo("test@example.com");
    }

    @Test
    void testGetAll() {
        
        List<Seller> sellers = Arrays.asList(testSeller);
        when(sellerRepository.findAll()).thenReturn(sellers);

        List<Seller> result = sellerService.getAll();

        assertEquals(1, result.size());
        assertEquals("Тест Продавец", result.get(0).getName());
        verify(sellerRepository).findAll();
    }

    @Test
    void testGetById_Found() {
        
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));

        Optional<Seller> result = sellerService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals("Тест Продавец", result.get().getName());
        verify(sellerRepository).findById(1L);
    }

    @Test
    void testCreate_ValidSeller() {
        
        when(sellerRepository.save(any(Seller.class))).thenReturn(testSeller);

        Seller result = sellerService.create(testSeller);

        assertNotNull(result);
        assertEquals("Тест Продавец", result.getName());
        verify(sellerRepository).save(testSeller);
    }

    @Test
    void testCreate_InvalidSeller() {
        
        Seller invalidSeller = new Seller("", testDate);

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> sellerService.create(invalidSeller)
        );
        assertEquals("Invalid seller data", exception.getMessage());
        verify(sellerRepository, never()).save(any());
    }

    @Test
    void testUpdate_ExistingSeller() {
        
        Seller updatedSeller = new Seller("Обновленное Имя", testDate);
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));
        when(sellerRepository.save(any(Seller.class))).thenReturn(testSeller);

        Seller result = sellerService.update(1L, updatedSeller);

        assertNotNull(result);
        verify(sellerRepository).findById(1L);
        verify(sellerRepository).save(any(Seller.class));
    }

    @Test
    void testDelete_ExistingSeller() {
        
        when(sellerRepository.existsById(1L)).thenReturn(true);

        sellerService.delete(1L);

        verify(sellerRepository).existsById(1L);
        verify(sellerRepository).deleteById(1L);
    }

    @Test
    void testValidateSeller_Valid() {
        
        Seller validSeller = new Seller("Валидное Имя", testDate);
        validSeller.setContactInfo("valid@example.com");

        boolean result = sellerService.validateSeller(validSeller);

        assertTrue(result);
    }

    @Test
    void testValidateSeller_Invalid() {
        boolean result = sellerService.validateSeller(null);

        assertFalse(result);
    }
}
