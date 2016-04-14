/**
 * Created by Sedlerr on 11.04.2016.
 */
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {
    String Path;
    boolean Information_type;
    public Parser(String _Path, boolean _Information_type){
        Information_type = _Information_type;
        Path = _Path; }
    private static final String USER_AGENT = // Недружественный поисковый робот )
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<String>(); // Список url-ов
    private Document htmlDocument; // HTML-документ


    /*
    Преобразует URL с помощью регулярных выражений в будущее имя файла
     */
    public static String getDomainName(String url) {
        url = url.replaceFirst("^(http://www\\.|http://|www\\.)", "");
        url = url.replaceAll("/", "_");
        return url;
    }


    /*
     Сохраняем код страницы
     */
    public void save_file(Document htmlDocument, String url) throws IOException {
        String domain = getDomainName(url);
        String filename = Path + domain + "file.html";
        System.out.println("Saving to file " + filename);
        BufferedWriter htmlWriter = new BufferedWriter(new OutputStreamWriter
                (new FileOutputStream
                        (filename), "UTF-8"));
        htmlWriter.write(htmlDocument.toString());
        htmlWriter.close();

        System.out.println("Done");

    }

    public void save_url (String url) throws IOException {
        String filename = Path + "url_file.txt";
        String buffer = url + "\n";
//        BufferedWriter Writer = new BufferedWriter(new OutputStreamWriter
//                (new FileOutputStream
//                        (filename), "UTF-8"));
//        Writer.write(url);
//        Writer.close();
        PrintWriter out = new PrintWriter(new FileWriter(filename, true));
        out.write(buffer);
        out.close();
    }
    /*
    Посылает HTTP запрос, проверяет ответ собирает все url-ы со страниц.
    Возвращает успешное/неуспешное заверешение функции
    */
    public boolean crawl(String url, String seed) {
        try {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT); // Устанавливаем соединение с заданным
                                                                              // USER_AGENT

            Document htmlDocument = connection.get();  // Получаем документ

            this.htmlDocument = htmlDocument;
            if (connection.response().statusCode() == 200) // Проверка на успещное соединение
            {
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** Retrieved something other than HTML"); // Проверка на документ = html
                return false;
            }

            if (Information_type) {
                save_file(htmlDocument, url); // сохраняем документ в файл
            }
            else {
                save_url(url);  // сохраняем url в файл
            }

            String seed_for_check  = seed.replaceFirst("^(http://www\\.|http://|www\\.)", "");
            System.out.println(String.format("\n Seed for check: %s  \n",seed_for_check));

            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links"); // Количество найденных ссылок
            System.out.println("Found " + Words_quantity(htmlDocument) + " words"); // Количество найденных слов
            for (Element link : linksOnPage) {
                if (link.absUrl("href").toLowerCase().contains(seed_for_check.toLowerCase())) {
                    this.links.add(link.absUrl("href"));
                }
                else {
//                    System.out.println(String.format("\n %s Not our URL \n",link.absUrl("href")));
                }
//                System.out.println(String.format("link: %s \n seed: %s", link.absUrl("href").toLowerCase(), seed_for_check.toLowerCase()))

            }
            return true;
        } catch (IOException ioe) {
            System.out.println("Bad HTTP request"+ioe);
            return false;
        }
    }


    /*
    Прототип функции подсчета слов
    */
    public int Words_quantity(Document htmlDocument) {
        String text = htmlDocument.body().text();
        int word_q = 0;
        word_q = text.split(" ").length;
        return  word_q;
    }

    /*
       Прототип функции поиска слова
     */
    public boolean searchForWord(String searchWord) {
        // Defensive coding. This method should only be used after a successful crawl.
        if (this.htmlDocument == null) {
            System.out.println("ERROR! Call crawl() before performing analysis on the document");
            return false;
        }
        System.out.println("Searching for the word " + searchWord + "...");
        String bodyText = this.htmlDocument.body().text();
        return bodyText.toLowerCase().contains(searchWord.toLowerCase());
    }

    public List<String> getLinks() {
        return this.links;
    }
}