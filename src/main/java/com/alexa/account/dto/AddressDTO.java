package com.alexa.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AddressDTO(
        @NotBlank(message = "Street name is mandatory")
        String streetName,

        @NotBlank(message = "House number is mandatory")
        @Pattern(regexp = "^[1-9][0-9]{0,4}([A-Za-z])?(-[A-Za-z0-9]+)?$", message = "House number must be a valid house number (e.g., 123, 45A, 7-1, 123-bis)")
        String houseNumber,

        @NotBlank(message = "Post code is mandatory")
        @Pattern(regexp = "^[0-9]{4}\\s[A-Za-z]{2}$", message = "Post code must be 4 digits followed by space and 2 alphabets (e.g., 1234 AB)")
        String postCode,

        @NotBlank(message = "City is mandatory")
        String city
) {
}

