package com.airline.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void defaultConstructor_andGettersSetters_shouldWork() {
        Notification n = new Notification();

        // Varsayılan değerler
        assertEquals(0, n.getId());
        assertEquals(0, n.getUserId());
        assertNull(n.getMessage());
        assertNull(n.getCreatedAt());
        assertFalse(n.isRead());

        // Değerleri set et
        LocalDateTime now = LocalDateTime.of(2025, 5, 8, 17, 0);
        n.setId(10);
        n.setUserId(99);
        n.setMessage("Test mesajı");
        n.setCreatedAt(now);
        n.setRead(true);

        // Getter’larla doğrula
        assertEquals(10, n.getId());
        assertEquals(99, n.getUserId());
        assertEquals("Test mesajı", n.getMessage());
        assertEquals(now, n.getCreatedAt());
        assertTrue(n.isRead());
    }

    @Test
    void parameterizedConstructor_shouldInitializeFields() {
        LocalDateTime created = LocalDateTime.of(2025, 6, 1, 9, 30);
        Notification n = new Notification(42, "Merhaba", created);

        // id dışındaki alanlar constructor ile set ediliyor
        assertEquals(0, n.getId());
        assertEquals(42, n.getUserId());
        assertEquals("Merhaba", n.getMessage());
        assertEquals(created, n.getCreatedAt());
        assertFalse(n.isRead(), "Yeni bildirim varsayılan olarak okunmamış olmalı");

        // Sonra id ve isRead setter ile ayarlanabilir
        n.setId(5);
        n.setRead(true);
        assertEquals(5, n.getId());
        assertTrue(n.isRead());
    }
}
