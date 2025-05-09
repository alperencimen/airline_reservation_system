package com.airline.dao;

import com.airline.model.Notification;
import com.airline.util.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NotificationDAOTest {

    private NotificationDAO dao;

    @BeforeAll
    void init() {
        // H2 in-memory DB modunu MySQL uyumlu veya default kullan
        DatabaseConnection.setUrl("jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=-1");
        DatabaseConnection.setCredentials("", "");
        dao = new NotificationDAO();
    }

    @BeforeEach
    void resetDatabase() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS notifications");
            stmt.execute(
                "CREATE TABLE notifications (" +
                  "id INT AUTO_INCREMENT PRIMARY KEY, " +
                  "user_id INT, " +
                  "message VARCHAR(255), " +
                  "created_at TIMESTAMP, " +
                  "is_read BOOLEAN" +
                ")"
            );
        }
    }

    @Test
    void createNotification_andRetrieve_shouldWork() throws Exception {
        // Yeni bildirim oluştur
        boolean created = dao.createNotification(42, "Test mesajı");
        assertTrue(created, "createNotification true dönmeli");

        // DB’den çek
        List<Notification> list = dao.getNotificationsByUserId(42);
        assertFalse(list.isEmpty(), "Bildirim listesi boş olmamalı");

        Notification n = list.get(0);
        assertEquals(42, n.getUserId());
        assertEquals("Test mesajı", n.getMessage());
        assertNotNull(n.getCreatedAt(), "createdAt ayarlı olmalı");
        assertFalse(n.isRead(), "Yeni bildirim okunmamış olmalı");
    }

    @Test
    void markAsRead_shouldUpdateFlag() throws Exception {
        // Bir bildirim oluştur
        dao.createNotification(99, "Oku beni");
        List<Notification> list = dao.getNotificationsByUserId(99);
        assertFalse(list.isEmpty(), "Bildirim listelenmeli");
        Notification n = list.get(0);
        int id = n.getId();

        // Okundu olarak işaretle
        boolean marked = dao.markAsRead(id);
        assertTrue(marked, "markAsRead true dönmeli");

        // Tekrar çek ve kontrol et
        Notification updated = dao.getNotificationsByUserId(99)
                                  .stream()
                                  .filter(x -> x.getId() == id)
                                  .findFirst()
                                  .orElseThrow();
        assertTrue(updated.isRead(), "isRead true olmalı");
    }

    @AfterAll
    void tearDown() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE notifications");
        }
    }
}
