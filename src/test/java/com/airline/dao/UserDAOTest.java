package com.airline.dao;

import com.airline.model.User;
import com.airline.util.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDAOTest {
    private UserDAO dao;
    private int baseUserId;

    @BeforeAll
    void init() {
        // H2 kullan, MySQL uyumlu modda
        DatabaseConnection.setUrl("jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=-1");
        DatabaseConnection.setCredentials("", "");
        dao = new UserDAO();
    }

    @BeforeEach
    void resetDatabase() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS users");
            stmt.execute(
                "CREATE TABLE users (" +
                  "id INT AUTO_INCREMENT PRIMARY KEY, " +
                  "username VARCHAR(50), " +
                  "password VARCHAR(100), " +
                  "gender VARCHAR(10), " +
                  "age INT, " +
                  "country VARCHAR(50), " +
                  "is_admin BOOLEAN, " +
                  "is_active BOOLEAN, " +
                  "default_seat_preference VARCHAR(10)" +
                ")"
            );
        }
        // Temel kullanıcı ekle
        User base = new User();
        base.setUsername("deniz");
        base.setPassword("pwd");
        base.setGender("Female");
        base.setAge(28);
        base.setCountry("Turkey");
        base.setAdmin(false);
        assertTrue(dao.createUser(base));
        baseUserId = dao.getUserByUsername("deniz").getId();
    }

    @Test
    void createUser_and_getUserByUsername_shouldWork() throws Exception {
        User fetched = dao.getUserByUsername("deniz");
        assertNotNull(fetched);
        assertEquals("deniz", fetched.getUsername());
        assertEquals("pwd", fetched.getPassword());
        assertEquals("Female", fetched.getGender());
        assertEquals(28, fetched.getAge());
        assertEquals("Turkey", fetched.getCountry());
        assertFalse(fetched.isAdmin());
        assertTrue(fetched.isActive());
    }

    @Test
    void updateUserPreference_shouldPersist() throws Exception {
        User u = dao.getUserByUsername("deniz");
        u.setDefaultSeatPreference("AISLE");
        assertTrue(dao.updateUserPreference(u));
        User updated = dao.getUserById(baseUserId);
        assertEquals("AISLE", updated.getDefaultSeatPreference());
    }

    @Test
    void updateUserStatus_shouldToggleActive() throws Exception {
        assertTrue(dao.updateUserStatus(baseUserId, false));
        User u = dao.getUserById(baseUserId);
        assertFalse(u.isActive());

        assertTrue(dao.updateUserStatus(baseUserId, true));
        assertTrue(dao.getUserById(baseUserId).isActive());
    }

    @Test
    void getAllUsers_shouldExcludeAdmins() throws Exception {
        // Admin kullanıcı ekle
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("adminpwd");
        admin.setGender("M");
        admin.setAge(35);
        admin.setCountry("TR");
        admin.setAdmin(true);
        assertTrue(dao.createUser(admin));

        List<User> list = dao.getAllUsers();
        assertEquals(1, list.size());
        assertEquals("deniz", list.get(0).getUsername());
    }

    @Test
    void getUserById_shouldReturnCorrect() throws Exception {
        User u = dao.getUserById(baseUserId);
        assertNotNull(u);
        assertEquals("deniz", u.getUsername());
    }

    @Test
    void updateUser_shouldPersistAllFields() throws Exception {
        User u = dao.getUserById(baseUserId);
        u.setUsername("deniz2");
        u.setPassword("pwd2");
        u.setGender("Other");
        u.setAge(30);
        u.setCountry("USA");
        u.setAdmin(true);
        u.setActive(false);

        assertTrue(dao.updateUser(u));
        User updated = dao.getUserById(baseUserId);
        assertEquals("deniz2", updated.getUsername());
        assertEquals("pwd2", updated.getPassword());
        assertEquals("Other", updated.getGender());
        assertEquals(30, updated.getAge());
        assertEquals("USA", updated.getCountry());
        assertTrue(updated.isAdmin());
        assertFalse(updated.isActive());
    }

    @AfterAll
    void tearDown() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE users");
        }
    }
}
