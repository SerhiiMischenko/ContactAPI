package org.homeproject.ContactAPI.controller;
import org.homeproject.ContactAPI.entity.Contact;
import org.homeproject.ContactAPI.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contact")
public class ContactController {
    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/create")
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        return new ResponseEntity<>(contactService.createContact(
                new Contact(contact.getFirstName(), contact.getLastName(), contact.getPhoneNumber())), HttpStatus.CREATED);
    }

    @GetMapping("/get")
    public ResponseEntity<List<Contact>> getContacts() {
        List<Contact> allContacts = contactService.readAllContacts();
        return new ResponseEntity<>(allContacts, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Contact> getContactByID(@PathVariable("id") Long id) {
        Contact findContact = contactService.readContactById(id);
        if(findContact != null) {
            return new ResponseEntity<>(findContact, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("update/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable ("id") Long id, @RequestBody Contact contact) {
        Contact oldContact = contactService.readContactById(id);
        if(oldContact != null) {
            if(contact.getFirstName() != null){
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
        }else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("delete/{id}")
    public void deleteById(@PathVariable ("id") Long id) {
        contactService.deleteById(id);
    }
}
