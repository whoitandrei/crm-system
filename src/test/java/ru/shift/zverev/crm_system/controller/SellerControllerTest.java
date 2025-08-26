package ru.shift.zverev.crm_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.shift.zverev.crm_system.dto.SellerRequest;
import ru.shift.zverev.crm_system.model.Seller;
import ru.shift.zverev.crm_system.service.SellerServiceInterface;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class SellerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SellerServiceInterface sellerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Seller testSeller;
    private SellerRequest testSellerRequest;
    private LocalDateTime testDate;

    @BeforeEach
    void setUp() {

        testDate = LocalDateTime.now();
        testSeller = new Seller("Тест Продавец", testDate);
        testSeller.setId(1L);
        testSeller.setContactInfo("test@example.com");

        testSellerRequest = new SellerRequest();
        testSellerRequest.setName("Тест Продавец");
        testSellerRequest.setContactInfo("test@example.com");
    }

    @Test
    void testGetAllSellers() throws Exception {
        
        List<Seller> sellers = Arrays.asList(testSeller);
        when(sellerService.getAll()).thenReturn(sellers);

        
        mockMvc.perform(get("/api/sellers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Тест Продавец"))
                .andExpect(jsonPath("$[0].contactInfo").value("test@example.com"));

        verify(sellerService).getAll();
    }

    @Test
    void testGetSellerById_Found() throws Exception {
        
        when(sellerService.getById(1L)).thenReturn(Optional.of(testSeller));

        
        mockMvc.perform(get("/api/sellers/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тест Продавец"))
                .andExpect(jsonPath("$.contactInfo").value("test@example.com"));

        verify(sellerService).getById(1L);
    }

    @Test
    void testGetSellerById_NotFound() throws Exception {
        
        when(sellerService.getById(999L)).thenReturn(Optional.empty());

        
        mockMvc.perform(get("/api/sellers/999"))
                .andExpect(status().isNotFound());

        verify(sellerService).getById(999L);
    }

    @Test
    void testCreateSeller_Valid() throws Exception {
        
        when(sellerService.create(any(Seller.class))).thenReturn(testSeller);

        
        mockMvc.perform(post("/api/sellers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSellerRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тест Продавец"))
                .andExpect(jsonPath("$.contactInfo").value("test@example.com"));

        verify(sellerService).create(any(Seller.class));
    }

    @Test
    void testCreateSeller_Invalid() throws Exception {
        
        SellerRequest invalidRequest = new SellerRequest();
        invalidRequest.setName(""); // Пустое имя
        invalidRequest.setContactInfo("test@example.com");

        
        mockMvc.perform(post("/api/sellers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(sellerService, never()).create(any());
    }

    @Test
    void testUpdateSeller_Valid() throws Exception {
        
        when(sellerService.update(eq(1L), any(Seller.class))).thenReturn(testSeller);

        
        mockMvc.perform(put("/api/sellers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSellerRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тест Продавец"));

        verify(sellerService).update(eq(1L), any(Seller.class));
    }

    @Test
    void testDeleteSeller() throws Exception {
        
        doNothing().when(sellerService).delete(1L);

        
        mockMvc.perform(delete("/api/sellers/1"))
                .andExpect(status().isNoContent());

        verify(sellerService).delete(1L);
    }

    @Test
    void testGetMostProductiveSeller() throws Exception {
        
        when(sellerService.getMostProductiveSellerOfAllTime()).thenReturn(testSeller);

        
        mockMvc.perform(get("/api/sellers/analytics/most-productive"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тест Продавец"));

        verify(sellerService).getMostProductiveSellerOfAllTime();
    }

    @Test
    void testGetMostProductiveSellerByPeriod() throws Exception {
        
        when(sellerService.getMostProductiveSellerByPeriod(30)).thenReturn(testSeller);

        
        mockMvc.perform(get("/api/sellers/analytics/most-productive/30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тест Продавец"));

        verify(sellerService).getMostProductiveSellerByPeriod(30);
    }

    @Test
    void testGetSellersWithLowPerformance() throws Exception {
        
        List<Seller> lowPerformers = Arrays.asList(testSeller);
        BigDecimal limit = new BigDecimal("100.00");
        when(sellerService.getSellersAmountLessThan(limit)).thenReturn(lowPerformers);

        
        mockMvc.perform(get("/api/sellers/analytics/low-performance")
                .param("limit", "100.00"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Тест Продавец"));

        verify(sellerService).getSellersAmountLessThan(any(BigDecimal.class));
    }
}
