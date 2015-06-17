package me.zcx.vertx.http;

import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by zcx2001 on 2015-06-17.
 */
public class UrlTest {
    public static void main(String[] args) throws IOException {
        URL a = new URL("http://img03.taobaocdn.com/imgextra/i3/320868932/TB2JeHWcVXXXXbCXpXXXXXXXXXX_%21%21320868932.jpg");
        System.out.println(a.getProtocol());
        System.out.println(a.getHost());
        System.out.println(a.getPort());
        System.out.println(a.getDefaultPort());
        System.out.println(a.getPath());

        System.out.println(FilenameUtils.getName(a.getPath()));
        System.out.println(FilenameUtils.getExtension(a.getPath()));
    }
}
