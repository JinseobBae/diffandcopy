package utils;

import java.io.File;
import java.text.SimpleDateFormat;

public class CommonUtils {

    public static void printBlank(){ System.out.println();}

    public static void println(String txt){ System.out.println(txt);}

    public static void println(int txt){ System.out.println(txt);}

    public static boolean checkIsFileByName(String txt){

        String[] pathSep = txt.split("/");
        int pathLength = pathSep.length;

        if(pathLength > 0 && pathSep[pathLength -1 ].contains(".")){
            return true;
        }else{
            return false;
        }

//        if(txt.endsWith(".class") || txt.endsWith(".jar") || txt.endsWith(".xml") || txt.endsWith(".properties") || txt.endsWith(".js") || txt.endsWith(".ftl")
//                || txt.endsWith(".hbs") || txt.endsWith(".txt")){
//            return true;
//        }else{
//            return false;
//        }
    }

    public static String getNowTimeMilSec(String format){
        return new SimpleDateFormat(format).format(System.currentTimeMillis());
    }

    public static String toFileSeperator(String txt){
        return txt.replaceAll("/", File.separator);
    }

}
