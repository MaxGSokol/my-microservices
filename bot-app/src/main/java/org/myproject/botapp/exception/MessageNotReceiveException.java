package org.myproject.botapp.exception;

import org.myproject.botapp.infrastructure.telegram.utils.BotScriptUtil;

public class MessageNotReceiveException extends Exception {
    public MessageNotReceiveException() {
        super(BotScriptUtil.SOMETHING_WRONG);
    }
}
