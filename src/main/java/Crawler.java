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
    /*
    Конструктор класса

    Path - Путь до файла с seed-ами
    Depth - Глубина обхода сайтов
    Information_type < 0 - urls only, 1 - html files >
    User_agent - Тип подключения к сайтам
    */
    int Depth;
    String Path;
    String Seeds_path;
    boolean Information_type;

//    public Crawler(String Path, int Depth,  maxThreads, boolean Information_type, String User_agent);}
    public Crawler (int _Depth, String _Path, String _Seeds_path, boolean _Information_type)
            throws InstantiationException, IllegalAccessException {
        Depth = _Depth;
        Path = _Path;
        Seeds_path = _Seeds_path;
        Information_type = _Information_type;
        // do function for this
        String filename = Path + "url_file.txt";
        File f = new File(filename);
        if(f.exists() && !f.isDirectory()) {
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
            List<String> seeds = Files.readAllLines(Paths.get(Seeds_path), utf8charset);
            for ( String One_seed : seeds) {
                System.out.println(String.format(" %s ", One_seed));
                search(One_seed);
            }

        }
        catch (IOException e){
            System.out.println(String.format("Getting seeds error : %s",e));
        }
    }
    // Поля

    public int level = 0;
    private Charset utf8charset = Charset.forName("UTF-8");
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


    // Парсер
    public void search(String seed) {
        level = 0; // кустарно -__-
        this.Buffer.clear();
        this.pagesToVisit.clear();
        //System.out.println(String.format(" %s ", Seeds_path));
//        try {
//            List<String> seeds = Files.readAllLines(Paths.get(Seeds_path), utf8charset);
//            for ( String One_seed : seeds) {
//                System.out.println(String.format(" %s ", One_seed));
//            }
//
//        }
//        catch (IOException e){
//            System.out.println(String.format("Getting seeds error : %s",e));
//            return;
//        }
        String currentUrl;
        Parser parser = new Parser(Path, Information_type);
//        if (this.pagesToVisit.isEmpty())  // Если нет осталось страниц, которые надо посетить
//        {
//            currentUrl = seed; // Присваиваем переменной исходный url
//            this.pagesVisited.add(seed); // Добавляем его в список посещенных страниц
//        } else {
//            currentUrl = this.nextUrl(); // Берем следующий url
//        }
//
//        parser.crawl(currentUrl, seed); // Метод, описанный в parser-е
//
//        this.pagesToVisit.addAll(parser.getLinks());
//        level++;
        this.pagesToVisit.add(seed);
        //this.pagesVisited.add(seed);
        while (level < Depth) // Пока не достигли предела по страницам
        {

            while (!this.pagesToVisit.isEmpty()) {
                currentUrl = this.nextUrl();
                parser.crawl(currentUrl,seed);
                this.Buffer.addAll(parser.getLinks());
            }
            this.pagesToVisit.addAll(Buffer);
            level++;
        }
        System.out.println(String.format("**Done** Visited %s web page(s)", this.pagesVisited.size()));
    }


    public static void main(String[] args) {
        try {
            boolean Information_type = true;
            String Seeds = "Seeds.txt";
            String Path = "Results/";
            int Depth = 1;

            if (args.length == 4) {
                Information_type = Boolean.parseBoolean(args[4]);
            }
            if (args.length >= 3) {
                Seeds = args[3];
            }
            if (args.length >= 2) {
                Path = args[2];
            }
            if (args.length >= 1) {
                Depth = Integer.parseInt(args[1]);
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
