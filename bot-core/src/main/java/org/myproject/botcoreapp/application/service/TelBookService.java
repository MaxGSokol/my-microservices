package org.myproject.botcoreapp.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.myproject.botcoreapp.domain.entity.UserTelephoneBook;
import org.myproject.botcoreapp.domain.repository.UserTelBookRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TelBookService {
    private final UserTelBookRepository telBookRepository;

    @Transactional
    public void addUpdateContact(Long chatId, String name, String phoneNumber) {
        UserTelephoneBook userTelephoneBook = getSingleBook(chatId);
        userTelephoneBook.getTelBook().put(name, phoneNumber);
        addUpdateTelBook(userTelephoneBook);
    }

    public String getPhoneNumber(long chatId, String name) throws NullPointerException {
        if (getSingleBook(chatId).getTelBook().containsKey(name)) {
            return getSingleBook(chatId).getTelBook().get(name);
        } else {
            throw new NullPointerException();
        }
    }

    @Transactional
    public void removeContact(long chatId, String name) throws NullPointerException {
        UserTelephoneBook userTelephoneBook = getSingleBook(chatId);
        if (userTelephoneBook.getTelBook().containsKey(name)) {
            userTelephoneBook.getTelBook().remove(name);
        } else {
            throw new NullPointerException();
        }
        addUpdateTelBook(userTelephoneBook);
    }

    public Map<String, String> getAllContact(long chatId) {
        return getSingleBook(chatId).getTelBook();
    }

    private UserTelephoneBook getSingleBook(long chatId) {
        UserTelephoneBook userTelephoneBook = telBookRepository.findUserTelephoneBooksByChatId(chatId);
        if (userTelephoneBook == null) {
            userTelephoneBook = new UserTelephoneBook();
            userTelephoneBook.setChatId(chatId);
            userTelephoneBook.setTelBook(new HashMap<>());
        }
        return userTelephoneBook;
    }

    private void addUpdateTelBook(UserTelephoneBook userTelephoneBook) {
        telBookRepository.save(userTelephoneBook);
    }

}
