package org.homeproject.ContactAPI.service;

import lombok.Data;
import org.homeproject.ContactAPI.dto.UserDTO;
import org.homeproject.ContactAPI.error.ErrorResponse;
import org.homeproject.ContactAPI.repository.UserRepository;
import org.homeproject.ContactAPI.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Data
public class UserService {
    private UserRepository userRepository;
    private final ErrorResponse errorResponse;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.errorResponse = new ErrorResponse();

    }

    public ResponseEntity<?> createUser(User user, Authentication authentication) {
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
        if (userRepository.findByUsername(user.getUsername()) != null &&
                userRepository.findByUsername(user.getUsername()).getUsername().equals(user.getUsername())) {
            errorResponse.statusAlreadyCreated("This account already created", "/user");
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = userRepository.save(user);
        return new ResponseEntity<>(createdUser, HttpStatus.OK);
    }

    public ResponseEntity<?> getAllUsers() {
        List<User> userList = userRepository.findAll();
        ModelMapper modelMapper = new ModelMapper();
        List<UserDTO> userDTOList = userList.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    public ResponseEntity<?> getUserById(Long id) {
        try {
            Optional<User> getUser = userRepository.findById(id);
            ModelMapper modelMapper = new ModelMapper();
            UserDTO userDTO = modelMapper.map(getUser, UserDTO.class);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.statusNotFound(id, "User not found", "/user/");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
