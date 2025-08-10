package org.myproject.botcoreapp.infrastructure.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

@Getter
@Setter
@UtilityClass
public final class ChatParamsHolder {
    public static final ThreadLocal<Long> CHAT_ID = new ThreadLocal<>();
}
