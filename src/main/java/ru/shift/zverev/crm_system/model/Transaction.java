package ru.shift.zverev.crm_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {
    public enum PaymentType {
        CARD, CASH
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Seller is mandatory")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="seller_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_seller"))
    private Seller seller;

    @NotNull(message = "amount is mandatory")
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @NotNull(message = "payment type is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false, length = 20)
    private PaymentType paymentType;

    @NotNull(message = "transaction date is mandatory")
    @PastOrPresent(message = "transaction date cannot be in the future")
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    public Transaction() {}

    public Transaction(Seller seller, BigDecimal amount, PaymentType paymentType, LocalDateTime transactionDate) {
        this.seller = seller;
        this.amount = amount;
        this.paymentType = paymentType;
        this.transactionDate = transactionDate;
    }

    public Transaction(Long id, Seller seller, BigDecimal amount, PaymentType paymentType, LocalDateTime transactionDate) {
        this.id = id;
        this.seller = seller;
        this.amount = amount;
        this.paymentType = paymentType;
        this.transactionDate = transactionDate;
    }
}
