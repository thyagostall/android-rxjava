package com.thyago.rxjava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import rx.Observable;

/**
 * Created by thyago on 06/01/2017.
 */

public class URLRepository {
    public static Observable<List<String>> get() {
        String[] items = {
                "http://blog.danlew.net/2014/09/15/grokking-rxjava-part-1/",
                "http://fragmentedpodcast.com/",
                "https://github.com/kaushikgopal/RxJava-Android-Samples",
                "https://github.com/thyagostall/android-rxjava",
                "http://www.thyago.com/address-which-does-not-exist"
        };
        return Observable.just(Arrays.asList(items));
    }

    private static String parseTitle(String html) {
        int start = html.indexOf("<title>") + 7;
        int end = html.indexOf("</title>", start);

        return html.substring(start, end);
    }

    public static Observable<String> getTitle(String address) {
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder html = new StringBuilder();
            String line;
            while ((line = buffer.readLine()) != null) {
                html.append(line);
            }

            return Observable.just(parseTitle(html.toString()));
        } catch (IOException e) {
            return Observable.just(null);
        }
    }
}
