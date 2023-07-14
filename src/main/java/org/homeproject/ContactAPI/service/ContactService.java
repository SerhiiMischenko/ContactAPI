package org.homeproject.ContactAPI.service;

import lombok.Data;
import org.homeproject.ContactAPI.dto.ContactDTO;
import org.homeproject.ContactAPI.entity.User;
import org.homeproject.ContactAPI.error.ErrorResponse;
import org.homeproject.ContactAPI.error.InvalidPhoneNumberException;
import org.homeproject.ContactAPI.repository.ContactRepository;
import org.homeproject.ContactAPI.entity.Contact;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class ContactService {
    private ContactRepository contactRepository;
    private final ErrorResponse errorResponse;
    private final UserService userService;

    public ContactService(ContactRepository contactRepository, UserService userService) {
        this.contactRepository = contactRepository;
        this.userService = userService;
        this.errorResponse = new ErrorResponse();
    }

    public ResponseEntity<?> createContact(Contact contact, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            errorResponse.statusNotAuthorized("You are no authorized", "/contact");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }
        Contact newContact = new Contact();
        try {
            String currentUserName = authentication.getName();
            User currentUser = userService.getUserByUsername(currentUserName);
            if (authentication.isAuthenticated() && currentUser.getRole().equals("ROLE_USER")) {
                newContact =
                        new Contact(currentUser, contact.getFirstName(), contact.getLastName(), contact.getPhoneNumber());
                return new ResponseEntity<>(contactRepository.save(newContact), HttpStatus.OK);
            } else {
                errorResponse.statusNotAuthorized("You are no authorized", "/contact");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }

        } catch (InvalidPhoneNumberException e) {
            errorResponse.statusNotValid("Not valid phone number", "/contact");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            return new ResponseEntity<>(contactRepository.save(newContact), HttpStatus.OK);
        }
    }

    public ResponseEntity<?> readAllContacts(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            errorResponse.statusNotAuthorized("You are no authorized", "/contact");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }
        String currentUsername = authentication.getName();
        User currentUser = userService.getUserByUsername(currentUsername);
        if (authentication.isAuthenticated() && currentUser.getRole().equals("ROLE_ADMIN")) {
            ModelMapper modelMapper = new ModelMapper();
            List<Contact> allContacts = contactRepository.findAll();
            List<ContactDTO> allContactsDTO = allContacts.stream()
                    .map(contact -> modelMapper.map(contact, ContactDTO.class))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(allContactsDTO, HttpStatus.OK);
        } else {
            List<Contact> userContacts = getContactListByUserId(currentUser.getId());
            ModelMapper modelMapper = new ModelMapper();
            List<ContactDTO> contactDTOList = userContacts.stream()
                    .map(contact -> modelMapper.map(contact, ContactDTO.class))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(contactDTOList, HttpStatus.OK);
        }
    }

    public ResponseEntity<?> getContactById(Long id, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            errorResponse.statusNotAuthorized("You are no authorized", "/contact");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }
        String currentName = authentication.getName();
        User currentUser = userService.getUserByUsername(currentName);
        try {
            Contact findContact = readContactById(id);
            if (authentication.isAuthenticated() && findContact.getUser_id().equals(currentUser.getId()) ||
                    currentUser.getRole().equals("ROLE_ADMIN")) {
                ModelMapper modelMapper = new ModelMapper();
                ContactDTO contactDTO = modelMapper.map(findContact, ContactDTO.class);
                return new ResponseEntity<>(contactDTO, HttpStatus.OK);
            }
            if (authentication.isAuthenticated() && findContact.getUser_id().equals(currentUser.getId()) &&
                    currentUser.getRole().equals("ROLE_USER")) {
                return new ResponseEntity<>(findContact, HttpStatus.OK);
            } else {
                errorResponse.statusNotAuthorized("You are no authorized", "/contact");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            errorResponse.statusNotFound(id, "Contact not found", "contact/");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> updateContact(Long id, Contact contact, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            errorResponse.statusNotAuthorized("You are no authorized", "/contact");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }
        String currentName = authentication.getName();
        User currentUser = userService.getUserByUsername(currentName);
        try {
            Contact oldContact = readContactById(id);
            if (currentUser.getRole().equals("ROLE_ADMIN")) {
                if (contact.getFirstName() != null) {
                    oldContact.setFirstName(contact.getFirstName());
                }
                if (contact.getLastName() != null) {
                    oldContact.setLastName(contact.getLastName());
                }
                if (contact.getPhoneNumber() != null) {
                    oldContact.setPhoneNumber(contact.getPhoneNumber());
                }
                contactRepository.save(oldContact);
                return new ResponseEntity<>(oldContact, HttpStatus.OK);
            }
            if (currentUser.getId().equals(oldContact.getUser_id()) && currentUser.getRole().equals("ROLE_USER")) {
                if (contact.getFirstName() != null) {
                    oldContact.setFirstName(contact.getFirstName());
                }
                if (contact.getLastName() != null) {
                    oldContact.setLastName(contact.getLastName());
                }
                if (contact.getPhoneNumber() != null) {
                    oldContact.setPhoneNumber(contact.getPhoneNumber());
                }
                contactRepository.save(oldContact);
                return new ResponseEntity<>(oldContact, HttpStatus.OK);
            } else {
                errorResponse.statusNotAuthorized("You are no authorized", "/contact");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            errorResponse.statusNotFound(id, "Contact not found", "contact/");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<ErrorResponse> deleteById(Long id, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            errorResponse.statusNotAuthorized("You are no authorized", "/contact");
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }
        String currentName = authentication.getName();
        User currentUser = userService.getUserByUsername(currentName);
        try {
            Contact findContact = readContactById(id);
            if (currentUser.getRole().equals("ROLE_ADMIN")) {
                contactRepository.deleteById(id);
                errorResponse.statusOk(id, "Contact deleted", "contact/");

                return new ResponseEntity<>(errorResponse, HttpStatus.OK);
            }
            if (currentUser.getId().equals(findContact.getUser_id()) && currentUser.getRole().equals("ROLE_USER")) {
                contactRepository.deleteById(id);
                errorResponse.statusOk(id, "Contact deleted", "contact/");

                return new ResponseEntity<>(errorResponse, HttpStatus.OK);
            } else {
                errorResponse.statusNotAuthorized("You are no authorized", "/contact");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            errorResponse.statusNotFound(id, "Contact not found", "contact/");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    public Contact readContactById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    public List<Contact> getContactListByUserId(Long id) {
        List<Contact> contactRepositoryAll = contactRepository.findAll();

        return contactRepositoryAll.stream()
                .filter(contact -> contact.getUser_id().equals(id))
                .collect(Collectors.toList());
    }
}
