package springdemobot.springbot.service;

import lombok.SneakyThrows;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import springdemobot.springbot.config.BotConfig;
import springdemobot.springbot.function.TelegramFun;
import springdemobot.springbot.function.constants.Constants;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private long chatIduser = 955854034;
    private boolean isFirstButton = false;
    private boolean isAdmin = false;
    final BotConfig config;

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
        long chatId = update.getMessage().getChatId();
        int messageId = update.getMessage().getMessageId();
        long userId = update.getMessage().getFrom().getId();
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            System.out.println(messageText);
//            System.out.println("Chat Id  " + chatId);
//
//            String getGroupName = getGroupName(update.getMessage().getChat());
//            System.out.println(getGroupName);
            System.out.println(update.getMessage());

            if (!isAdmin(update.getMessage())) {
                checkForLinks(messageText, String.valueOf(chatId), messageId);
            }

        }else {
            String caption = update.getMessage().getCaption();
            System.out.println(update.getMessage().getCaption());
            if (!isAdmin(update.getMessage())) {
                checkForLinks(caption, String.valueOf(chatId), messageId);
            }
        }


//
//        if (update.hasCallbackQuery()) {
//            String callbackData = update.getCallbackQuery().getData();
//            long messageId = update.getCallbackQuery().getMessage().getMessageId();
//            long chatid = update.getCallbackQuery().getMessage().getChatId();
//
//            if (callbackData.equals("Yes")) {
//                String text = "You pressed Yes";
//                EditMessageText editMessageText = new EditMessageText();
//                editMessageText.setChatId(String.valueOf(chatid));
//                editMessageText.setText(text);
//                editMessageText.setMessageId((int) messageId);
//                try {
//                    execute(editMessageText);
//                } catch (TelegramApiException telegramApiException) {
//                }
//            }else if (callbackData.equals("No")){
//                String text = "You pressed No";
//                EditMessageText editMessageText = new EditMessageText();
//                editMessageText.setChatId(String.valueOf(chatid));
//                editMessageText.setText(text);
//                editMessageText.setMessageId((int) messageId);
//                try {
//                    execute(editMessageText);
//                } catch (TelegramApiException telegramApiException) {
//                }
//            }
//        }


    }

    private boolean isAdmin(Message message) {
        //curren user who send message
        User user = message.getFrom();

        //Chat Member
        GetChatMember getChatMember = new GetChatMember(String.valueOf(message.getChatId()), user.getId());
        try {
            ChatMember chatMember = execute(getChatMember);

            // Check if the user is an admin in the group
            if (chatMember != null && chatMember.getStatus().equals("creator") || chatMember.getStatus().equals("administrator")) {
                return true;
            } else {
                return false;
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void deleteMessage(String chatId, int messageId) {
        DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);
        try {
            execute(deleteMessage);
        } catch (TelegramApiException tae) {
            throw new RuntimeException(tae);
        }
    }

    public void checkForLinks(String text, String chatId, int messageId) {
        // Regular expression to match URLs
        String urlRegex = "(http(s)?://|www\\.)\\S+";

        // Create a pattern object
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);

        // Create a matcher object
        Matcher matcher = pattern.matcher(text);

        // Check if the text contains a link
        if (matcher.find() || text.contains("@")) {
            deleteMessage(chatId, messageId);
        }
    }

    @SneakyThrows
    private void sendImage(long chatId, String message) {
        if (message.equals("Tillo")) {
            KeyboardButton sendButton = TelegramFun.createButton("Send Photo");
            KeyboardRow row1 = TelegramFun.createRow();
            row1.add(sendButton);
            List<KeyboardRow> keyboardRows = TelegramFun.addKeyboards();
            keyboardRows.add(row1);
            ReplyKeyboardMarkup replyKeyboardMarkup = TelegramFun.setUpKeyBoardMarkup(keyboardRows);


            for (String imgurl : getImagesList()) {
                InputStream stream = new URL(imgurl).openStream();
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(String.valueOf(chatId));
                sendPhoto.setPhoto(new InputFile(stream, imgurl));
                sendPhoto.setReplyMarkup(replyKeyboardMarkup);
                try {
                    execute(sendPhoto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


    }


    private void register(String chatId, Update update) {
        SendMessage message = new SendMessage();
        message.setText("Register");
        message.setChatId(chatId);
        InlineKeyboardMarkup markupLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsline = new ArrayList<>();
        List<InlineKeyboardButton> rowline = new ArrayList<>();

        InlineKeyboardButton yesbutton = new InlineKeyboardButton();
        yesbutton.setText("Yes");
        yesbutton.setCallbackData("Yes");

        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData("No");

        rowline.add(yesbutton);
        rowline.add(noButton);

        rowsline.add(rowline);
        markupLine.setKeyboard(rowsline);
        message.setReplyMarkup(markupLine);

        try {
            execute(message);
        } catch (TelegramApiException telegramApiException) {
        }


    }


    private List<String> getImagesList() {
        List<String> imgs = new ArrayList<>();
        imgs.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT-6E9KG94NV_FMovfal6jE6k8byGLtTJ4b6w&usqp=CAU");
        imgs.add("https://res.klook.com/image/upload/q_85/c_fill,w_750/v1654586251/blog/wsnqunszlajd5ypjo29l.jpg");
        imgs.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQIgQve-wngO9tNY-R6al3XZ5pLqZpul5sNvoUJNAKvTGz13IQSDsrkCX1ErnNzNjNcygw&usqp=CAU");
        return imgs;
    }

    public boolean checkAdmin(String message) {
        if (message.contains(Constants.ADMIN)) {
            return true;
        }
        return false;
    }

    public static int calculateNumbers(String expression) {
        // Split the expression into operands and operator
        String[] tokens = expression.split("[\\+\\-\\*/]");

        // Extract the operands
        int operand1 = Integer.parseInt(tokens[0]);
        int operand2 = Integer.parseInt(tokens[1]);

        // Extract the operator
        char operator = expression.charAt(tokens[0].length());

        // Perform the calculation
        int result = 0;
        switch (operator) {
            case '+':
                result = operand1 + operand2;
                break;
            case '-':
                result = operand1 - operand2;
                break;
            case '*':
                result = operand1 * operand2;
                break;
            case '/':
                if (operand2 != 0) {
                    result = operand1 / operand2;
                } else {
                    System.out.println("Error: Division by zero");
                }
                break;
            default:
                System.out.println("Error: Invalid operator");
        }

        return result;
    }

    private void startCommandReceived(long chatId, String firstName) {
        String answer = "Hi, " + firstName;
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textTosend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textTosend);
        System.out.println("Chat Id " + chatId);
        try {
            execute(message);
        } catch (TelegramApiException telegramApiException) {

        }
    }


    private void wrongMessages(String message, long chatId) {
        SendMessage sendMessage = new SendMessage();
        if (message.contains("kot")) {
            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setText("Sokinish mumkin emas");
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException telegramApiException) {
        }
    }


    private String getGroupName(Chat chat) {
        String groupname;

        if (chat.isUserChat()) {
            groupname = "Private Chat";
        } else if (chat.isGroupChat()) {
            groupname = chat.getTitle();
        } else {
            groupname = "Uknown";
        }
        return groupname;
    }


}














/*
//            wrongMessages(messageText, chatId);
//            User user = update.getMessage().getFrom();
//            System.out.println(user.getFirstName());




//            switch (messageText) {
//                case "/start": startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
//            }
 */