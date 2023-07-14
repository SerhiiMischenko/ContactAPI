package org.homeproject.ContactAPI.entity;

import lombok.Data;
import org.homeproject.ContactAPI.error.InvalidPhoneNumberException;

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

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    public Contact(User user, String firstName, String lastName, String phoneNumber) {
        this.user_id = user.getId();
        this.firstName = firstName;
        this.lastName = lastName;
        if (!phoneNumber.matches("\\+\\d{12}")) {
            throw new InvalidPhoneNumberException("Invalid phone number " + phoneNumber);
        }

        this.phoneNumber = phoneNumber;
    }

    public Contact() {
    }
}


