/**
 * Created by Sedlerr on 11.04.2016.
 */
public class CrawlerTest
{

    public static void main(String[] args)
    {
        int Depthd = 1;
        try {
            Crawler crawler = new Crawler(Depthd, "Results/", "Seeds.txt", false);
        }
        catch (InstantiationException a) {
            System.out.println(a);
        }
        catch (IllegalAccessException b) {
            System.out.println(b);
        }


    }
}