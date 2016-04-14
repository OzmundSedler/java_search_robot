/**
 * Created by Sedlerr on 11.04.2016.
 */
import javax.lang.model.element.Element;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Crawler {
    int Depth;
    String Path;
    String Seeds_path;
    boolean Information_type;

    /*
    Конструктор класса

    _Path - Путь до output файла
    _Seeds_path - Путь до файла с сидами
    _Depth - Глубина обхода сайтов
    _Information_type < 0 - urls only, 1 - html files >
    _User_agent - Тип подключения к сайтам
    */

    public Crawler (int _Depth, String _Path, String _Seeds_path, boolean _Information_type)
            throws InstantiationException, IllegalAccessException {
        Depth = _Depth;
        Path = _Path;
        Seeds_path = _Seeds_path;
        Information_type = _Information_type;
        // do function for this
        String filename = Path + "url_file.txt";
        File f = new File(filename);
        if(f.exists() && !f.isDirectory()) { // Проверка на существование файла для вывода url и очищение его
            try {
                PrintWriter writer = new PrintWriter(filename);
                writer.print("");
                writer.close();
            }
            catch (IOException a) {
                System.out.println("No url_file yet"+a);
            }
        }
        // and this
        try {
            List<String> seeds = Files.readAllLines(Paths.get(Seeds_path), utf8charset); // Считывание сидов
            for ( String One_seed : seeds) {
                System.out.println(String.format(" %s ", One_seed));
                search(One_seed);
            }

        }
        catch (IOException e){
            System.out.println(String.format("Getting seeds error : %s",e));
        }
    }

    public int level = 0; // Текущий уровень вложенности
    private Charset utf8charset = Charset.forName("UTF-8"); // Кодировка
    private Set<String> pagesVisited = new HashSet<String>(); // Список посещенных страниц
    // Set выбран, т.к. в данной структуре данных
    // каждый элемент уникален
    private List<String> pagesToVisit = new LinkedList<String>(); // Список страниц, которые надо посетить
    // Формируется добавление в него url-ов с распарсенных страниц
    private List<String> Buffer = new LinkedList<String>();


    /* Метод выбора следующего url
    Не возвращает url-ы, которое уже были посещены
     */
    private String nextUrl() {

        String nextUrl;
        do {
            nextUrl = this.pagesToVisit.remove(0); // Берем первый элемент мн-ва страниц к посещению
        } while (this.pagesVisited.contains(nextUrl) && (!this.pagesToVisit.isEmpty())); // Проверяем, что еще не посетили эту страницу,
        // Если она есть в списке pagesVisited берем следующую
        this.pagesVisited.add(nextUrl);  // Добавляем в список посещенных
        return nextUrl; // Возвращаем
    }


    /* Основной метод класса, который осуществялет поиск новых страниц,
       подключение к ним, скачивание документов, путем обращения к классу Parser
    */
    public void search(String seed) {
        level = 0;  // Очищаем все буфферы и параметры, чтобы искать по другому seed-у
        this.Buffer.clear();
        this.pagesToVisit.clear();

        String currentUrl;
        Parser parser = new Parser(Path, Information_type); // Создаем новый объект класса парсер

        this.pagesToVisit.add(seed); // Добавляем первоначальный сид в список страниц для посещения

        while (level < Depth) // Пока не достигли нужной глубины проходим цикл
        {

            while (!this.pagesToVisit.isEmpty()) { // Пока есть страницы для посещения
                currentUrl = this.nextUrl(); // Берем url
                parser.crawl(currentUrl,seed); // Идем по нему и собираем данные
                this.Buffer.addAll(parser.getLinks()); // Добавляем найденные url-ы в буффер
            }
            this.pagesToVisit.addAll(Buffer); // После того, как закончили текущий уровень, добавляем содержимое буффера
                                              // В массив для дальнейшего посещения
            level++;
        }
        System.out.println(String.format("**Done** Visited %s web page(s)", this.pagesVisited.size()));
    }


    public static void main(String[] args) {
        try {
            // Стандартные значения
            boolean Information_type = true;
            String Seeds = "Seeds.txt";
            String Path = "Results/";
            int Depth = 1;

            // Ввод из консоли
            if (args.length == 4) {
                Information_type = Boolean.parseBoolean(args[4]);
            }
            if (args.length >= 3) {
                Seeds = args[2];
            }
            if (args.length >= 2) {
                Path = args[1];
            }
            if (args.length >= 1) {
                Depth = Integer.parseInt(args[0]);
                new Crawler(Depth, Path, Seeds, Information_type);
                return;
            }
        } catch (Exception e) {
            System.err.println("An error occured: ");
            e.printStackTrace();
            // System.err.println(e.toString());
        }
        System.err.println("Usage: java simple Crawler <Depth> <Path were to safe> <Path to seeds.txt> <Information type>");
        System.err.println("Crawls the web for html files or urls");
        System.err.println("Default: \n Depth = 1; \n Path = 'Results/'; " +
                "\n Path to seeds = 'Seeds.txt'; \n Information_type = true; \n");
    }
}
