package ru.ikusov.training.telegrambot.services.example;

//todo: логику задавания примера и обработки ответа на пример перенести сюда
//todo: работу с БД (сохранение ответов, подсчёт достижений, проверка начата ли игра)
//todo: делегировать в адаптер ExampleRepositoryAdapter

//todo: всякую матлогику генерация примеров тоже отдельный сервис

//todo: логика формирования сообщения тоже в отдельный сервис
//@Service
//@RequiredArgsConstructor
public class ExampleService {
//    private final ExampleProvider exampleProvider;
//    private final ExampleRepository exampleRepository;
//
//    public String handleExampleAnswer(User user, Chat chat, Integer userAnswer) {
//        long timer = System.nanoTime() - exampleProvider.getTimer();
//        int rightAnswer = exampleProvider.getAnswerInt();
//
//        boolean isRight = Objects.equals(userAnswer, rightAnswer);
//
//        List<ExampleAnswerEntity> answers = exampleRepository.findAll();
//        int timerSeconds = (int) (timer / 1_000_000_000);
//        MathAchieves achieves = new MathAchieves(answers, user, chat, rightAnswer, timerSeconds);
//
//        var exampleAnswerMessageGenerator =
//                new ExampleAnswerMessageGenerator
//                        (achieves, user, chat, userAnswer,
//                                rightAnswer, (int) (timer / 1_000_000_000));
//        textAnswer = exampleAnswerMessageGenerator.generate();
//        score = exampleAnswerMessageGenerator.getExampleScore();
//
//        //todo: из ExampleAnswerEntity выпилить ChatEntity, оставить только id чата
//        //todo: но id чата теперь должен быть составной Chat ID + Group ID
//        exampleAnswer = new ExampleAnswerEntity()
//                //todo: эту штучку можно заменить на @CreationTimestamp
//                .setTimestamp(System.currentTimeMillis() / 1000)
//                //todo: утильный класс для формирования БД-ID чата из телеграм-ID чата и ID топика
//                .setChatId(formChatId(chat))
//                .setUser(user)
//                .setRight(isRight);
//
//        if (isRight) {
//            exampleAnswer.setTimer(timer / 1_000_000).setScore(score);
//            exampleProvider.setAnswered(true);
//        }
//
//        databaseConnector.save(exampleAnswer);
//    }
//
//    public boolean isAnswered(Chat chat) {
//        return true;
//    }
}
