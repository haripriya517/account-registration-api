package com.alexa.account.dto;

import com.alexa.account.config.YesNoDeserializer;
import com.alexa.account.config.YesNoSerializer;
import com.alexa.account.model.AccountStatus;
import com.alexa.account.model.AccountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountResponseDTO(
        String requestId,
        String name,
        @JsonFormat(pattern = "dd-MM-yyyy")
        LocalDate dateOfBirth,
        IdDocumentResponseDTO idDocument,
        AddressDTO address,
        AccountType accountType,
        BigDecimal startingBalance,
        String email,
        BigDecimal monthlySalary,
        @JsonSerialize(using = YesNoSerializer.class)
        @JsonDeserialize(using = YesNoDeserializer.class)
        Boolean interestedInOtherProducts,
        AccountStatus status
) {
}
