import utils.CommonUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static utils.CommonUtils.printBlank;
import static utils.CommonUtils.println;

/**
 * CREATED BY JINSEOB / 2020-03-13
 */
public class MakePatchFile {

    public static void main(String[] args){

        printBlank();
        printBlank();

        println("***************************************");
        println("*    *****  ******  ******  *    *    *");
        println("*    *      *    *  *    *   *  *     *");
        println("*    *      *    *  ******     *      *");
        println("*    *      *    *  *          *      *");
        println("*    *****  ******  *          *      *");
        println("*************************************** by JIN SEOB");



        System.out.println("FILE COPY PROCESSING....");
        printBlank();

        /**
         * 0 : PATH
         * 1 : BEFORE DIR
         * 2 : AFTER DIR
         * 3: RESULT FILE NAME
         */
        String path = args[0];
        String befroeDir = args[1];
        String afterDir = args[2];
        String resultFileName = args[3];

        File file = new File(path + File.separator + resultFileName);

        if(file.exists()){
            try(FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader)){
                String result = "";
                List<String> resultList = new ArrayList<>();
                while((result = bufferedReader.readLine()) != null){
                    resultList.add(result);
                }
                CopyFileService copyFileService = new CopyFileService();
                copyFileService.fileTask(resultList, path, befroeDir, afterDir);
            }catch (Exception e){
                e.printStackTrace();
                println("Sorry, Failed :/");
            }
        }else {
            println("FILE NOT EXIST!!!");
        }
        println("FILE COPY FINISH!");
    }
}
