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
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Api(tags = "Users Management")
public class UserController {
    private final UserService userService;
    private final ErrorResponse errorResponse;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        this.errorResponse = new ErrorResponse();
    }

    @ApiOperation(value = "Create user by request body", notes = "Returns a new creation user")
    @PostMapping()
    public ResponseEntity<?> createUser(@RequestBody User user, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            errorResponse.statusAlreadyCreated(
                    "You cannot create an account while logged into another account", "/user");
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        if (user.getUsername() == null || user.getUsername().isEmpty() || user.getUsername().isBlank()) {
            errorResponse.statusNotValid("Username is empty", "/user");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        if (user.getPassword() == null || user.getPassword().isEmpty() || user.getPassword().isBlank()) {
            errorResponse.statusNotValid("Password is empty", "/user");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        if(userService.getUserByUsername(user.getUsername()) != null &&
                userService.getUserByUsername(user.getUsername()).getUsername().equals(user.getUsername())){
            errorResponse.statusAlreadyCreated("This account already created", "/user");
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.OK);
    }

    @ApiOperation(value = "Get all users", notes = "Returns users list")
    @Secured("ROLE_ADMIN")
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
    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User getUser = userService.getUserById(id);
            ModelMapper modelMapper = new ModelMapper();
            UserDTO userDTO = modelMapper.map(getUser, UserDTO.class);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.statusNotFound(id, "User not found", "/user/");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Update user by ID", notes = "Returns this updated user")
    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User oldUser = userService.getUserById(id);
            if (oldUser != null) {
                if (user.getUsername() != null && user.getPassword() != null) {
                    oldUser.setUsername(user.getUsername());
                    oldUser.setPassword(passwordEncoder.encode(user.getPassword()));
                }
                oldUser.setRole(user.getRole());
                User updateUser = userService.updateUser(oldUser);

                return new ResponseEntity<>(updateUser, HttpStatus.OK);
            }
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.statusNotFound(id, "User not found", "/user/");

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Delete user by ID", notes = "Returns response with Http status")
    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        ErrorResponse errorResponse = new ErrorResponse();
        try {
            userService.deleteUserById(id);
            errorResponse.statusOk(id, "User deleted", "/user/");

            return new ResponseEntity<>(errorResponse, HttpStatus.OK);
        } catch (RuntimeException e) {
            errorResponse.statusNotFound(id, "User not found", "/user/");

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }
}
