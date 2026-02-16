package com.alexa.account.service;

import com.alexa.account.dto.FieldValidationRequest;
import com.alexa.account.dto.FieldValidationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FieldValidationService Tests")
class FieldValidationServiceTest {

    private FieldValidationService service;

    @BeforeEach
    void setUp() {
        service = new FieldValidationService();
    }

    // Name Validation Tests
    @Test
    @DisplayName("Should validate name successfully")
    void testValidateName_ValidName_ReturnsValid() {
        FieldValidationResponse response = service.validateName("Haripriya");
        assertTrue(response.valid());
        assertEquals("Valid", response.message());
    }

    @Test
    @DisplayName("Should reject null name")
    void testValidateName_Null_ReturnsFalse() {
        FieldValidationResponse response = service.validateName(null);
        assertFalse(response.valid());
    }

    @Test
    @DisplayName("Should reject blank name")
    void testValidateName_Blank_ReturnsFalse() {
        FieldValidationResponse response = service.validateName("   ");
        assertFalse(response.valid());
    }

    // Date of Birth Tests
    @Test
    @DisplayName("Should validate valid date of birth")
    void testValidateDateOfBirth_ValidDate_ReturnsValid() {
        FieldValidationResponse response = service.validateDateOfBirth("15-05-1990");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should reject future date of birth")
    void testValidateDateOfBirth_FutureDate_ReturnsFalse() {
        FieldValidationResponse response = service.validateDateOfBirth("15-05-2099");
        assertFalse(response.valid());
    }

    @Test
    @DisplayName("Should reject invalid date format")
    void testValidateDateOfBirth_InvalidFormat_ReturnsFalse() {
        FieldValidationResponse response = service.validateDateOfBirth("2024-05-15");
        assertFalse(response.valid());
    }

    // Address Tests
    @Test
    @DisplayName("Should validate street name")
    void testValidateStreetName_ValidName_ReturnsValid() {
        FieldValidationResponse response = service.validateStreetName("Main Street");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should reject blank street name")
    void testValidateStreetName_Blank_ReturnsFalse() {
        FieldValidationResponse response = service.validateStreetName("   ");
        assertFalse(response.valid());
    }

    @Test
    @DisplayName("Should validate valid house number")
    void testValidateHouseNumber_ValidNumber_ReturnsValid() {
        FieldValidationResponse response = service.validateHouseNumber("123");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should validate house number with letter")
    void testValidateHouseNumber_WithLetter_ReturnsValid() {
        FieldValidationResponse response = service.validateHouseNumber("123A");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should validate house number with dash")
    void testValidateHouseNumber_WithDash_ReturnsValid() {
        FieldValidationResponse response = service.validateHouseNumber("123-1");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should reject invalid house number starting with 0")
    void testValidateHouseNumber_InvalidNumber_ReturnsFalse() {
        FieldValidationResponse response = service.validateHouseNumber("0123");
        assertFalse(response.valid());
    }

    @Test
    @DisplayName("Should validate valid postcode")
    void testValidatePostCode_ValidCode_ReturnsValid() {
        FieldValidationResponse response = service.validatePostCode("1234 AB");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should reject postcode without space")
    void testValidatePostCode_NoSpace_ReturnsFalse() {
        FieldValidationResponse response = service.validatePostCode("1234AB");
        assertFalse(response.valid());
    }

    @Test
    @DisplayName("Should validate city")
    void testValidateCity_ValidCity_ReturnsValid() {
        FieldValidationResponse response = service.validateCity("Amsterdam");
        assertTrue(response.valid());
    }

    // Account Type Tests
    @Test
    @DisplayName("Should validate account type SAVINGS")
    void testValidateAccountType_Savings_ReturnsValid() {
        FieldValidationResponse response = service.validateAccountType("Savings");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should validate account type CURRENT")
    void testValidateAccountType_Current_ReturnsValid() {
        FieldValidationResponse response = service.validateAccountType("Current");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should validate account type INVESTMENT")
    void testValidateAccountType_Investment_ReturnsValid() {
        FieldValidationResponse response = service.validateAccountType("Investment");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should reject invalid account type")
    void testValidateAccountType_InvalidType_ReturnsFalse() {
        FieldValidationResponse response = service.validateAccountType("Unknown");
        assertFalse(response.valid());
    }

    // InterestedInOtherProducts Tests
    @Test
    @DisplayName("Should validate Y for interested")
    void testValidateInterestedInOtherProducts_Y_ReturnsValid() {
        FieldValidationResponse response = service.validateInterestedInOtherProducts("Y");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should validate N for not interested")
    void testValidateInterestedInOtherProducts_N_ReturnsValid() {
        FieldValidationResponse response = service.validateInterestedInOtherProducts("N");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should reject invalid Y/N value")
    void testValidateInterestedInOtherProducts_Invalid_ReturnsFalse() {
        FieldValidationResponse response = service.validateInterestedInOtherProducts("INVALID");
        assertFalse(response.valid());
    }

    @Test
    @DisplayName("Should accept null for optional field")
    void testValidateInterestedInOtherProducts_Null_ReturnsValid() {
        FieldValidationResponse response = service.validateInterestedInOtherProducts(null);
        assertTrue(response.valid());
    }

    // validateField Tests
    @Test
    @DisplayName("Should validate field using validateField method")
    void testValidateField_Name_ReturnsValid() {
        FieldValidationRequest request = new FieldValidationRequest("name", "John");
        FieldValidationResponse response = service.validateField(request);
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should reject unknown field")
    void testValidateField_UnknownField_ReturnsFalse() {
        FieldValidationRequest request = new FieldValidationRequest("unknown", "value");
        FieldValidationResponse response = service.validateField(request);
        assertFalse(response.valid());
        assertTrue(response.message().contains("Unknown field"));
    }

    // Additional Edge Cases
    @Test
    @DisplayName("Should validate field with case insensitive field name - dateOfBirth")
    void testValidateField_DateOfBirthCaseInsensitive_ReturnsValid() {
        FieldValidationRequest request = new FieldValidationRequest("dateofbirth", "15-05-1990");
        FieldValidationResponse response = service.validateField(request);
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should validate field with case insensitive field name - accountType")
    void testValidateField_AccountTypeCaseInsensitive_ReturnsValid() {
        FieldValidationRequest request = new FieldValidationRequest("accounttype", "Savings");
        FieldValidationResponse response = service.validateField(request);
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should validate field with case insensitive field name - interestedInOtherProducts")
    void testValidateField_InterestedInOtherProductsCaseInsensitive_ReturnsValid() {
        FieldValidationRequest request = new FieldValidationRequest("interestedinotherprodcts", "Y");
        FieldValidationResponse response = service.validateField(request);
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should reject null date of birth")
    void testValidateDateOfBirth_Null_ReturnsFalse() {
        FieldValidationResponse response = service.validateDateOfBirth(null);
        assertFalse(response.valid());
        assertTrue(response.message().contains("mandatory"));
    }

    @Test
    @DisplayName("Should reject blank date of birth")
    void testValidateDateOfBirth_Blank_ReturnsFalse() {
        FieldValidationResponse response = service.validateDateOfBirth("   ");
        assertFalse(response.valid());
        assertTrue(response.message().contains("mandatory"));
    }

    @Test
    @DisplayName("Should reject null street name")
    void testValidateStreetName_Null_ReturnsFalse() {
        FieldValidationResponse response = service.validateStreetName(null);
        assertFalse(response.valid());
        assertTrue(response.message().contains("mandatory"));
    }

    @Test
    @DisplayName("Should reject null house number")
    void testValidateHouseNumber_Null_ReturnsFalse() {
        FieldValidationResponse response = service.validateHouseNumber(null);
        assertFalse(response.valid());
        assertTrue(response.message().contains("mandatory"));
    }

    @Test
    @DisplayName("Should reject blank house number")
    void testValidateHouseNumber_Blank_ReturnsFalse() {
        FieldValidationResponse response = service.validateHouseNumber("   ");
        assertFalse(response.valid());
        assertTrue(response.message().contains("mandatory"));
    }

    @Test
    @DisplayName("Should reject null postcode")
    void testValidatePostCode_Null_ReturnsFalse() {
        FieldValidationResponse response = service.validatePostCode(null);
        assertFalse(response.valid());
        assertTrue(response.message().contains("mandatory"));
    }

    @Test
    @DisplayName("Should reject blank postcode")
    void testValidatePostCode_Blank_ReturnsFalse() {
        FieldValidationResponse response = service.validatePostCode("   ");
        assertFalse(response.valid());
        assertTrue(response.message().contains("mandatory"));
    }

    @Test
    @DisplayName("Should reject invalid postcode format - wrong letter count")
    void testValidatePostCode_WrongFormat_ReturnsFalse() {
        FieldValidationResponse response = service.validatePostCode("1234 A");
        assertFalse(response.valid());
        assertTrue(response.message().contains("4 digits"));
    }

    @Test
    @DisplayName("Should reject null city")
    void testValidateCity_Null_ReturnsFalse() {
        FieldValidationResponse response = service.validateCity(null);
        assertFalse(response.valid());
        assertTrue(response.message().contains("mandatory"));
    }

    @Test
    @DisplayName("Should reject blank city")
    void testValidateCity_Blank_ReturnsFalse() {
        FieldValidationResponse response = service.validateCity("   ");
        assertFalse(response.valid());
        assertTrue(response.message().contains("mandatory"));
    }

    @Test
    @DisplayName("Should reject null account type")
    void testValidateAccountType_Null_ReturnsFalse() {
        FieldValidationResponse response = service.validateAccountType(null);
        assertFalse(response.valid());
        assertTrue(response.message().contains("mandatory"));
    }

    @Test
    @DisplayName("Should reject blank account type")
    void testValidateAccountType_Blank_ReturnsFalse() {
        FieldValidationResponse response = service.validateAccountType("   ");
        assertFalse(response.valid());
        assertTrue(response.message().contains("mandatory"));
    }

    @Test
    @DisplayName("Should validate account type case insensitive - lowercase savings")
    void testValidateAccountType_LowercaseSavings_ReturnsValid() {
        FieldValidationResponse response = service.validateAccountType("savings");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should validate account type case insensitive - uppercase CURRENT")
    void testValidateAccountType_UppercaseCurrent_ReturnsValid() {
        FieldValidationResponse response = service.validateAccountType("CURRENT");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should validate account type case insensitive - mixed case InVeStMeNt")
    void testValidateAccountType_MixedCase_ReturnsValid() {
        FieldValidationResponse response = service.validateAccountType("InVeStMeNt");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should accept blank for optional interestedInOtherProducts field")
    void testValidateInterestedInOtherProducts_Blank_ReturnsValid() {
        FieldValidationResponse response = service.validateInterestedInOtherProducts("   ");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should validate Y in lowercase for interested")
    void testValidateInterestedInOtherProducts_LowercaseY_ReturnsValid() {
        FieldValidationResponse response = service.validateInterestedInOtherProducts("y");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should validate N in lowercase for not interested")
    void testValidateInterestedInOtherProducts_LowercaseN_ReturnsValid() {
        FieldValidationResponse response = service.validateInterestedInOtherProducts("n");
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should validate ID document - valid image")
    void testValidateIdDocument_ValidImage_ReturnsValid() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "passport.jpg",
            "image/jpeg",
            "content".getBytes()
        );
        FieldValidationResponse response = service.validateIdDocument(file);
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should validate ID document - valid PDF")
    void testValidateIdDocument_ValidPdf_ReturnsValid() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "document.pdf",
            "application/pdf",
            "content".getBytes()
        );
        FieldValidationResponse response = service.validateIdDocument(file);
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Should reject null ID document")
    void testValidateIdDocument_Null_ReturnsFalse() {
        FieldValidationResponse response = service.validateIdDocument(null);
        assertFalse(response.valid());
        assertTrue(response.message().contains("mandatory"));
    }

    @Test
    @DisplayName("Should reject empty ID document")
    void testValidateIdDocument_Empty_ReturnsFalse() {
        MultipartFile file = new MockMultipartFile("file", new byte[0]);
        FieldValidationResponse response = service.validateIdDocument(file);
        assertFalse(response.valid());
    }

    @Test
    @DisplayName("Should reject invalid ID document type")
    void testValidateIdDocument_InvalidType_ReturnsFalse() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "document.txt",
            "text/plain",
            "content".getBytes()
        );
        FieldValidationResponse response = service.validateIdDocument(file);
        assertFalse(response.valid());
        assertTrue(response.message().contains("image"));
    }

    @Test
    @DisplayName("Should reject ID document exceeding size limit")
    void testValidateIdDocument_ExceedsSize_ReturnsFalse() {
        // Create a file larger than 10MB
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MultipartFile file = new MockMultipartFile(
            "file",
            "large.jpg",
            "image/jpeg",
            largeContent
        );
        FieldValidationResponse response = service.validateIdDocument(file);
        assertFalse(response.valid());
        assertTrue(response.message().contains("10MB"));
    }

    @Test
    @DisplayName("Should reject ID document with null content type")
    void testValidateIdDocument_NullContentType_ReturnsFalse() {
        MultipartFile file = new MockMultipartFile(
            "file",
            "document.jpg",
            null,
            "content".getBytes()
        );
        FieldValidationResponse response = service.validateIdDocument(file);
        assertFalse(response.valid());
    }

    @Test
    @DisplayName("Should validate all field names via validateField method")
    void testValidateField_AllFieldNames_WorkCorrectly() {
        // Test streetName
        FieldValidationRequest streetRequest = new FieldValidationRequest("streetname", "Main Street");
        assertTrue(service.validateField(streetRequest).valid());

        // Test houseNumber
        FieldValidationRequest houseRequest = new FieldValidationRequest("housenumber", "123");
        assertTrue(service.validateField(houseRequest).valid());

        // Test postcode
        FieldValidationRequest postcodeRequest = new FieldValidationRequest("postcode", "1234 AB");
        assertTrue(service.validateField(postcodeRequest).valid());

        // Test city
        FieldValidationRequest cityRequest = new FieldValidationRequest("city", "Amsterdam");
        assertTrue(service.validateField(cityRequest).valid());
    }
}

