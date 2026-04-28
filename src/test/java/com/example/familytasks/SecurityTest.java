package com.example.familytasks;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import static org.junit.jupiter.api.Assertions.*;

public class SecurityTest {

    @Test
    void testPasswordHashing() {
        String rawPassword = "myPassword123";

        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        assertNotEquals(rawPassword, hashedPassword);

        assertTrue(BCrypt.checkpw(rawPassword, hashedPassword), "Пароль должен совпадать с хешем");
        assertFalse(BCrypt.checkpw("wrongPass", hashedPassword), "Неверный пароль не должен проходить");
    }
}