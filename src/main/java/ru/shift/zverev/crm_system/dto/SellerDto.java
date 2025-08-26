package ru.shift.zverev.crm_system.dto;

import lombok.Data;
import lombok.Setter;
import ru.shift.zverev.crm_system.model.Seller;

import java.time.LocalDateTime;


@Data
@Setter
public class SellerDto {
    private Long id;
    private String name;
    private String contactInfo;
    private LocalDateTime registrationDate;

    public SellerDto() {}

    public SellerDto(Long id, String name, String contactInfo, LocalDateTime registrationDate) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        this.registrationDate = registrationDate;
    }


}
