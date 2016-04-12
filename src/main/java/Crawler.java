/**
 * Created by Sedlerr on 11.04.2016.
 */
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Crawler {
    // Поля

    public int MAX_PAGES_TO_SEARCH = 10; // Количество рассматриваемых страниц
    private Set<String> pagesVisited = new HashSet<String>(); // Список посещенных страниц
    // Set выбран, т.к. в данной структуре данных
    // каждый элемент уникален
    private List<String> pagesToVisit = new LinkedList<String>(); // Список страниц, которые надо посетить
    // Формируется добавление в него url-ов с распарсенных страниц

    /* Метод выбора следующего url
    Не возвращает url-ы, которое уже были посещены
     */

    private String nextUrl() {
        String nextUrl;
        do {
            nextUrl = this.pagesToVisit.remove(0); // Берем первый элемент мн-ва страниц к посещению
        } while (this.pagesVisited.contains(nextUrl)); // Проверяем, что еще не посетили эту страницу,
        // Если она есть в списке pagesVisited берем следующую
        this.pagesVisited.add(nextUrl);  // Добавляем в список посещенных
        return nextUrl; // Возвращаем
    }


    // Парсер
    public void search(String url) {
        while (this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) // Пока не достигли предела по страницам
        {
            String currentUrl;
            Parser parser = new Parser();
            if (this.pagesToVisit.isEmpty())  // Если нет осталось страниц, которые надо посетить
            {
                currentUrl = url; // Присваиваем переменной входящий url
                this.pagesVisited.add(url); // Добавляем его в список посещенных страниц
            } else {
                currentUrl = this.nextUrl(); // Берем следующий url
            }

            parser.crawl(currentUrl); // Метод, описанный в parser-е

            this.pagesToVisit.addAll(parser.getLinks());
        }
        System.out.println(String.format("**Done** Visited %s web page(s)", this.pagesVisited.size()));
    }
}
