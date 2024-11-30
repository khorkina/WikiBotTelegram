package com.bot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            // Создаем объект TelegramBotsApi для регистрации бота
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            // Регистрируем нашего бота
            botsApi.registerBot(new WikiBot());

            System.out.println("Bot successfully started!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            System.out.println("Error starting the bot: " + e.getMessage());
        }
    }
}


