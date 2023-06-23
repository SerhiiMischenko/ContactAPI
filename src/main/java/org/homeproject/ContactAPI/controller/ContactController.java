package org.homeproject.ContactAPI.controller;

import org.homeproject.ContactAPI.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/contact")
public class ContactController {
    private final ContactService contactService;
@Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }
@RequestMapping(value = "/get", method = {RequestMethod.GET})
    public ModelAndView getContact() {
    ModelAndView result = new ModelAndView("contact/contactsView");
    return result.addObject("allContacts", contactService.readAllContacts());
    }
}
