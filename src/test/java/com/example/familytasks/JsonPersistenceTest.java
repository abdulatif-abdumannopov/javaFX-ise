package com.example.familytasks;

import com.example.familytasks.models.Role;
import com.example.familytasks.models.User;
import com.example.familytasks.services.DataService;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class JsonPersistenceTest {

    @Test
    void testSaveAndLoadConsistency() {
        DataService.getUsers().clear();
        User originalUser = new User(99, "Сохраняемый Юзер", "hash", Role.CHILD);
        DataService.getUsers().add(originalUser);

        DataService.saveData();

        DataService.getUsers().clear();
        DataService.loadData();

        User loadedUser = DataService.getUserById(99);
        assertNotNull(loadedUser);
        assertEquals("Сохраняемый Юзер", loadedUser.getName());
    }
}