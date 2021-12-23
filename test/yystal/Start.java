package test.yystal;

import pisi.unitedmeows.yystal.utils.kThread;
import pisi.unitedmeows.yystal.web.YWebClient;

import java.io.File;
import java.util.HashMap;

public class Start {

    public static void main(String[] args) {

        YWebClient webClient = new YWebClient();
        webClient.downloadFile("https://placekitten.com/g/200/300", new File("cat.png"));
        System.out.println("Finished downloading");

        kThread.sleep(10000);
    }

}
