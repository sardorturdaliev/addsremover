package springdemobot.springbot.function;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/*
1.Create Button, add name
2.Create Row,add button to row
3.Add KeyBoard
4.Set up ReplyKeyboardMarkup
 */
public class TelegramFun {

    public static KeyboardButton createButton(String name) {
        //Create Button
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText(name);
        return keyboardButton;
    }

    public static  KeyboardRow createRow() {
        KeyboardRow keyboardRow = new KeyboardRow();
        return keyboardRow;
    }

    public static List<KeyboardRow> addKeyboards() {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        return keyboardRows;
    }

    public static ReplyKeyboardMarkup setUpKeyBoardMarkup(List<KeyboardRow> keyboardRowList) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        keyboardMarkup.setKeyboard(keyboardRowList);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }


}
