package com.example.familytasks;

import com.example.familytasks.models.Role;
import com.example.familytasks.models.User;
import com.example.familytasks.services.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class DataServiceTest {

    @BeforeEach
    void setUp() {
        DataService.getUsers().clear();
        String hashed = BCrypt.hashpw("admin", BCrypt.gensalt());
        DataService.getUsers().add(new User(1, "TestUser", hashed, Role.PARENT));
    }

    @Test
    void testAuthenticationSuccess() {
        User user = DataService.authenticate("TestUser", "admin");
        assertNotNull(user, "Пользователь должен быть авторизован");
        assertNotNull(user.getSessionToken(), "Токен должен быть сгенерирован");
    }

    @Test
    void testAuthenticationFail() {
        User user = DataService.authenticate("TestUser", "wrong_password");
        assertNull(user, "Авторизация должна провалиться при неверном пароле");
    }

    @Test
    void testTokenVerification() {
        User user = DataService.authenticate("TestUser", "admin");
        String token = user.getSessionToken();

        User verified = DataService.getUserByToken(user.getId(), token);
        assertEquals(user.getName(), verified.getName(), "Токен должен подтверждать личность");
    }
}