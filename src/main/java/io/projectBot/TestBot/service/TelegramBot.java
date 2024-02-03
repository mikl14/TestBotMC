package io.projectBot.TestBot.service;

import io.projectBot.TestBot.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;
    String[] masterClassesNames = new String[]{"Прога", "Пайка", "Кринж", "База", "Скуфирование"};
    MasterClassList masterClasses = new MasterClassList(MasterClassFactory.createMasterClasses(masterClassesNames, 10));

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long ChatId = update.getMessage().getChatId();
            switch (messageText) {

                case "/OnlyAdminCheck":
                    SendMessage(ChatId, masterClasses.allUsers());
                    break;

                case "/start":
                case "/info":

                    SendMessage(ChatId, "Я Бот помощник форума НАЗВАНИЕ !, В течении всего дня форума я буду информировать вас о новых событиях на форуме, так же через меня вы можете записаться на мастер класс!");
                    try {
                        execute(createKeyboard(ChatId, new String[]{"Расписание форума", "Запись на мастер класс", "Организаторы"}));
                    } catch (TelegramApiException ex) {
                        throw new RuntimeException(ex);
                    }

                    break;
                case "Назад":
                    try {
                        execute(createKeyboard(ChatId, new String[]{"Расписание форума", "Запись на мастер класс", "Организаторы"}));
                    } catch (TelegramApiException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                case "Запись на мастер класс":
                    try {
                        execute(createKeyboard(ChatId, masterClassesNames));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "Расписание форума":
                    SendMessage(ChatId, "11:00\t✅ Выступление главного спикера\n" +
                            "11:45\t✅ Панельная дискуссия по технологическим трендам\n" +
                            "12:30\t✅ Мастер-класс по искусственному интеллекту\n" +
                            "13:15\t✅ Обеденный перерыв\n" +
                            "14:00\t✅ Презентация новой технологии\n" +
                            "14:45\t✅ Круглый стол по кибербезопасности\n" +
                            "15:30\t✅ Доклад о перспективах развития Интернета вещей\n" +
                            "16:15\t✅ Заключительное слово и награждение победителей конкурса инноваций");
                    break;
                case "Организаторы":
                    SendMessage(ChatId, "ВЦ фрязино я думаю...");
                    break;
                default:
                    MasterClass currentMaster = masterClasses.findByName(messageText);
                    if (currentMaster != null) {
                        switch (currentMaster.checkUser(update.getMessage().getFrom().getUserName())) {
                            case 1:
                                SendMessage(ChatId, "Вы успешно записались на мастер класс: " + currentMaster.name + "\nкак: " + update.getMessage().getFrom().getUserName()
                                        + "\nОсталось свободных записей: " + currentMaster.getFreeBooking() + "\n для отмены своей записи выберите запись на этот мастер класс повторно");
                                break;
                            case 0:
                                SendMessage(ChatId, "Ваша запись на мастер класс: " + currentMaster.name + "\nкак: " + update.getMessage().getFrom().getUserName()
                                        + "\nУспешно отменена!");
                                break;
                            case -1:
                                SendMessage(ChatId, "Все места на этот мастер класс уже забронированы, но вы можете выбрать другой!");
                                break;
                        }
                        XmlService.writeItemsToCategory();
                    } else {
                        SendMessage(ChatId, "Хм... я не знаю эту команду напишите /info чтобы начать работу со мной");
                    }
            }

        } else if (update.hasCallbackQuery()) {
            String query = update.getCallbackQuery().getData();
            long ChatId = update.getCallbackQuery().getMessage().getChatId();

            switch (query) {
                case "Запись на мастер класс":
                    try {
                        execute(createKeyboard(ChatId, masterClassesNames));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;

                default:


            }

        }


    }


    private void SendMessage(long chatId, String Text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(Text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.out.println(e);
        }
    }


    private static SendMessage createKeyboard(long chatId, String[] buttonLabels) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        for (String label : buttonLabels) {
            KeyboardRow row = new KeyboardRow();
            row.add(label);
            keyboard.add(row);
        }

        KeyboardRow row = new KeyboardRow();
        row.add("Назад");
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        SendMessage message = new SendMessage();
        message.setText("Выберите нужный пункт меню:");
        message.setChatId(String.valueOf(chatId));
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }


    public void sendReminder() {
        LocalTime now = LocalTime.now();
        if (now.getHour() == 17 && now.getMinute() == 0) {
            SendMessage message = new SendMessage();
            message.setChatId("YOUR_CHAT_ID");
            message.setText("Напоминание !");
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }


}
