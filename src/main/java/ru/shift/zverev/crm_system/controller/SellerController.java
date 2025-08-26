package ru.shift.zverev.crm_system.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.shift.zverev.crm_system.dto.SellerDto;
import ru.shift.zverev.crm_system.dto.SellerRequest;
import ru.shift.zverev.crm_system.model.Seller;
import ru.shift.zverev.crm_system.service.SellerServiceInterface;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.antlr.v4.runtime.misc.Pair;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sellers")
@CrossOrigin(origins = "*")
public class SellerController {

    private final SellerServiceInterface sellerService;

    @Autowired
    public SellerController(SellerServiceInterface sellerService) {
        this.sellerService = sellerService;
    }

    @GetMapping
    public ResponseEntity<List<SellerDto>> getAllSellers() {
        List<Seller> sellers = sellerService.getAll();
        List<SellerDto> sellerDtos = sellers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sellerDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SellerDto> getSellerById(@PathVariable Long id) {
        return sellerService.getById(id)
                .map(seller -> ResponseEntity.ok(convertToDto(seller)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SellerDto> createSeller(@Valid @RequestBody SellerRequest sellerRequest) {
        Seller seller = convertToEntity(sellerRequest);
        seller.setRegistrationDate(LocalDateTime.now());
        Seller savedSeller = sellerService.create(seller);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(savedSeller));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SellerDto> updateSeller(@PathVariable Long id, @Valid @RequestBody SellerRequest sellerRequest) {
        Seller updatedSeller = sellerService.update(id, convertToEntity(sellerRequest));
        return ResponseEntity.ok(convertToDto(updatedSeller));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable Long id) {
        sellerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Analytics endpoints
    @GetMapping("/analytics/most-productive")
    public ResponseEntity<SellerDto> getMostProductiveSeller() {
        Seller seller = sellerService.getMostProductiveSellerOfAllTime();
        return seller != null ? ResponseEntity.ok(convertToDto(seller)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/analytics/most-productive/{days}")
    public ResponseEntity<SellerDto> getMostProductiveSellerByPeriod(@PathVariable int days) {
        Seller seller = sellerService.getMostProductiveSellerByPeriod(days);
        return seller != null ? ResponseEntity.ok(convertToDto(seller)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/analytics/low-performance")
    public ResponseEntity<List<SellerDto>> getSellersWithLowPerformance(
            @RequestParam BigDecimal limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<Seller> sellers;
        if (startDate != null && endDate != null) {
            sellers = sellerService.getSellersAmountLessThanAndPeriod(limit, startDate, endDate);
        } else {
            sellers = sellerService.getSellersAmountLessThan(limit);
        }
        
        List<SellerDto> sellerDtos = sellers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sellerDtos);
    }

    @GetMapping("analytics/most-productive-time/{sellerId}/{days}")
    public Pair<LocalDate, LocalDate> getMostProductiveTime(@PathVariable Long sellerId, @PathVariable Long days) {
        Pair<LocalDate, LocalDate> dates = sellerService.getMostProductiveTimeById(sellerId, days);
        return dates;
    }



    private SellerDto convertToDto(Seller seller) {
        return new SellerDto(
                seller.getId(),
                seller.getName(),
                seller.getContactInfo(),
                seller.getRegistrationDate()
        );
    }

    private Seller convertToEntity(SellerRequest sellerRequest) {
        Seller seller = new Seller();
        seller.setName(sellerRequest.getName());
        seller.setContactInfo(sellerRequest.getContactInfo());
        return seller;
    }
}
