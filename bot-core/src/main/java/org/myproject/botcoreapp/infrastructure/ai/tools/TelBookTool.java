package org.myproject.botcoreapp.infrastructure.ai.tools;

import com.voximplant.apiclient.ClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myproject.botcoreapp.application.service.TelBookService;
import org.myproject.botcoreapp.infrastructure.processor.TextMessageProcessor;
import org.myproject.botcoreapp.infrastructure.utils.ChatParamsHolder;
import org.myproject.botcoreapp.infrastructure.utils.CoreScriptUtils;
import org.myproject.botcoreapp.infrastructure.voximplant.StartScenario;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelBookTool {
    private final TelBookService telBookService;
    private final TextMessageProcessor textMessageProcessor;

    @Tool(description = "Покажи мне мои контакты/телефонную книгу")
    public String showAllContacts() {
        Long chatId = ChatParamsHolder.CHAT_ID.get();
        Map<String, String> contacts = telBookService.getAllContact(chatId);
        if (!contacts.isEmpty()) {
            return getTextContacts(contacts);
        } else {
            return CoreScriptUtils.TEL_BOOK_EMPTY;
        }
    }

    @Tool(description = "Добавь/запиши/обнови мои контакты name number", returnDirect = true)
    public String updateContacts(
            @ToolParam(description = "Как имя (например 'Иван'), так и ФИ (например 'Иван Иванов')") String name,
            @ToolParam(description = "номер телефона в format +7XXXXXXXXXX") String number
    ) {
        Long chatId = ChatParamsHolder.CHAT_ID.get();
        if (!isValidTelNum(number)) {
            return CoreScriptUtils.WRONG_NUMBER_FORMAT;
        }
        telBookService.addUpdateContact(chatId, name, number);
        return CoreScriptUtils.CONTACT_UPDATED + name + " - " + number;
    }

    @Tool(description = "Удали из контактов name", returnDirect = true)
    public String deleteSingleContact(
            @ToolParam(description = "Как имя (например 'Иван'), так и ФИ (например 'Иван Иванов')") String name
    ) {
        Long chatId = ChatParamsHolder.CHAT_ID.get();
        try {
            telBookService.removeContact(chatId, name);
        } catch (NullPointerException e) {
            return CoreScriptUtils.CONTACT_NOT_EXIST;
        }
        return CoreScriptUtils.CONTACT_DELETED + name;
    }

    @Tool(description = "Покажи мне номер name", returnDirect = true)
    public String showSingleContact(
            @ToolParam(description = "Как имя (например 'Иван'), так и ФИ (например 'Иван Иванов')") String name
    ) {
        Long chatId = ChatParamsHolder.CHAT_ID.get();
        try {
            return telBookService.getPhoneNumber(chatId, name);
        } catch (NullPointerException e) {
            return CoreScriptUtils.CONTACT_NOT_EXIST;
        }
    }

    @Tool(description = "Позвони name и скажи text", returnDirect = true)
    public String callToContact(
            @ToolParam(description = "Как имя (например 'Иван'), так и ФИ (например 'Иван Иванов')") String name,
            String text
    ) {
        Long chatId = ChatParamsHolder.CHAT_ID.get();
        try {
            String telNumber = telBookService.getPhoneNumber(chatId, name);

            StartScenario.executeCall(telNumber, text);
        } catch (NullPointerException e) {
            return CoreScriptUtils.CONTACT_NOT_EXIST;

        } catch (ClientException e) {
            return CoreScriptUtils.SOMETHING_GO_WRONG;
        }
        return CoreScriptUtils.EXECUTE_CALL + name + " " + text;
    }

    private String getTextContacts(Map<String, String> contact) {
        return textMessageProcessor.telBookMapFormater(contact);
    }

    private boolean isValidTelNum(String telNum) {
        String phoneRegex = "^\\+7\\d{10}$";
        return telNum != null && telNum.matches(phoneRegex);
    }
}
