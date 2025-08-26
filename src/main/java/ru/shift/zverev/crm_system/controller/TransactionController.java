package ru.shift.zverev.crm_system.controller;

import jakarta.validation.Valid;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.shift.zverev.crm_system.dto.SellerDto;
import ru.shift.zverev.crm_system.dto.TransactionDto;
import ru.shift.zverev.crm_system.dto.TransactionRequest;
import ru.shift.zverev.crm_system.model.Transaction;
import ru.shift.zverev.crm_system.service.TransactionServiceInterface;
import ru.shift.zverev.crm_system.service.SellerServiceInterface;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionServiceInterface transactionService;
    private final SellerServiceInterface sellerService;

    @Autowired
    public TransactionController(TransactionServiceInterface transactionService, SellerServiceInterface sellerService) {
        this.transactionService = transactionService;
        this.sellerService = sellerService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAll();
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransactionById(@PathVariable Long id) {
        return transactionService.getById(id)
                .map(transaction -> ResponseEntity.ok(convertToDto(transaction)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@Valid @RequestBody TransactionRequest transactionRequest) {
        Transaction transaction = convertToEntity(transactionRequest);
        transaction.setTransactionDate(LocalDateTime.now());
        Transaction savedTransaction = transactionService.create(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(savedTransaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDto> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionRequest transactionRequest) {
        Transaction updatedTransaction = transactionService.update(id, convertToEntity(transactionRequest));
        return ResponseEntity.ok(convertToDto(updatedTransaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Filter by seller
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<TransactionDto>> getTransactionsBySeller(
            @PathVariable Long sellerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<Transaction> transactions;
        if (startDate != null && endDate != null) {
            transactions = transactionService.getTransactionsBySellerIdAndPeriod(sellerId, startDate, endDate);
        } else {
            transactions = transactionService.getTransactionsBySellerId(sellerId);
        }
        
        List<TransactionDto> transactionDtos = transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDtos);
    }



    @GetMapping("/analytics/total/{sellerId}")
    public ResponseEntity<Map<String, BigDecimal>> getTotalAmountBySeller(
            @PathVariable Long sellerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        BigDecimal totalAmount;
        if (startDate != null && endDate != null) {
            totalAmount = transactionService.getTotalAmountBySellerIdAndPeriod(sellerId, startDate, endDate);
        } else {
            totalAmount = transactionService.getTotalAmountBySellerId(sellerId);
        }
        
        return ResponseEntity.ok(Map.of("totalAmount", totalAmount));
    }

    @GetMapping("/analytics/statistics")
    public ResponseEntity<Map<String, BigDecimal>> getSalesStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        Map<String, BigDecimal> statistics = transactionService.getSalesStatisticsByPeriod(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }





    private TransactionDto convertToDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getSeller().getId(),
                transaction.getSeller().getName(),
                transaction.getAmount(),
                transaction.getPaymentType().toString(),
                transaction.getTransactionDate()
        );
    }

    private Transaction convertToEntity(TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setSeller(sellerService.getById(transactionRequest.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found with id: " + transactionRequest.getSellerId())));
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setPaymentType(Transaction.PaymentType.valueOf(transactionRequest.getPaymentType().toUpperCase()));
        return transaction;
    }
}
