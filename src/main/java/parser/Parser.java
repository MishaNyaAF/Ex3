package parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Parser {
    private static String getTown() {
        var townScan = new Scanner(System.in);
        System.out.println("Введите название города: ");
        return townScan.nextLine();
    }

    private static Document getPage(String town) throws IOException {
        String url = "https://www.gismeteo.ru/catalog/russia/";
        Element catalog = Jsoup.parse(new URL(url), 3000).selectFirst("div[class=popular-cities]");
        assert catalog != null;
        Elements towns = catalog.select("div[class=catalog-item-link]");
        String href = "";
        for (var t : towns) {
            if (t.text().contains(town)) {
                href = Objects.requireNonNull(t.selectFirst("a")).attr("href");
            }
        }
        if (href.equals("")) {
            return null;
        }
        String urla = "https://www.gismeteo.ru" + href + "weekly/";
        return Jsoup.parse(new URL(urla), 3000);
    }

    public static void parseval(String town) throws IOException {
        if (getPage(town) != null) {
            Element weatherTable = Objects.requireNonNull(getPage(town)).selectFirst("div[class=widget-items]");
            assert weatherTable != null;
            Elements dates = weatherTable.select("div[class=date]");
            Elements days = weatherTable.select("div[class=day]");
            Elements skyTwo = weatherTable.select("div[class=weather-icon tooltip]");
            var sk = new ArrayList<String>();
            for (var element : skyTwo) {
                sk.add(element.attr("data-text"));
            }
            Elements temp = weatherTable.select("span[class=unit unit_temperature_c]");
            Elements winds = Objects.requireNonNull(weatherTable.selectFirst(
                    "div[class=widget-row widget-row-wind-gust row-with-caption]")).select("div[class=row-item]");
            var windList = new ArrayList<String>();
            for (var element : winds.select("div[class=row-item]")) {
                windList.add(element.select("span[class=wind-unit unit unit_wind_m_s]").text());
            }
            System.out.println(Objects.requireNonNull(getPage(town).selectFirst("h1")).text());
            System.out.printf("%1$10s%2$35s%3$24s%4$12s\n", "Дата", "Облачность, осадки", "Температура (min-max)", "Ветер");
            for (int i = 0; i < 7; i++) {
                System.out.printf("%1$6s, %2$2s %3$34s %4$12s°C - %5$3s°C %6$8s м/с\n", dates.get(i).text(), days.get(i).text(),
                        sk.get(i), temp.get(2 * i + 2).text(), temp.get(2 * i + 1).text(), windList.get(i));
            }
        } else {
            System.out.println("Такого города не найдено в каталоге :(");
        }
    }
}
