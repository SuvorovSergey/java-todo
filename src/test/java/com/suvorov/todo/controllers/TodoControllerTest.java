package com.suvorov.todo.controllers;

import com.suvorov.todo.models.Todo;
import com.suvorov.todo.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoRepository todoRepository;

    @Test
    public void testGetAllTodos() throws Exception {
        Todo todo = new Todo("Test Title", "Test Description");
        todo.setId(1L);

        when(todoRepository.findAll()).thenReturn(Collections.singletonList(todo));

        mockMvc.perform(get("/api/todos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].description").value("Test Description"));
    }

    @Test
    public void testCreateTodo() throws Exception {
        Todo todo = new Todo("New Task", "Task Description");
        todo.setId(1L);

        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        String todoJson = "{\"title\":\"New Task\",\"description\":\"Task Description\"}";

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(todoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("Task Description"));
    }

    @Test
    public void testDeleteTodo() throws Exception {
        Todo todo = new Todo("Delete Me", "To be deleted");
        todo.setId(1L);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        mockMvc.perform(delete("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteTodoNotFound() throws Exception {
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateTodoWithInvalidData() throws Exception {
        // Проверка пустых значений
        String emptyTodoJson = "{\"title\":\"\",\"description\":\"\"}";
        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyTodoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title must be between 3 and 100 characters"))
                .andExpect(jsonPath("$.description").value("Description must be between 3 and 255 characters"));

    
    }
    

    @Test
    public void testCreateTodoWithShortTitle() throws Exception {
        String invalidTodoJson = "{\"title\":\"ab\",\"description\":\"Valid description\"}";

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidTodoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title must be between 3 and 100 characters"));
    }

    @Test
    public void testCreateTodoWithLongDescription() throws Exception {
        String longDescription = "a".repeat(256); // 256 символов
        String invalidTodoJson = "{\"title\":\"Valid title\",\"description\":\"" + longDescription + "\"}";

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidTodoJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Description must be between 3 and 255 characters"));
    }
}
