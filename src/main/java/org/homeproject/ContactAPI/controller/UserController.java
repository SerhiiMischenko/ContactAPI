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
        return userService.createUser(user, authentication);
    }

    @ApiOperation(value = "Get all users", notes = "Returns users list")
    @Secured("ROLE_ADMIN")
    @GetMapping()
    public ResponseEntity<?> getUsers() {
        return userService.getAllUsers();
    }

    @ApiOperation(value = "Get user by ID", notes = "Returns a single user based on ID")
    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
       return userService.getUserById(id);
    }

//    @ApiOperation(value = "Update user by ID", notes = "Returns this updated user")
//    @Secured({"ROLE_ADMIN", "ROLE_USER"})
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user, Authentication authentication) {
//        User oldUser;
//        try {
//            oldUser = userService.getUserById(id);
//        } catch (RuntimeException ignore) {
//            errorResponse.statusNotFound(id, "User not found", "/user");
//            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//        }
//
//        User currentUser = userService.getUserByUsername(authentication.getName());
//
//        if (!oldUser.getId().equals(currentUser.getId())) {
//            errorResponse.statusNotAuthorized("You are not authorized", "/path");
//            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
//        }
//        if (user.getUsername() != null &&
//                oldUser.getUsername().equals(user.getUsername())) {
//            errorResponse.statusAlreadyCreated("This username already exist", "/user");
//            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
//        } else if (user.getUsername() != null) {
//            oldUser.setUsername(user.getUsername());
//        }
//        if (user.getPassword() != null) {
//            oldUser.setPassword(passwordEncoder.encode(user.getPassword()));
//        }
//        userService.updateUser(oldUser);
//
//        return new ResponseEntity<>(oldUser, HttpStatus.OK);
//    }
//
//    @ApiOperation(value = "Delete user by ID", notes = "Returns response with Http status")
//    @Secured({"ROLE_ADMIN", "ROLE_USER"})
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteById(@PathVariable Long id, Authentication authentication) {
//        try {
//            User user = userService.getUserById(id);
//            User authUser = userService.getUserByUsername(authentication.getName());
//            if (!user.getId().equals(authUser.getId())) {
//                errorResponse.statusNotAuthorized("You are not authorized", "/user");
//
//                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
//            }
//        } catch (Exception e) {
//            errorResponse.statusNotFound(id, "User not found", "/user");
//            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
//        }
//        userService.deleteUserById(id);
//        errorResponse.statusOk(id, "User deleted", "/user/");
//
//        return new ResponseEntity<>(errorResponse, HttpStatus.OK);
//    }
}
