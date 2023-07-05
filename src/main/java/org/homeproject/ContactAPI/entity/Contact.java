package org.homeproject.ContactAPI.entity;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "contacts")
@Data
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    private Long user_id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    public Contact(User user, String firstName, String lastName, String phoneNumber) {
        this.user_id = user.getId();
        this.firstName = firstName;
        this.lastName = lastName;
        if (phoneNumber.matches("\\+\\d{12}")) {
            this.phoneNumber = phoneNumber;
        }
    }


    public Contact() {
    }
}


