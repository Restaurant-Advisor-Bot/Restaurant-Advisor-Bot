package ru.spbstu.restaurantadvisor.llm;

public interface GigaChatClient {
    /**
     * @param prompt текст запроса
     * @return сгенерированный ответ
     * @throws TimeoutException при превышении таймаута 30 секунд
     * @throws LlmApiException при ошибках API
     */
    String generateExplanation(String prompt) throws TimeoutException, LlmApiException;
}