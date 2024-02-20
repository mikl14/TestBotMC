package io.projectBot.TestBot.service;

import io.projectBot.TestBot.config.BotConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;
    String[] masterClassesNames = new String[]{"Подтверждаю","Назад"};
    MasterClassList masterClasses = new MasterClassList(MasterClassFactory.createMasterClasses(masterClassesNames, 10));

    String adminedChatId = "-4168573346";

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

                    SendMessage(ChatId, "Я Бот Учебно-методической комиссии");
                    try {
                        execute(createKeyboard(ChatId, new String[]{"О нас", "Получить консультацию", "Методички","Бланк"}));
                    } catch (TelegramApiException ex) {
                        throw new RuntimeException(ex);
                    }

                    break;
                case "Назад":
                    try {
                        execute(createKeyboard(ChatId, new String[]{"О нас", "Получить консультацию", "Методички"}));
                    } catch (TelegramApiException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                case "Получить консультацию":
                    SendMessage(ChatId, "Напишите предмет для консультации в формате : \"Предмет: название \"");
                    break;
                case "О нас":
                    SendMessage(ChatId,"УМК (Учебно-методическая Комиссия) Профкома студентов\n" +
                            "Вниманию студентов 1-2 курса!\n" +
                            "\n" +
                            "Если у Вас возникают проблемы с учебой:\n" +
                            "· если Вам необходимо объяснить сложный материал,\n" +
                            "· если вдруг не получается решить домашнее задание или рубежный контроль,\n" +
                            "· если нужна помощь в подготовке к рубежному контролю, модулю, защите домашнего задания или экзамену\n" +
                            "\n" +
                            "В рамках работы Учебной Методической Комиссии Профсоюза студентов МГТУ им. Н.Э. Баумана Ваши друзья готовы помочь в решении проблем.");
                    break;
                case "Методички":
                    SendMessage(ChatId, "Тут список ссылок на все что нам надо");
                    break;
                case  "Бланк":
                    try
                    {
                        ClassPathResource resource = new ClassPathResource("blank.pdf");
                        InputStream inputStream = resource.getInputStream();

                        InputFile ss = new InputFile();
                        ss.setMedia(inputStream,"blank.pdf");
                        execute(sendDocument(ChatId,"Ваш бланк",ss));
                    }
                    catch (Exception e)
                    {
                        SendMessage(ChatId, "Что-то пошло не так попробуйте позже");
                    }
                    break;
                default:
                    if(messageText.contains("Предмет:"))
                    {
                        SendMessage(ChatId, "Вы успешно записались на консультацию: " + "\nкак: " + update.getMessage().getFrom().getUserName());
                        SendMessage(Long.parseLong(adminedChatId), '@'+update.getMessage().getFrom().getUserName() + " Записался на консультацию ! \n\n"+messageText+'\n'+ "\nНапишите ему в ближайшее время!");
                    }
                    else
                    {
                        SendMessage(ChatId, "Хм... я не могу понять проверьте правильность написания");
                    }

            }

        } else if (update.hasCallbackQuery()) {
            String query = update.getCallbackQuery().getData();
            long ChatId = update.getCallbackQuery().getMessage().getChatId();

            switch (query) {
                case "Получить консультацию":
                        SendMessage(ChatId, "Напишите предмет для консультации в формате : \"Предмет: название \"");
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


        keyboardMarkup.setKeyboard(keyboard);
        SendMessage message = new SendMessage();
        message.setText("Выберите нужный пункт меню:");
        message.setChatId(String.valueOf(chatId));
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    private static SendDocument sendDocument(long ChatId, String caption, InputFile sendFile)
    {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(String.valueOf(ChatId));
        sendDocument.setCaption(caption);
        sendDocument.setDocument(sendFile);
        return  sendDocument;
    }

}


