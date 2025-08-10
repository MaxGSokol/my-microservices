package org.myproject.botapp.exception;

import org.myproject.botapp.infrastructure.telegram.utils.BotScriptUtil;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super(BotScriptUtil.SOMETHING_WRONG);
    }
}
