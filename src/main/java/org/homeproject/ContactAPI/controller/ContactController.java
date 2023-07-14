package org.homeproject.ContactAPI.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.homeproject.ContactAPI.entity.Contact;
import org.homeproject.ContactAPI.error.ErrorResponse;
import org.homeproject.ContactAPI.service.ContactService;
import org.homeproject.ContactAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contact")
@Api(tags = "Contacts Management")
public class ContactController {
    private final ContactService contactService;
    private final UserService userService;
    private final ErrorResponse errorResponse;


    @Autowired
    public ContactController(ContactService contactService, UserService userService) {
        this.contactService = contactService;
        this.userService = userService;
        this.errorResponse = new ErrorResponse();
    }

    @ApiOperation(value = "Create contact by request body", notes = "Returns a new creation contact")
    @Secured("ROLE_USER")
    @PostMapping()
    public ResponseEntity<?> createContact(@RequestBody Contact contact, Authentication authentication) {
        return contactService.createContact(contact, authentication);
    }

    @ApiOperation(value = "Get all user contacts", notes = "Returns contacts list")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping()
    public ResponseEntity<?> getContacts(Authentication authentication) {
        return contactService.readAllContacts(authentication);
    }

    @ApiOperation(value = "Get contact by ID", notes = "Returns a single contact based on ID")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/{id}")
    public ResponseEntity<?> getContactByID(@PathVariable("id") Long id, Authentication authentication) {
        return contactService.getContactById(id, authentication);
    }

    @ApiOperation(value = "Update contact by ID", notes = "Returns this updated contact")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateContact(@PathVariable("id") Long id,
                                           @RequestBody Contact contact, Authentication authentication) {
        return contactService.updateContact(id, contact, authentication);
    }

    @ApiOperation(value = "Delete contact by ID", notes = "Returns response with Http status")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @DeleteMapping("/{id}")
    public ResponseEntity<ErrorResponse> deleteById(@PathVariable("id") Long id,
                                                    Authentication authentication) {
        return contactService.deleteById(id, authentication);
    }
}
