package ru.shift.zverev.crm_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class SellerRequest {

    @NotBlank(message = "seller name is mandatory")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @Size(max = 255, message = "contact info must be less than 255 characters")
    private String contactInfo;
}
