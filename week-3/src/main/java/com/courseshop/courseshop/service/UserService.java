package com.courseshop.courseshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.courseshop.courseshop.model.ResultPaginationDTO;
import com.courseshop.courseshop.model.Todo;
import com.courseshop.courseshop.model.User;
import com.courseshop.courseshop.repository.UserRepository;
import com.courseshop.courseshop.exception.EmailAlreadyExistsException;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. Create
    public User handleCreateUser(User user) {
        if (this.userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        return this.userRepository.save(user);
    }

    public List<User> handleGetAllUser(){
        return this.userRepository.findAll();
    }

    // 2. Get Single User by ID
    public User getUserById(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        return userOptional.orElse(null);
    }

    // 3. Update User
    public User handleUpdateUser(Long id, User inputUser) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            User currentUser = userOptional.get();
            currentUser.setName(inputUser.getName());
            currentUser.setEmail(inputUser.getEmail());
            currentUser.setDob(inputUser.getDob());
            currentUser.setCompleted(inputUser.isCompleted());
            return this.userRepository.save(currentUser);
        }
        return null;
    }

    // 4. Delete User
    public boolean handleDeleteUser(Long id) {
        if (this.userRepository.existsById(id)) {
            this.userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // 5. Get All Users with Pagination
    public ResultPaginationDTO fetchAllUsersWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> pageUser = this.userRepository.findAll(pageable);

        ResultPaginationDTO dto = new ResultPaginationDTO();
        dto.setData(pageUser.getContent());
        dto.setPage(page);
        dto.setSize(size);
        dto.setTotalPages(pageUser.getTotalPages());
        dto.setTotalElements(pageUser.getTotalElements());

        return dto;
    }
}
