package org.myproject.botapp.infrastructure.telegram.session.state;


public enum AdminSessionState {
    INIT,
    REGISTRATION,
    CHOOSE_FATE,
    SEND_MESSAGES_TO_ADMINS,
    SEND_MESSAGE_TO_USERS
}
