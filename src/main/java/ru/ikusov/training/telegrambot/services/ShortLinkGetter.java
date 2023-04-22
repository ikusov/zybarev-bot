package ru.ikusov.training.telegrambot.services;

public class ShortLinkGetter {
    private static final String urlShorter = "https://clck.ru/--?url=";

    public static String getShortLink(String urlToShort) {
        String url = urlShorter + urlToShort;
        String answer = urlToShort;
//        StringBuffer response = new StringBuffer();
        try {
            answer = new HttpConnector(url).getTextString();
//            URL obj = new URL(url);
//            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
//
//            connection.setRequestMethod("GET");
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String inputLine;
//
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return answer;
    }
}
