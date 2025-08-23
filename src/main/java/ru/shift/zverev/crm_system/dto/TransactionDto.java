package ru.shift.zverev.crm_system.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
public class TransactionDto {
    private Long id;
    private Long sellerId;
    private String sellerName;
    private BigDecimal amount;
    private String paymentType;
    private LocalDateTime transactionDate;

    public TransactionDto() {}

    public TransactionDto(Long id, Long sellerId, String sellerName, BigDecimal amount,
                          String paymentType, LocalDateTime transactionDate) {
        this.id = id;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.amount = amount;
        this.paymentType = paymentType;
        this.transactionDate = transactionDate;
    }
}
