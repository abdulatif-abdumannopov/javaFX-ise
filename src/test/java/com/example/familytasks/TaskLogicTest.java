package com.example.familytasks;

import com.example.familytasks.models.Role;
import com.example.familytasks.models.Task;
import com.example.familytasks.models.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskLogicTest {

    @Test
    void testTaskCompletionAndReward() {
        User child = new User(2, "Ребенок", "hash", Role.CHILD);
        Task task = new Task(1, "Помыть посуду", 50, child.getId());

        assertEquals(0, child.getBalance());

        task.setStatus("COMPLETED");
        child.addBalance(task.getReward());

        assertEquals(50, child.getBalance(), "Баланс должен увеличиться на сумму награды");
    }
}