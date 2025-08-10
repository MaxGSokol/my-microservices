package org.myproject.botapp.infrastructure.telegram.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class BotScriptUtil {
    public static final String GREETING = "Приветствую вас! \n";
    public static final String BOT_GREETING = "Вас приветствует телеграм бот \"Виртуальный ассистент\" !\n";
    public static final String WELCOME_NEW_USER = "Для начала работы с ботом пройдите регистрацию.\n";
    public static final String WELCOME_REGISTERED_USER = "Рады снова видеть вас!\n";
    public static final String END_SESSION = "Для завершения нажмите - ";
    public static final String TO_MAIN_MENU = "Для возврата в главное меню нажмите - ";
    public static final String LETS_START = "Далее просто отдаете команды текстовым сообщением.\n";
    public static final String REPEAT_INPUT = "Повторите ввод.\n";
    public static final String CHOOSE_ACTION = "Выберите действие.\n";
    public static final String MESSAGE_FROM_ADMIN = "Вы получили сообщение от администратора -\n";
    public static final String SEND_YOUR_MESSAGE = "Сейчас самое время отправить сообщение.\n";
    public static final String TEL_NUM_EXAMPLE = "Пример 7(XXX)XXXXXXX,\n или 7(XXX)XXX-XX-XX\n";
    public static final String FOLLOW_THE_INSTRUCTIONS = "Следуйте инструкциям.\n";
    public static final String REGISTRATION_IN_PROGRESS = "Данные отправлены в обработку.\n Ожидайте подтверждения.\n";
    public static final String USER_WAITING_FOR_REGISTRATION = "Данный пользователь ожидает регистрации.\n";
    public static final String REGISTRATION_SUCCESS = "Поздравляем! Регистрация прошла успешно.\n";
    public static final String REGISTRATION_DENIED = "К нашему сожалению вам отказано в регистрации.\n";
    public static final String NEW_USER_ADDED = "Добавлен новый Пользователь.\n";
    public static final String NEW_ADMIN_ADDED = "Добавлен новый Администратор.\n";
    public static final String NO_USER_AVAILABLE = "Нет доступных пользователей.\n";
    public static final String WRONG_ACTION = "Не корректное значение.\n";
    public static final String SOMETHING_WRONG = "Что то пошло не по плану. Попробуйте позже.\n";
    public static final String GOOD_BY = "Всего доброго =)\n";
    public static final String ERROR_SESSION_MESSAGE = "Вы получили это сообщение из за ошибки программы, \n" +
            "или из за неверной введённой команды.\n";
}
