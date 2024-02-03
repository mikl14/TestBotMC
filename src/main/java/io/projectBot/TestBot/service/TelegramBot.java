package io.projectBot.TestBot.service;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import io.projectBot.TestBot.config.BotConfig;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;
    String[] masterClassesNames = new String[]{"Прога", "Пайка", "Кринж", "База", "Скуфирование"};
    MasterClassList masterClasses = new MasterClassList(MasterClassFactory.createMasterClasses(masterClassesNames, 10));

    Notification[] notifications = new Notification[]{
            new Notification(LocalTime.of(20,0,0),"Сейчас 8 вечера!"),
            new Notification(LocalTime.of(19,0,0),"Сейчас 7 вечера!"),
            new Notification(LocalTime.of(18,0,0),"Сейчас 6 вечера!"),
            new Notification(LocalTime.of(17,16,0),"Сейчас 5 вечера!"),
            new Notification(LocalTime.of(16,40,0),"Сейчас 4.5 вечера!")
    };

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
            ChatsDatas.writeChatId(update.hasMessage() ? update.getMessage().getChat().getId() : update.getCallbackQuery().getMessage().getChat().getId());
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
        System.out.println("Chat! " + String.valueOf(chatId));
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



    public void startSendingMessages() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        LocalTime specifiedTime = LocalTime.of(12, 0); // Время для отправки сообщения

        executor.scheduleAtFixedRate(() -> {
            LocalTime currentTime = LocalTime.now();

            List<Long> ChatIds = ChatsDatas.getChatIds();

            System.out.println(ChatIds.toString());
                for (Notification notification:notifications)
                {
                    if (currentTime.getHour() == notification.getEventTime().getHour() && currentTime.getMinute() == notification.getEventTime().getMinute()) {
                        for(Long chatId:ChatIds) {
                           SendMessage(chatId, notification.getMessage());
                        }
                    }
                }
                System.out.println("not time");

        }, 0, 20, TimeUnit.SECONDS); // проверять раз в 30 сек
    }

}


