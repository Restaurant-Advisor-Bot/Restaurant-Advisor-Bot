package ru.spbstu.restaurantadvisor.service.explanation;

import ru.spbstu.restaurantadvisor.llm.GigaChatClient;
import ru.spbstu.restaurantadvisor.llm.TimeoutException;
import ru.spbstu.restaurantadvisor.llm.LlmApiException;
import ru.spbstu.restaurantadvisor.service.requeststatus.RequestStatusService;
import ru.spbstu.restaurantadvisor.service.history.HistoryService;
import ru.spbstu.restaurantadvisor.bot.sender.MessageSender;
import org.springframework.stereotype.Service;

@Service
public class ExplanationServiceImpl implements ExplanationService {

    private final RequestStatusService requestStatusService;
    private final GigaChatClient gigaChatClient;
    private final MessageSender messageSender;
    private final HistoryService historyService;

    public ExplanationServiceImpl(RequestStatusService requestStatusService,
                                  GigaChatClient gigaChatClient,
                                  MessageSender messageSender,
                                  HistoryService historyService) {
        this.requestStatusService = requestStatusService;
        this.gigaChatClient = gigaChatClient;
        this.messageSender = messageSender;
        this.historyService = historyService;
    }

    @Override
    public void generateExplanation(long telegramId, String restaurantId,
                                    String restaurantName, String preferences,
                                    String previousContext) {

        if (requestStatusService.isGenerationInProgress(telegramId, restaurantId)) {
            messageSender.sendMessage(telegramId,
                "Объяснение уже генерируется, пожалуйста, подождите");
            return;
        }

        requestStatusService.startGeneration(telegramId, restaurantId);
        messageSender.sendMessage(telegramId, "Генерирую объяснение…");

        try {
            String prompt = String.format(
                "Объясни, почему ресторан '%s' подходит пользователю с предпочтениями: %s. %s",
                restaurantName, preferences, previousContext);
            String explanation = gigaChatClient.generateExplanation(prompt);
            historyService.addExplanationNote(telegramId, restaurantId, explanation);
            messageSender.sendMessage(telegramId, explanation);
            requestStatusService.completeGeneration(telegramId, restaurantId, true);
        } catch (TimeoutException | LlmApiException e) {
            messageSender.sendMessage(telegramId, "Не удалось сгенерировать объяснение. Попробуйте позже");
            requestStatusService.completeGeneration(telegramId, restaurantId, false);
        }
    }
}