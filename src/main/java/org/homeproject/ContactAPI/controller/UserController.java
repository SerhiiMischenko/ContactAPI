package org.homeproject.ContactAPI.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.homeproject.ContactAPI.dto.UserDTO;
import org.homeproject.ContactAPI.entity.User;
import org.homeproject.ContactAPI.error.ErrorResponse;
import org.homeproject.ContactAPI.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Api(tags = "Users Management")
public class UserController {
    private final UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
@Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "Create user by request body", notes = "Returns a new creation user")
    @PostMapping()
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.statusNotValid("Login or password is empty", "/user");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Get all users", notes = "Returns users list")
    @GetMapping()
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<User> userList = userService.getAllUsers();
        ModelMapper modelMapper = new ModelMapper();
        List<UserDTO> userDTOList = userList.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    @ApiOperation(value = "Get user by ID", notes = "Returns a single user based on ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
    try {
        User getUser = userService.getUserById(id);
        ModelMapper modelMapper = new ModelMapper();
        UserDTO userDTO = modelMapper.map(getUser, UserDTO.class);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }catch (RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.statusNotFound(id, "User not found", "/user/");
        return new ResponseEntity<>(errorResponse,HttpStatus.NOT_FOUND);
    }
    }

    @ApiOperation(value = "Update user by ID", notes = "Returns this updated user")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
    try {
        User oldUser = userService.getUserById(id);
        if(oldUser != null) {
            if(user.getUsername() != null && user.getPassword() != null) {
                oldUser.setUsername(user.getUsername());
                oldUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            oldUser.setRole(user.getRole());
            User updateUser = userService.updateUser(oldUser);

            return new ResponseEntity<>(updateUser, HttpStatus.OK);
        }
    }catch (RuntimeException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.statusNotFound(id, "User not found", "/user/");

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Delete user by ID", notes = "Returns response with Http status")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
    ErrorResponse errorResponse = new ErrorResponse();
    try {
        userService.deleteUserById(id);
        errorResponse.statusOk(id, "User deleted", "/user/");

        return new ResponseEntity<>(errorResponse, HttpStatus.OK);
    }catch (RuntimeException e) {
        errorResponse.statusNotFound(id, "User not found", "/user/");

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    }
}
