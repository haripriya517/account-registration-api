package com.alexa.account.model;

import com.alexa.account.util.YesNoBoolean;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter to store Boolean values as Y/N strings in database.
 * Automatically converts between Boolean (Java) and String (Database) on CRUD operations.
 */
@Converter(autoApply = false)
public class YesNoConverter implements AttributeConverter<Boolean, String> {

    /**
     * Convert Boolean to Y/N string for database storage.
     * Called when saving to database.
     */
    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return YesNoBoolean.toYesNo(attribute);
    }

    /**
     * Convert Y/N string from database to Boolean.
     * Called when reading from database.
     */
    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        return YesNoBoolean.toBoolean(dbData);
    }
}

