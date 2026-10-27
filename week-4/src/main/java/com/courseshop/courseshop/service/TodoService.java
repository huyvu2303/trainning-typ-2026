package com.courseshop.courseshop.service;

import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.SecondaryRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.courseshop.courseshop.model.ResultPaginationDTO;
import com.courseshop.courseshop.model.Todo;
import com.courseshop.courseshop.repository.TodoRepository;

@Service 
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository){
        this.todoRepository = todoRepository;
    }

    // 1. Create
    public Todo handleCreateTodo(Todo todo) {
        return this.todoRepository.save(todo);
    }
    
    // public void handleGetTodo(){
    //     // List <Todo> todos = this.todoRepository.findAll();
    //     // todos.forEach(System.out::println);
    //     Optional <Todo> todoOptional = this.todoRepository.findByname("Huy Vu1");
    //     if(todoOptional.isPresent()){
    //         System.out.println(todoOptional.get().toString());
    //     }
    // }

    public List<Todo> handleGetTodo(){
        return this.todoRepository.findAll();
    }
    // 2. Get Single by ID
    public Todo getTodoById(Long id){
        Optional <Todo> todoOptinal = this.todoRepository.findById(id);
        return todoOptinal.isPresent() ? todoOptinal.get() : null;
    }
    

    // public void handleUpdateTodo(){
    //     Optional <Todo> todoOptional = this.todoRepository.findById(1L);
    //     if(todoOptional.isPresent()){
    //         Todo currentTodo = todoOptional.get();
    //         currentTodo.setCompleted(false);
    //         currentTodo.setName("Vu Huy dep trai qua di mat");
            
    //         this.todoRepository.save(currentTodo);
    //     }
    // }

    // 3. Update
    public Todo handleUpdateTodo(Long id, Todo inputTodo) {
        Optional<Todo> todoOptional = this.todoRepository.findById(id);
        if (todoOptional.isPresent()) {
            Todo currentTodo = todoOptional.get();
            currentTodo.setCompleted(inputTodo.isCompleted());
            currentTodo.setName(inputTodo.getName());
            return this.todoRepository.save(currentTodo);
        }
        return null;
    }
   
    // 4. Delete
    public boolean handleDeleteTodo(Long id) {
        if (this.todoRepository.existsById(id)) {
            this.todoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 5. Get All with Pagination
    public ResultPaginationDTO fetchAllTodosWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Todo> pageTodo = this.todoRepository.findAll(pageable);

        ResultPaginationDTO dto = new ResultPaginationDTO();
        dto.setData(pageTodo.getContent());
        dto.setPage(page);
        dto.setSize(size);
        dto.setTotalPages(pageTodo.getTotalPages());
        dto.setTotalElements(pageTodo.getTotalElements());

        return dto;
    }
}
