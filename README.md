# Usage guide

Параметры: дата, время, место (без проблелов).

    /cghk 16.02 14:00 Клубе(Турнир-ОСВЧ)
    /quit 1.4 12:00 Дома-у-Олега

Создаётся неанонимный опрос с вариантами: "Буду", "Не буду", "Не знаю"

# Development Guide

## Local

Requires Heroku CLI, Java 17

- Create a test bot in Telegram with `@BotFather`
- Create .env file in root folder
- Put test bot token as BOT_TOKEN in .env

    BOT_TOKEN=111111111:abcabcabc

Run:

    ./gradlew stage
    heroku local

Communicate with Bot in Telegram.