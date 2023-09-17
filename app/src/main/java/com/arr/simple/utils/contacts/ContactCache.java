package com.arr.simple.utils.contacts;

import com.arr.simple.model.Contact;
import java.util.HashMap;
import java.util.Map;

public class ContactCache {

    private static ContactCache instance;
    private Map<String, Contact> contactMap;

    private ContactCache() {
        contactMap = new HashMap<>();
    }

    public static synchronized ContactCache getInstance() {
        if (instance == null) {
            instance = new ContactCache();
        }
        return instance;
    }

    public void addContact(Contact contact) {
        contactMap.put(contact.getNumber(), contact);
    }

    public Contact getContact(String numero) {
        return contactMap.get(numero);
    }
}
