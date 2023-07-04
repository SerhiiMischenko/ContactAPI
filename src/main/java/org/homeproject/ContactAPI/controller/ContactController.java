package org.homeproject.ContactAPI.controller;
import org.homeproject.ContactAPI.entity.Contact;
import org.homeproject.ContactAPI.error.ErrorResponse;
import org.homeproject.ContactAPI.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.Comparator;
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
    public ResponseEntity<?> createContact(@RequestBody Contact contact) {
        try {
            return new ResponseEntity<>(contactService.createContact(
                    new Contact(contact.getFirstName(), contact.getLastName(), contact.getPhoneNumber())), HttpStatus.OK);
        }catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.statusNotValid("Some row is empty", "/create");

            return new ResponseEntity<>(errorResponse ,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<List<Contact>> getContacts() {
        List<Contact> allContacts = contactService.readAllContacts();
        Collections.sort(allContacts, Comparator.comparing(Contact::getLastName));
        return new ResponseEntity<>(allContacts, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getContactByID(@PathVariable("id") Long id) {
        try {
            Contact findContact = contactService.readContactById(id);
            if (findContact != null) {
                return new ResponseEntity<>(findContact, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.statusNotFound(id, "Contact not found", "/get/" + id);

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("update/{id}")
    public ResponseEntity<?> updateContact(@PathVariable ("id") Long id, @RequestBody Contact contact) {
        try{
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
            }
        }catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.statusNotFound(id, "Contact not found", "update/" + id);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<ErrorResponse> deleteById(@PathVariable ("id") Long id) {
        ErrorResponse errorResponse = new ErrorResponse();

        try {
            contactService.deleteById(id);
            errorResponse.statusOk(id, "Contact deleted", "delete/");

            return new ResponseEntity<>(errorResponse, HttpStatus.OK);
        }catch (RuntimeException e) {
            errorResponse.statusNotFound(id, "Contact not found", "delete/");

            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }
}
