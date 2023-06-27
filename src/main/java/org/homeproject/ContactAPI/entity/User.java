package org.homeproject.ContactAPI.entity;

import lombok.Data;
import org.springframework.stereotype.Component;
import javax.persistence.*;

@Data
@Entity
@Component
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String role;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = UserRole.USER.toString();
    }
}
