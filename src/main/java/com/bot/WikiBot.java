package com.bot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class WikiBot extends TelegramLongPollingBot {

    private List<String> sresults = new ArrayList<>();
    private List<String> links = new ArrayList<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();

            if (text.equals("/start")) {
                sendTextMessage(message.getChatId(), "Support the Dev: www.github.com/DeKabilan");
            } else {
                handleSearchQuery(message);
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void handleSearchQuery(Message message) {
        sresults.clear();
        links.clear();
        searchWikipedia(message.getText());
        sendInlineKeyboard(message.getChatId(), message.getText());
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        int index = Integer.parseInt(callbackQuery.getData());
        String result = getResultFromWikipedia(links.get(index));
        sendTextMessage(callbackQuery.getMessage().getChatId(), "-----" + sresults.get(index) + "-----\n" + result);
    }

    private void searchWikipedia(String query) {
        String base = "https://en.wikipedia.org";
        String searchUrl = "https://en.wikipedia.org/w/index.php?search=" + query + "&title=Special:Search&profile=advanced&fulltext=1&ns0=1";

        try {
            Document doc = Jsoup.connect(searchUrl).get();
            for (int i = 0; i < 4; i++) {
                Element link = doc.select("a[data-serp-pos='" + i + "']").first();
                if (link != null) {
                    sresults.add(link.text());
                    links.add(base + link.attr("href"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getResultFromWikipedia(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements paragraphs = doc.select("p");
            if (paragraphs.size() > 2) {
                return paragraphs.get(1).text() + "\n" + paragraphs.get(2).text();
            } else if (paragraphs.size() > 1) {
                return paragraphs.get(1).text();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No result found.";
    }

    private void sendInlineKeyboard(Long chatId, String query) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(query);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for (int i = 0; i < sresults.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(sresults.get(i));
            button.setCallbackData(String.valueOf(i));
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            buttons.add(row);
        }

        markup.setKeyboard(buttons);
        message.setReplyMarkup(markup);


        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendTextMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "YourBotUsername"; // Укажите имя бота
    }

    @Override
    public String getBotToken() {
        return "YOUR_BOT_TOKEN"; // Укажите токен бота
    }
}



