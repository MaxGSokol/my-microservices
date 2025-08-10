package org.myproject.botapp.exception;

import org.myproject.botapp.infrastructure.telegram.utils.BotScriptUtil;

public class WrongInputException extends RuntimeException {
    public WrongInputException() {
        super(BotScriptUtil.WRONG_ACTION);
    }
}
