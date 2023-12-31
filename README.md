# Usage guide

- Добавьте бота в телеграм-группу.
- Пишите команды, начинающиеся с `/cghk` или `/quiz`

Параметры: дата, время, место, дополнительная информация. Параметры разделены пробелами.

    /chgk 16.02 14:00 Клубе ТурнирОСВЧ
    /quit 1.4 12:00 Дома у Олега

Создаётся неанонимный опрос с вариантами: "Буду", "Не буду", "Еще не знаю"

Опрос автоматически закрывается через час после указанного времени (пока работает только по Берлинскому времени).

# Development guide

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