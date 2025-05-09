package com.airline.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        User u = new User();

        // Varsayılan değerler
        assertEquals(0, u.getId());
        assertNull(u.getUsername());
        assertNull(u.getPassword());
        assertNull(u.getGender());
        assertEquals(0, u.getAge());
        assertNull(u.getCountry());
        assertFalse(u.isAdmin());
        assertFalse(u.isActive());
        assertNull(u.getDefaultSeatPreference());

        // Test verisi
        u.setId(77);
        u.setUsername("deniz");
        u.setPassword("pwd123");
        u.setGender("Female");
        u.setAge(30);
        u.setCountry("Turkey");
        u.setAdmin(true);
        u.setActive(true);
        u.setDefaultSeatPreference("AISLE");

        // Getter’larla kontrol
        assertEquals(77, u.getId());
        assertEquals("deniz", u.getUsername());
        assertEquals("pwd123", u.getPassword());
        assertEquals("Female", u.getGender());
        assertEquals(30, u.getAge());
        assertEquals("Turkey", u.getCountry());
        assertTrue(u.isAdmin());
        assertTrue(u.isActive());
        assertEquals("AISLE", u.getDefaultSeatPreference());
    }

    @Test
    void implementsARSModel_contract() {
        // ARSModel arayüzündeki metodların User’da da çalıştığını test edelim
        ARSModel m = new User();
        m.setId(9);
        m.setActive(false);

        assertEquals(9, m.getId());
        assertFalse(m.isActive());
    }
}
