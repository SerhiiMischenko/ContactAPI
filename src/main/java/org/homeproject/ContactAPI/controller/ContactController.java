package org.homeproject.ContactAPI.controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.homeproject.ContactAPI.dto.ContactDTO;
import org.homeproject.ContactAPI.entity.Contact;
import org.homeproject.ContactAPI.error.ErrorResponse;
import org.homeproject.ContactAPI.service.ContactService;
import org.homeproject.ContactAPI.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

import org.homeproject.ContactAPI.entity.User;

@RestController
@RequestMapping("/contact")
@Api(tags = "Contacts Management")
public class ContactController {
    private final ContactService contactService;
    private final UserService userService;



    @Autowired
    public ContactController(ContactService contactService, UserService userService) {
        this.contactService = contactService;
        this.userService = userService;
    }
    @ApiOperation(value = "Create contact by request body", notes = "Returns a new creation contact")
    @Secured("ROLE_USER")
    @PostMapping()
    public ResponseEntity<?> createContact(@RequestBody Contact contact, Authentication authentication) {
        try {
            String currentUserName = authentication.getName();
            User currentUser = userService.getUserByUsername(currentUserName);
            if(authentication.isAuthenticated() && currentUser.getRole().equals("ROLE_USER")) {
                Contact newContact =
                        new Contact(currentUser, contact.getFirstName(), contact.getLastName(), contact.getPhoneNumber());
                return new ResponseEntity<>(contactService.createContact(newContact), HttpStatus.OK);
            }

        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.statusNotValid("Some row is empty", "/contact");

            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.statusNotAuthorized("You are no authorized", "/contact");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ApiOperation(value = "Get all user contacts", notes = "Returns contacts list")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping()
    public ResponseEntity<?> getContacts(Authentication authentication) {
        try {
            String currentUsername = authentication.getName();
            User currentUser = userService.getUserByUsername(currentUsername);
            if(authentication.isAuthenticated() && currentUser.getRole().equals("ROLE_ADMIN")) {
                ModelMapper modelMapper = new ModelMapper();
                List<Contact> allContacts = contactService.readAllContacts();
                List<ContactDTO> allContactsDTO = allContacts.stream()
                        .map(contact -> modelMapper.map(contact, ContactDTO.class))
                        .collect(Collectors.toList());
                return new ResponseEntity<>(allContactsDTO, HttpStatus.OK);
            }
            else if (authentication.isAuthenticated() && currentUser.getRole().equals("ROLE_USER")){
                List<Contact> userContacts = contactService.getContactListByUserId(currentUser.getId());

                ModelMapper modelMapper = new ModelMapper();
                List<ContactDTO> contactDTOList = userContacts.stream()
                        .map(contact -> modelMapper.map(contact, ContactDTO.class))
                        .collect(Collectors.toList());

                return new ResponseEntity<>(contactDTOList, HttpStatus.OK);
            }

        }catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.statusNotAuthorized("You are no authorized", "/contact");

            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }

    }

    @ApiOperation(value = "Get contact by ID", notes = "Returns a single contact based on ID")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/{id}")
    public ResponseEntity<?> getContactByID(@PathVariable("id") Long id, Authentication authentication) {
        try {
            String currentName = authentication.getName();
            User currentUser = userService.getUserByUsername(currentName);
            Contact findContact = contactService.readContactById(id);
            if(authentication.isAuthenticated() && findContact.getUser_id().equals(currentUser.getId()) ||
                    currentUser.getRole().equals("ROLE_ADMIN")){
                ModelMapper modelMapper = new ModelMapper();
                ContactDTO contactDTO = modelMapper.map(findContact, ContactDTO.class);
                return new ResponseEntity<>(contactDTO, HttpStatus.OK);
            }
            if(authentication.isAuthenticated() && findContact.getUser_id().equals(currentUser.getId()) ||
                    currentUser.getRole().equals("ROLE_USER")){
                return new ResponseEntity<>(findContact, HttpStatus.OK);
            }else {
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.statusNotAuthorized("You are no authorized", "/contact");

                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }
        }catch (RuntimeException e) {
        }
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.statusNotAuthorized("You are no authorized", "/contact");
        return new ResponseEntity<>(errorResponse, HttpStatus.OK);
    }

    @ApiOperation(value = "Update contact by ID", notes = "Returns this updated contact")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateContact(@PathVariable ("id") Long id,
                                           @RequestBody Contact contact, Authentication authentication) {
        String currentName = authentication.getName();
        User currentUser = userService.getUserByUsername(currentName);
        Contact oldContact = contactService.readContactById(id);
        if(currentUser.getId().equals(oldContact.getUser_id()) || currentUser.getRole().equals("ROLE_ADMIN")) {
            try{
                if (contact.getFirstName() != null) {
                    oldContact.setFirstName(contact.getFirstName());
                }
                if (contact.getLastName() != null) {
                    oldContact.setLastName(contact.getLastName());
                }
                if(contact.getPhoneNumber() != null){
                    oldContact.setPhoneNumber(contact.getPhoneNumber());
                }

                Contact updContact = contactService.updateContact(oldContact);
                return new ResponseEntity<>(updContact, HttpStatus.OK);
            }catch (RuntimeException e) {
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.statusNotFound(id, "Contact not found", "contact/");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
        }
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.statusNotFound(id, "Contact not found", "contact/");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Delete contact by ID", notes = "Returns response with Http status")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @DeleteMapping("/{id}")
    public ResponseEntity<ErrorResponse> deleteById(@PathVariable ("id") Long id,
                                                    Authentication authentication) {
        ErrorResponse errorResponse = new ErrorResponse();
        String currentName = authentication.getName();
        User currentUser = userService.getUserByUsername(currentName);
        Contact findContact = contactService.readContactById(id);
        if(currentUser.getId().equals(findContact.getUser_id()) || currentUser.getRole().equals("ROLE_ADMIN")){
            try {
                contactService.deleteById(id);
                errorResponse.statusOk(id, "Contact deleted", "contact/");

                return new ResponseEntity<>(errorResponse, HttpStatus.OK);
            }catch (RuntimeException e) {
                errorResponse.statusNotFound(id, "Contact not found", "contact/");

                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
        }
        errorResponse.statusNotFound(id, "Contact not found", "delete/");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
