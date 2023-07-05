package org.homeproject.ContactAPI.service;

import lombok.Data;
import org.homeproject.ContactAPI.repository.ContactRepository;
import org.homeproject.ContactAPI.entity.Contact;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class ContactService {
    private ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }
    public Contact createContact(Contact contact) {
        return contactRepository.save(contact);
    }
    public List<Contact> readAllContacts(){
        return contactRepository.findAll();
    }
    public Contact updateContact(Contact contact) {
        return contactRepository.save(contact);
    }
    public void deleteById(Long id) {
        contactRepository.deleteById(id);
    }
    public Contact readContactById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    public List<Contact> getContactsByUserId(Long id) {
        List<Contact> contactRepositoryAll = contactRepository.findAll();

        List<Contact> filteredContacts = contactRepositoryAll.stream()
                .filter(contact -> contact.getUser_id().equals(id))
                .collect(Collectors.toList());

        return filteredContacts;
    }
}
