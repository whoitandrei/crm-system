package ru.shift.zverev.crm_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockitoBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.shift.zverev.crm_system.dto.TransactionRequest;
import ru.shift.zverev.crm_system.model.Seller;
import ru.shift.zverev.crm_system.model.Transaction;
import ru.shift.zverev.crm_system.service.SellerService;
import ru.shift.zverev.crm_system.service.SellerServiceInterface;
import ru.shift.zverev.crm_system.service.TransactionService;
import ru.shift.zverev.crm_system.service.TransactionServiceInterface;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionServiceInterface transactionService;

    @MockitoBean
    private SellerServiceInterface sellerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Transaction testTransaction;
    private Seller testSeller;
    private TransactionRequest testTransactionRequest;
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

        testTransactionRequest = new TransactionRequest();
        testTransactionRequest.setSellerId(1L);
        testTransactionRequest.setAmount(new BigDecimal("100.00"));
        testTransactionRequest.setPaymentType("CARD");
    }

    @Test
    void testGetAllTransactions() throws Exception {
        
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getAll()).thenReturn(transactions);

        
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].paymentType").value("CARD"));

        verify(transactionService).getAll();
    }

    @Test
    void testGetTransactionById_Found() throws Exception {
        
        when(transactionService.getById(1L)).thenReturn(Optional.of(testTransaction));

        
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.paymentType").value("CARD"));

        verify(transactionService).getById(1L);
    }

    @Test
    void testGetTransactionById_NotFound() throws Exception {
        
        when(transactionService.getById(999L)).thenReturn(Optional.empty());

        
        mockMvc.perform(get("/api/transactions/999"))
                .andExpect(status().isNotFound());

        verify(transactionService).getById(999L);
    }

    @Test
    void testCreateTransaction_Valid() throws Exception {
        
        when(sellerService.getById(1L)).thenReturn(Optional.of(testSeller));
        when(transactionService.create(any(Transaction.class))).thenReturn(testTransaction);

        
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTransactionRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.paymentType").value("CARD"));

        verify(transactionService).create(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_Invalid() throws Exception {
        
        TransactionRequest invalidRequest = new TransactionRequest();
        invalidRequest.setSellerId(1L);
        invalidRequest.setAmount(new BigDecimal("-10.00")); // Отрицательная сумма
        invalidRequest.setPaymentType("CARD");

        
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).create(any());
    }

    @Test
    void testUpdateTransaction() throws Exception {
        
        when(transactionService.update(eq(1L), any(Transaction.class))).thenReturn(testTransaction);
        when(sellerService.getById(1L)).thenReturn(Optional.of(testSeller));

        
        mockMvc.perform(put("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTransactionRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00));

        verify(transactionService).update(eq(1L), any(Transaction.class));
    }

    @Test
    void testDeleteTransaction() throws Exception {
        
        doNothing().when(transactionService).delete(1L);

        
        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isNoContent());

        verify(transactionService).delete(1L);
    }

    @Test
    void testGetTransactionsBySeller() throws Exception {
        
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getTransactionsBySellerId(1L)).thenReturn(transactions);

        
        mockMvc.perform(get("/api/transactions/seller/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].sellerId").value(1));

        verify(transactionService).getTransactionsBySellerId(1L);
    }

    @Test
    void testGetTotalAmountBySeller() throws Exception {
        
        BigDecimal totalAmount = new BigDecimal("500.00");
        when(transactionService.getTotalAmountBySellerId(1L)).thenReturn(totalAmount);

        
        mockMvc.perform(get("/api/transactions/analytics/total/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalAmount").value(500.00));

        verify(transactionService).getTotalAmountBySellerId(1L);
    }

    @Test
    void testGetSalesStatistics() throws Exception {
        
        Map<String, BigDecimal> statistics = new HashMap<>();
        statistics.put("totalSales", new BigDecimal("1000.00"));
        statistics.put("transactionCount", new BigDecimal("5"));
        
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        
        when(transactionService.getSalesStatisticsByPeriod(any(), any())).thenReturn(statistics);

        
        mockMvc.perform(get("/api/transactions/analytics/statistics")
                .param("startDate", start.toString())
                .param("endDate", end.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalSales").value(1000.00))
                .andExpect(jsonPath("$.transactionCount").value(5));

        verify(transactionService).getSalesStatisticsByPeriod(any(), any());
    }
}
