package com.alexa.account.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Service for generating memorable and secure request IDs.
 *
 * Format: XXXX-YYYY
 * Example: AB2K-0225
 *
 * XXXX - 4 random readable characters (memorable, not guessable)
 * YYYY - DOB in MMYY format (recognizable by customer)
 *
 * Benefits:
 * - Memorable: Uses only readable characters (no 0/1/I/O confusion)
 * - Secure: Random characters prevent guessing
 * - Recognizable: Includes customer's DOB month-year
 * - Readable: Hyphenated format for easy reading
 */
@Service
public class RequestIdGeneratorService {

    private static final DateTimeFormatter DOB_MMYY_FORMATTER = DateTimeFormatter.ofPattern("MMyy");
    private static final String READABLE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Excluded I, O, 0, 1
    private static final Random RANDOM = new Random();

    /**
     * Generate a memorable request ID for customer reference.
     *
     * @param dateOfBirth customer's date of birth
     * @return generated request ID in format XXXX-YYYY
     */
    public String generateRequestId(LocalDate dateOfBirth) {
        // Generate 4 random readable characters
        String randomPart = generateRandomCharacters(4);

        // Get DOB in MMYY format
        String dobPart = (dateOfBirth != null)
            ? dateOfBirth.format(DOB_MMYY_FORMATTER)
            : "0000";

        // Combine in readable format: XXXX-YYYY
        return String.format("%s-%s", randomPart, dobPart);
    }

    /**
     * Generate random readable characters.
     * Uses only unambiguous characters: A-Z (except I, O), 2-9 (excludes 0, 1)
     *
     * @param length number of characters to generate
     * @return random string of readable characters
     */
    private String generateRandomCharacters(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(READABLE_CHARS.charAt(RANDOM.nextInt(READABLE_CHARS.length())));
        }
        return sb.toString();
    }
}

