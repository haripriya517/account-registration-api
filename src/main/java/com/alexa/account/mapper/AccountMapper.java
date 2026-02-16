package com.alexa.account.mapper;

import com.alexa.account.dto.AddressDTO;
import com.alexa.account.dto.AccountResponseDTO;
import com.alexa.account.dto.IdDocumentResponseDTO;
import com.alexa.account.model.AccountRequest;
import com.alexa.account.model.Address;
import com.alexa.account.model.IdDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between AccountRequest, AddressDTO, and response DTOs.
 * Uses MapStruct for compile-time code generation.
 */
@Mapper(componentModel = "spring")
public interface AccountMapper {


    /**
     * Convert AddressDTO to Address entity.
     */
    Address addressDtoToAddress(AddressDTO addressDTO);

    /**
     * Convert Address entity to AddressDTO.
     */
    AddressDTO addressToAddressDto(Address address);

    /**
     * Convert IdDocument entity to IdDocumentResponseDTO.
     * Maps fileSize to documentSize.
     */
    @Mapping(source = "fileName", target = "documentName")
    @Mapping(source = "fileType", target = "documentType")
    @Mapping(source = "fileSize", target = "documentSize")
    IdDocumentResponseDTO idDocumentToResponseDTO(IdDocument idDocument);

    /**
     * Convert AccountRequest entity to AccountResponseDTO.
     * Nested mappings for Address and IdDocument.
     */
    @Mapping(source = "address", target = "address")
    @Mapping(source = "idDocument", target = "idDocument")
    AccountResponseDTO accountRequestToResponseDTO(AccountRequest accountRequest);
}

