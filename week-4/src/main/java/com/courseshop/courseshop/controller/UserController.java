package com.courseshop.courseshop.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.courseshop.courseshop.model.RestErrorDTO;
import com.courseshop.courseshop.model.ResultPaginationDTO;
import com.courseshop.courseshop.model.Todo;
import com.courseshop.courseshop.model.User;
import com.courseshop.courseshop.exception.EmailAlreadyExistsException;
import com.courseshop.courseshop.service.UserService;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<String> handleEmailAlreadyExists(EmailAlreadyExistsException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    // 1. GET /users/{id} - Lấy User theo ID
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User userData = this.userService.getUserById(id);
        if (userData == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RestErrorDTO(404, "Not Found", "Không tìm thấy User với id = " + id));
        }
        return ResponseEntity.ok().body(userData);
    }

    // 2. GET /users?page=1&size=10 - Lấy danh sách User có phân trang
    @GetMapping("/users")
    public ResponseEntity<ResultPaginationDTO> getUsers(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        
        ResultPaginationDTO result = this.userService.fetchAllUsersWithPagination(page, size);
        return ResponseEntity.ok().body(result);
    }

    // 3. POST /users - Tạo User mới
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User input) {
        User newUser = this.userService.handleCreateUser(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    // 4. PUT /users/{id} - Cập nhật thông tin User
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User input) {
        User updatedUser = this.userService.handleUpdateUser(id, input);
        if (updatedUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RestErrorDTO(404, "Not Found", "Không tìm thấy User để cập nhật với id = " + id));
        }
        return ResponseEntity.ok().body(updatedUser);
    }

    // 5. DELETE /users/{id} - Xóa User
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        boolean isDeleted = this.userService.handleDeleteUser(id);
        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new RestErrorDTO(404, "Not Found", "Không tìm thấy User để xóa với id = " + id));
        }
        return ResponseEntity.ok().body("Xóa thành công user có id = " + id);
    }

    @GetMapping("/users-all")
    public ResponseEntity<List<User>> getAllUser() {
        List<User> listUser = this.userService.handleGetAllUser();
        return ResponseEntity.ok().body(listUser);
    }

}
