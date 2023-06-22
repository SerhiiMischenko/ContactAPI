package org.homeproject.ContactAPI.service;

import lombok.Data;
import org.homeproject.ContactAPI.data.UserRepository;
import org.homeproject.ContactAPI.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createContact(User user) {
        return userRepository.save(user);
    }
    public List<User> readAllUsers() {
        return userRepository.findAll();
    }
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
    public User readUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
