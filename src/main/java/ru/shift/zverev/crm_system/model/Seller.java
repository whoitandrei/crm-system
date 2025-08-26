package ru.shift.zverev.crm_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "sellers")
@Getter
@Setter
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "seller name is mandatory")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 255, message = "Seller contact info must be less than 255 characters")
    @Column(name = "contact_info", length = 255)
    private String contactInfo;

    @NotNull(message = "Registration date is mandatory")
    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;




    public Seller(){}

    public Seller(String name, LocalDateTime registrationDate) {
        this.name = name;
        this.registrationDate = registrationDate;
    }

    public Seller(Long id, String name, String contactInfo, LocalDateTime registrationDate) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        this.registrationDate = registrationDate;
    }

    public void addTransaction(Transaction transaction) {
        transaction.setSeller(this);
    }

    public void removeTransaction(Transaction transaction) {
        transaction.setSeller(null);
    }

}
