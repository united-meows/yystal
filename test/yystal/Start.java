package test.yystal;

import static pisi.unitedmeows.yystal.YYStal.*;
import static pisi.unitedmeows.yystal.parallel.Async.*;

import org.lwjglx.Sys;
import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.clazz.valuelock;
import pisi.unitedmeows.yystal.hook.YString;
import pisi.unitedmeows.yystal.parallel.Future;
import pisi.unitedmeows.yystal.parallel.utils.YTimer;
import pisi.unitedmeows.yystal.ui.YWindow;
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
