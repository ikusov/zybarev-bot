package ru.ikusov.training.telegrambot.services.wordle;

import org.springframework.stereotype.Component;

import java.io.IOException;

//TODO: добавить вордчекер из БД, чтобы обезопаситься от проблем с внешними сервисами
@Component
public interface WordChecker {
    String getName();

    /**
     * Проверяет, является ли слово существительным в именительном падеже.
     *
     * @param word русское слово в нижнем регистре.
     * @return признак того, что слово является существительным в именительном падеже.
     * todo: выпилить выброс исключения отсюда
     * @throws IOException в случае ошибки ввода-вывода при подключении к ресурсу
     */
    boolean check(String word) throws IOException;
}
