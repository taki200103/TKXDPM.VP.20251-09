package com.aims.entity.deliveryInfo;

import com.aims.exception.InvalidDeliveryInfoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeliveryInfoTest {

    @Test
    void constructorShouldPersistProvidedValues() {
        DeliveryInfo info = new DeliveryInfo(
                "123MainStreet",
                "Hanoi",
                "Alice",
                "alice@example.com",
                "0123456789",
                "Leave at gate"
        );

        assertEquals("123MainStreet", info.getDeliveryAddress());
        assertEquals("Hanoi", info.getCity());
        assertEquals("Alice", info.getRecipientName());
        assertEquals("alice@example.com", info.getEmail());
        assertEquals("0123456789", info.getPhoneNumber());
        assertEquals("Leave at gate", info.getInstructions());
    }

    @Test
    void setDeliveryAddressAcceptsValidValue() {
        DeliveryInfo info = new DeliveryInfo();
        assertDoesNotThrow(() -> info.setDeliveryAddress("45B/NguyenTrai"));
        assertEquals("45B/NguyenTrai", info.getDeliveryAddress());
    }

    @Test
    void setDeliveryAddressRejectsInvalidCharacters() {
        DeliveryInfo info = new DeliveryInfo();
        assertThrows(InvalidDeliveryInfoException.class, () -> info.setDeliveryAddress("12 Nguyen Trai"));
    }

    @Test
    void setRecipientNameAcceptsLettersOnly() {
        DeliveryInfo info = new DeliveryInfo();
        assertDoesNotThrow(() -> info.setRecipientName("Bob"));
        assertEquals("Bob", info.getRecipientName());
    }

    @Test
    void setRecipientNameRejectsNonLetters() {
        DeliveryInfo info = new DeliveryInfo();
        assertThrows(InvalidDeliveryInfoException.class, () -> info.setRecipientName("Bob123"));
    }

    @Test
    void setEmailValidatesFormat() {
        DeliveryInfo info = new DeliveryInfo();
        assertDoesNotThrow(() -> info.setEmail("user@mail.com"));
        assertEquals("user@mail.com", info.getEmail());
    }

    @Test
    void setEmailRejectsInvalidFormat() {
        DeliveryInfo info = new DeliveryInfo();
        assertThrows(InvalidDeliveryInfoException.class, () -> info.setEmail("user@@mail"));
    }

    @Test
    void setPhoneNumberAcceptsValidNumberWithSeparators() {
        DeliveryInfo info = new DeliveryInfo();
        assertDoesNotThrow(() -> info.setPhoneNumber("012-345-6789"));
        assertEquals("012-345-6789", info.getPhoneNumber());
    }

    @Test
    void setPhoneNumberRejectsMixedSeparators() {
        DeliveryInfo info = new DeliveryInfo();
        assertThrows(InvalidDeliveryInfoException.class, () -> info.setPhoneNumber("012-345.6789"));
    }
}
