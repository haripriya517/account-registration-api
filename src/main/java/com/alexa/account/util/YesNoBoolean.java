package com.alexa.account.util;

import java.util.Set;

/**
 * Utility class for Y/N boolean conversion.
 * Handles conversion between Boolean and Y/N string format.
 */
public final class YesNoBoolean {

    private static final Set<String> YES_VALUES = Set.of("Y", "YES", "TRUE", "1", "ON");
    private static final Set<String> NO_VALUES = Set.of("N", "NO", "FALSE", "0", "OFF");

    private YesNoBoolean() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Convert Boolean to Y/N string format for database storage.
     * @param value Boolean value (can be null)
     * @return "Y" for true, "N" for false, null if input is null
     */
    public static String toYesNo(Boolean value) {
        if (value == null) {
            return null;
        }
        return value ? "Y" : "N";
    }

    /**
     * Convert Y/N/YES/NO/TRUE/FALSE string to Boolean.
     * @param value String value in Y/N/YES/NO/TRUE/FALSE format
     * @return true for Y/YES/TRUE, false for N/NO/FALSE, null if input is null
     */
    public static Boolean toBoolean(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().toUpperCase();

        if (YES_VALUES.contains(normalized)) {
            return true;
        }

        if (NO_VALUES.contains(normalized)) {
            return false;
        }

        throw new IllegalArgumentException(
            "Invalid Y/N value: '" + value + "'. Valid values: Y, N, YES, NO, TRUE, FALSE"
        );
    }

    /**
     * Check if a string is valid Y/N value.
     */
    public static boolean isValidYesNoValue(String value) {
        if (value == null) {
            return true;
        }

        String normalized = value.trim().toUpperCase();
        return YES_VALUES.contains(normalized) || NO_VALUES.contains(normalized);
    }
}

