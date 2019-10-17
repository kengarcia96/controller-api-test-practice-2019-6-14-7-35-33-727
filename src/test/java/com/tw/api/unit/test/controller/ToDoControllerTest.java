package com.tw.api.unit.test.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
@ActiveProfiles(profiles = "test")
public class ToDoControllerTest {

    @Autowired
    private TodoController todoController;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TodoRepository todoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_get_all_todo() throws Exception {
        //given
        List<Todo> todoList = new ArrayList<>();
        Todo todo = new Todo(1, "mytestTodo", false, 2);
        todoList.add(todo);

        when(todoRepository.getAll()).thenReturn(todoList);
        //when
        ResultActions result = mvc.perform(get("/todos"));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("mytestTodo")))
                .andExpect(jsonPath("$[0].completed", is(false)))
                .andExpect(jsonPath("$[0].order", is(2)));

    }

    @Test
    void should_get_todo_with_id_1() throws Exception {
        //given
        Todo todo1 = new Todo(1, "doTestCode", false, 1);

        when(todoRepository.findById(todo1.getId())).thenReturn(Optional.of(todo1));
        //when
        ResultActions result = mvc.perform(get("/todos/1"));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("doTestCode")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.order", is(1)));

    }

    @Test
    void should_add_a_todo() throws Exception {
        //given
        Todo todo1 = new Todo(1, "doTestCode", false, 1);

        ResultActions result = mvc.perform(post("/todos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(todo1)));

        //then
        result.andExpect(status().isCreated())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("doTestCode")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.order", is(1)));

    }

    @Test
    void should_delete_a_todo() throws Exception {
        //when
        Todo todo1 = new Todo(1, "doTestCode", false, 1);
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo1));
        ResultActions result = mvc.perform(delete("/todos/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(todo1)));

        //then
        result.andExpect(status().isOk()).andDo(print());

    }

    @Test
    void should_update_a_todo() throws Exception {
        //when
        Todo todo1 = new Todo(1, "doTestCode", false, 1);
        todoRepository.add(todo1);
        Todo todo2 = new Todo(1, "finishTestCode", false, 1);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo1));
        ResultActions result = mvc.perform(patch("/todos/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(todo2)));

        //then

        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("finishTestCode")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.order", is(1)));
    }


}
