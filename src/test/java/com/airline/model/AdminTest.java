package com.airline.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    @Test
    void gettersAndSetters_shouldWorkCorrectly() {
        Admin admin = new Admin();

        // Başlangıç değerleri (primitive için 0/false, nesne için null)
        assertEquals(0, admin.getId());
        assertFalse(admin.isActive());
        assertNull(admin.getUsername());
        assertNull(admin.getPassword());

        // Set değerlerini ata
        admin.setId(123);
        admin.setUsername("deniz");
        admin.setPassword("s3cr3t");
        admin.setActive(true);

        // Getter’larla geri al ve doğrula
        assertEquals(123, admin.getId());
        assertEquals("deniz", admin.getUsername());
        assertEquals("s3cr3t", admin.getPassword());
        assertTrue(admin.isActive());
    }

    @Test
    void interfaceMethods_overrideCorrectly() {
        // ARSModel arayüzündeki metodların Admin sınıfında da çalıştığını test edelim
        ARSModel model = new Admin();
        model.setId(5);
        model.setActive(true);

        assertEquals(5, model.getId());
        assertTrue(model.isActive());
    }
}
