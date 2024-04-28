import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {
    // Карта для хранения частоты повторений количества поворотов направо
    public static final Map<Integer, Integer> frequencyMap = new HashMap<>();

    // Основной метод программы
    public static void main(String[] args) {
        int numOfThreads = 1000; // Количество потоков
        Thread[] threads = new Thread[numOfThreads]; // Массив для хранения потоков

        // Запускаем все потоки
        for (int i = 0; i < numOfThreads; i++) {
            threads[i] = new Thread(new RouteGenerator()); // Создаем новый поток с объектом RouteGenerator
            threads[i].start(); // Запускаем поток
        }

        // Ожидаем завершения выполнения всех потоков
        for (int i = 0; i < numOfThreads; i++) {
            try {
                threads[i].join(); // Ждем завершения работы каждого потока
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Выводим результаты
        printResult();
    }

    // Метод для обновления карты frequencyMap
    public static void updateFrequencyMap(int count) {
        synchronized (frequencyMap) { // Используем блок синхронизации для безопасного доступа к карте
            // Обновляем значение в карте: увеличиваем на 1, если значение уже существует, иначе ставим 1
            frequencyMap.put(count, frequencyMap.getOrDefault(countв, 0) + 1);
        }
    }

    // Метод для вывода результатов
    public static void printResult() {
        int maxFrequency = 0; // Максимальное количество повторений
        Map<Integer, Integer> frequencyToSize = new HashMap<>(); // Карта для хранения соответствия частоты и размера

        synchronized (frequencyMap) { // Используем блок синхронизации для безопасного доступа к карте
            // Перебираем все записи в карте
            for (int count : frequencyMap.keySet()) {
                int frequency = frequencyMap.get(count); // Получаем частоту для текущего значения
                maxFrequency = Math.max(maxFrequency, frequency); // Обновляем максимальное количество повторений
                frequencyToSize.put(frequency, count); // Заполняем карту соответствия частоты и размера
            }
        }

        // Выводим информацию о самом частом количестве повторений и размере
        System.out.println("Самое частое количество повторений " + maxFrequency + " (встретилось " + frequencyToSize.get(maxFrequency) + " раз)");
        System.out.println("Другие размеры:");

        // Выводим информацию о остальных размерах
        for (int frequency = maxFrequency - 1; frequency > 0; frequency--) {
            if (frequencyToSize.containsKey(frequency)) {
                System.out.println("- " + frequencyToSize.get(frequency) + " (" + frequency + " раз)");
            }
        }
    }

    // Внутренний класс для генерации маршрутов
    public static class RouteGenerator implements Runnable {
        private static final String INSTRUCTIONS = "RLRFR"; // Доступные инструкции
        private static final int ROUTE_LENGTH = 100; // Длина маршрута

        @Override
        public void run() {
            Random random = new Random(); // Создаем генератор случайных чисел
            StringBuilder route = new StringBuilder(); // Строим маршрут
            int rightCount = 0; // Счетчик поворотов направо

            // Генерируем маршрут
            for (int i = 0; i < ROUTE_LENGTH; i++) {
                char instruction = INSTRUCTIONS.charAt(random.nextInt(INSTRUCTIONS.length())); // Получаем случайную инструкцию
                if (instruction == 'R') {
                    rightCount++; // Подсчитываем повороты направо
                }
                route.append(instruction); // Добавляем инструкцию в маршрут
            }

            updateFrequencyMap(rightCount); // Обновляем карту с учетом количества поворотов направо
        }
    }
}
