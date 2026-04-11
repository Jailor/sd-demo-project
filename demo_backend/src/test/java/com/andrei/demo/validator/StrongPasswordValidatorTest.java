package com.andrei.demo.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class StrongPasswordValidatorTest {

    private StrongPasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StrongPasswordValidator();
    }

    @Test
    void isValid_withStrongPassword_returnsTrue() {
        assertTrue(validator.isValid("Secure1234!", null));
    }

    @Test
    void isValid_withNull_returnsFalse() {
        assertFalse(validator.isValid(null, null));
    }

    @Test
    void isValid_withShortPassword_returnsFalse() {
        assertFalse(validator.isValid("Ab1!", null));
    }

    @Test
    void isValid_withNoUppercase_returnsFalse() {
        assertFalse(validator.isValid("secure1234!", null));
    }

    @Test
    void isValid_withNoLowercase_returnsFalse() {
        assertFalse(validator.isValid("SECURE1234!", null));
    }

    @Test
    void isValid_withNoDigit_returnsFalse() {
        assertFalse(validator.isValid("SecurePass!", null));
    }

    @Test
    void isValid_withNoSpecialChar_returnsFalse() {
        assertFalse(validator.isValid("Secure1234", null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Abcdefg1!", "XY12345!z", "Hello123@"})
    void isValid_withVariousStrongPasswords_returnsTrue(String password) {
        assertTrue(validator.isValid(password, null));
    }

    @Test
    void isValid_withEmptyString_returnsFalse() {
        assertFalse(validator.isValid("", null));
    }

    @Test
    void isValid_withExactly8CharsAndAllCriteria_returnsTrue() {
        assertTrue(validator.isValid("Abcdef1!", null));
    }
}
