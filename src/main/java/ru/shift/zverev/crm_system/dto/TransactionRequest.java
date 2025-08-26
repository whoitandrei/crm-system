package ru.shift.zverev.crm_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class TransactionRequest {
    @NotNull(message = "seller id is mandatory")
    private Long sellerId;

    @NotNull(message = "payment type is mandatory")
    private String paymentType;

    @NotNull(message = "amount is mandatory")
    @DecimalMin(value = "0.0", inclusive = false, message = "amount must be greater than 0")
    private BigDecimal amount;
}
