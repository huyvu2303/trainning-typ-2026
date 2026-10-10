package com.courseshop.courseshop.controller;

import com.courseshop.courseshop.repository.TodoRepository;
import org.springframework.web.bind.annotation.RestController;

import com.courseshop.courseshop.model.RestErrorDTO;
import com.courseshop.courseshop.model.ResultPaginationDTO;
import com.courseshop.courseshop.model.Todo;
import com.courseshop.courseshop.service.TodoService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class TodoController {
    private final TodoRepository todoRepository;
    private final TodoService todoService;

    public TodoController(TodoService todoService, TodoRepository todoRepository){
        this.todoService = todoService;
        this.todoRepository = todoRepository;
    }
    // 1. GET /todos/{id} - Lấy chi tiết
    @GetMapping("/todos/{id}")
    public ResponseEntity<?> getTodoById(@PathVariable Long id) {
        Todo todoData = this.todoService.getTodoById(id);
        if (todoData == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RestErrorDTO(404, "Not Found", "Không tìm thấy Todo với id = " + id));
        }
        return ResponseEntity.ok().body(todoData);
    }

    // 2. GET /todos?page=1&size=10 - Lấy danh sách có phân trang
    @GetMapping("/todos")
    public ResponseEntity<ResultPaginationDTO> getTodos(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        
        ResultPaginationDTO result = this.todoService.fetchAllTodosWithPagination(page, size);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/todos-all")
    // public String getTodo() {
    //     this.todoService.handleGetTodo();
    //     return "get todo";
    // }
    public ResponseEntity<List<Todo>> getTodo() {
        List<Todo> listtodo = this.todoService.handleGetTodo();
        return ResponseEntity.ok().body(listtodo);
    }



    // @GetMapping("/create-todo")
    // public String create() {
    //     Todo myTodo = new Todo("Huy Vu",true);
    //     Todo newTodo = this.todoService.handleCreateTodo(myTodo); // luu lai Service trong Controllers
    //     //dung this de goi lai ham trong file todoService
    //     return "create todo with id " + newTodo.getId();
    // }
    // 3. POST /todos - Tạo mới
    @PostMapping("/todos")
    public ResponseEntity<Todo> createTodo(@RequestBody Todo input) {
        Todo newTodo = this.todoService.handleCreateTodo(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTodo);
    }




    // @GetMapping("/update-todo")
    // public String updateTodo() {
    //     this.todoService.handleUpdateTodo();
    //     return "update a todo";
    // }
    // 4. PUT /todos/{id} - Cập nhật
    @PutMapping("/todos/{id}")
    public ResponseEntity<?> updateTodo(@PathVariable Long id, @RequestBody Todo input) {
        Todo updatedTodo = this.todoService.handleUpdateTodo(id, input);
        if (updatedTodo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RestErrorDTO(404, "Not Found", "Không tìm thấy Todo để cập nhật với id = " + id));
        }
        return ResponseEntity.ok().body(updatedTodo);
    }




    // @GetMapping("/delete-todo")
    // public String deleteTodo() {
    //     this.todoService.handleDeleteTodo();
    //     return "delete a todo";
    // }
    // 5. DELETE /todos/{id} - Xóa
    @DeleteMapping("/todos/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id) {
        boolean isDeleted = this.todoService.handleDeleteTodo(id);
        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RestErrorDTO(404, "Not Found", "Không tìm thấy Todo để xóa với id = " + id));
        }
        return ResponseEntity.ok().body("Xóa thành công todo có id = " + id);
    }
    



}
