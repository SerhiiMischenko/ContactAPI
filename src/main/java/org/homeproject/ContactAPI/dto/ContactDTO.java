package org.homeproject.ContactAPI.dto;

import lombok.Data;
import org.homeproject.ContactAPI.entity.User;

@Data
public class ContactDTO {
    private Long id;
    private Long user_id;
    private String firstName;
    private String lastName;
    private String phoneNumber;

}
