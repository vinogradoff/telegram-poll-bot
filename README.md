# Usage guide

- Добавьте бота в телеграм-группу.
- Пишите команды, начинающиеся с `/cghk` или `/quiz`

Параметры: дата или день недели, время, место, дополнительная информация. Параметры разделены пробелами.

    /chgk 16.02 14:00 Клубе ТурнирОСВЧ
    /quiz 1.4 12:00 Дома у Олега
    /chgk вт 14:00 Клубе ТурнирОСВЧ
    /quiz четверг 12:00 Дома у Олега

Создаётся неанонимный опрос с вариантами: "Буду", "Не буду", "Еще не знаю"

День недели - всегда следующий (т.е. через 1-7 дней, не сегодня).

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