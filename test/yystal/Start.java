package test.yystal;

import org.jsfml.graphics.Color;
import static pisi.unitedmeows.yystal.YYStal.*;

import pisi.unitedmeows.yystal.exception.YEx;
import pisi.unitedmeows.yystal.parallel.Future;
import pisi.unitedmeows.yystal.parallel.Promise;
import pisi.unitedmeows.yystal.ui.YWindow;
import pisi.unitedmeows.yystal.utils.Mutex;

import java.util.Random;

import static pisi.unitedmeows.yystal.parallel.Async.*;

public class Start {

    public static void main(String[] args) {
        mutex.lock();
        System.out.println("test");
        try {
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
        
        }
        mutex.unlock();
    
        
        YWindow yWindow = new YWindow("Hello world!", 300, 300);
        yWindow.open();
    }
}
