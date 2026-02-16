package com.alexa.account.dto;

import com.alexa.account.config.YesNoDeserializer;
import com.alexa.account.config.YesNoSerializer;
import com.alexa.account.model.AccountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DraftRequestDTO(
        @NotBlank(message = "Name is mandatory")
        String name,

        @NotNull(message = "Date of birth is mandatory")
        @Past(message = "Date of birth must be in the past")
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate dateOfBirth,

        @NotNull(message = "Address is mandatory")
        @Valid
        AddressDTO address,

        AccountType accountType,

        @DecimalMin(value = "0.0", inclusive = true, message = "Starting balance must be greater than or equal to 0")
        BigDecimal startingBalance,

        @Email(message = "Email must be valid")
        String email,

        @DecimalMin(value = "0.0", inclusive = true, message = "Monthly salary must be greater than or equal to 0")
        BigDecimal monthlySalary,

        @JsonSerialize(using = YesNoSerializer.class)
        @JsonDeserialize(using = YesNoDeserializer.class)
        Boolean interestedInOtherProducts
) {
}
