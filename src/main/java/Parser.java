import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static Document getPage() throws IOException {
        String url = "https://www.gismeteo.ru/weather-moscow-4368/weekly/";
        return Jsoup.parse(new URL(url), 3000);
    }

    public static void main(String[] args) throws IOException {
        String date = "";
        Element weatherTable = getPage().selectFirst("div[class=widget-items]");
        assert weatherTable != null;
        Elements dates = weatherTable.select("div[class=date]");
        Elements days = weatherTable.select("div[class=day]");
        Elements sky = weatherTable.select("div[data-text]");
        String skies = sky.toString();
        Pattern skyPattern = Pattern.compile("[А-Яа-я,]+[А-Яа-я ]*");
        Matcher matcherSky = skyPattern.matcher(skies);
        var sk = new ArrayList<String>();
        while (matcherSky.find()) {
            int start = matcherSky.start();
            int end = matcherSky.end();
            sk.add(skies.substring(start, end));
        }
        Elements temp = weatherTable.select("span[class=unit unit_temperature_c]");
        Element windTable = weatherTable.selectFirst("div[class=widget-row widget-row-wind-gust row-with-caption]");
        assert windTable != null;
        String winds = windTable.toString();
        Pattern windPattern = Pattern.compile("m_s\">\\d+");
        Matcher matcher = windPattern.matcher(winds);
        var wi = new ArrayList<String>();
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            wi.add(winds.substring(start, end));
        }
        var windList = new ArrayList<String>();
        for (String w : wi) {
            windList.add(w.substring(5));
        }
        System.out.printf("%1$10s%2$30s%3$24s%4$12s\n", "Дата", "Облачность, осадки", "Температура (min-max)", "Ветер");
        for (int i = 0; i < 7; i++) {
            System.out.printf("%1$6s, %2$2s %3$29s %4$12s°C - %5$3s°C %6$8s м/с\n", dates.get(i).text(), days.get(i).text(),
                    sk.get(i),  temp.get(2 * i + 2).text(), temp.get(2 * i + 1).text(), windList.get(i));
        }
    }
}
