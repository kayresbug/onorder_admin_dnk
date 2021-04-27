package com.daon.admin_onorder;

import android.app.Application;
import android.util.Log;

import com.sam4s.printer.Sam4sPrint;

public class AdminApplication extends Application {
    private static Sam4sPrint printer = new Sam4sPrint();
    private static Sam4sPrint printer2 = new Sam4sPrint();
    private static Sam4sPrint printer3 = new Sam4sPrint();

    public static void setPrinter(Sam4sPrint printer1){
        printer = printer1;
    }
    public static void setPrinter2(Sam4sPrint printer1){
        printer2 = printer1;
    }
    public static void setPrinter3(Sam4sPrint printer1){
        printer3 = printer1;
    }

    public static boolean IsConnected1(){
        return printer.IsConnected(0);
    }
    public static boolean IsConnected2(){
        return printer2.IsConnected(0);
    }
    public static boolean IsConnected3(){
        return printer3.IsConnected(0);
    }

    public static Sam4sPrint getPrinter(){
        return printer;
    }
    public static Sam4sPrint getPrinter2(){
        return printer2;
    }
    public static Sam4sPrint getPrinter3(){
        return printer3;
    }
}
