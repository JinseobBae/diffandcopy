import utils.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static utils.CommonUtils.*;

public class CopyFileService {

    private List<String> newFileList = new ArrayList<>();
    private List<String> modFileList = new ArrayList<>();
    private List<String> delFileList = new ArrayList<>();
    private List<String> errorList = new ArrayList<>();

    boolean fileTask(List<String> resultList ,String path, String beforeDir, String afterDir) throws Exception{

//        Date date = new Date();

//        println(getNowTimeMilSec("YYYYMMDD_HHmmssSSS"));

        String rootDirName = path + File.separator + "result_" + getNowTimeMilSec("YYYYMMDD_HHmmssSSS");
        File rootDir = new File(rootDirName);


        if(!rootDir.exists()){
            rootDir.mkdir();
        }

        String[] txt;
        String changedFilePath = "";
        String[] pathSplit;
        String dirPath = "";
        String originFile = "";

        for(String result : resultList){
            if(!result.contains(".war") && !result.contains(".MF")){
                if(result.startsWith("Files") && result.endsWith("differ")){ //다른 파일
                    txt = result.split(" and ");
                    changedFilePath = txt[1].replace(" differ",""); // 끝에 differ 자르기
                    originFile = path + File.separator + changedFilePath; //기존파일명
                    pathSplit = changedFilePath.split("/");
                    dirPath = rootDirName;
                    dirPath = makeFileName(pathSplit, dirPath, afterDir);
                    String tempStr = dirPath.replace(rootDirName, "");
                    modFileList.add(tempStr);
                    println(Color.CYAN.getValue() + tempStr + " is copying" + Color.RESET.getValue());
                    fileCopy(originFile, dirPath);
                }else if(result.startsWith("Only in " + afterDir )){ //신규파일
//                Only in after/test: aaa.txt
                    txt = result.split(" in ")[1].split(": ");
                    changedFilePath = txt[0] + "/" +txt[1];
                    originFile = path + File.separator + changedFilePath;
                    pathSplit = changedFilePath.split("/");
                    dirPath = rootDirName;
                    dirPath = makeFileName(pathSplit, dirPath, afterDir);
                    newFileList.add(originFile.split(afterDir)[1]);

                    if(checkIsFileByName(originFile.split(afterDir)[1])){
                        println(Color.CYAN.getValue() + originFile.split(afterDir)[1] + " is copying" + Color.RESET.getValue());
                    }else{
                        println("origin : " + originFile);
                        println("after : " + afterDir);
                        println("split[1] : " +originFile.split(afterDir)[1]);
                        println(Color.CYAN.getValue() + originFile.split(afterDir)[1] + " (directory) is copying" + Color.RESET.getValue());
                    }
                    fileCopy(originFile, dirPath);
                }else if (result.startsWith("Only in " + beforeDir)){
                    txt = result.split(" in ")[1].split(": ");
                    changedFilePath = txt[0] + "/" +txt[1];
                    originFile = path + File.separator + changedFilePath;
                    originFile = originFile.split(beforeDir)[1];
                    delFileList.add(originFile);
                }
            }
        }

        generateResultFile(rootDirName);

        String resultFileName = rootDirName + File.separator + "copyResult.txt";
        int totalSize = newFileList.size() + modFileList.size() + delFileList.size();

        printBlank();

        println("**************************************"            );
        println("* [RESULT]                               "         );
        println("* TOTAL     : " + totalSize +          " files"    );

        if(newFileList.size() > 0){
            println(Color.GREEN.getValue() +  "* NEW       : " + newFileList.size() + " files" + Color.RESET.getValue()   );
        }else{
            println("* NEW       : " + 0 + " files"    );
        }

        if(modFileList.size() > 0){
            println(Color.GREEN.getValue() + "* MODIFIED  : " + modFileList.size() + " files" + Color.RESET.getValue()    );
        }else{
            println("* MODIFIED  : " + 0 + " files"    );
        }

        if(delFileList.size() > 0){
            println(Color.GREEN.getValue() + "* DELETED   : " + delFileList.size() + " files" + Color.RESET.getValue()    );
        }else{
            println("* DELETED   : " + 0 + " files"    );
        }

        if(errorList.size() > 0){
            println(Color.RED.getValue() + "* ERROR     : " + errorList.size() + " errors" + Color.RESET.getValue());
        }else{
            println("* ERROR     : " + 0 + " errors"     );
        }


        println("* RESULT FILE : " + resultFileName                 );
        println("**************************************             ");





        return true;

    }

    String makeFileName(String[]  pathSplit, String dirPath, String afterDir){
        String result = dirPath;
        for(String pathPiece : pathSplit){
            if(!afterDir.equals(pathPiece)){
                result = result + File.separator + pathPiece;
                if(!checkIsFileByName(pathPiece)){ //디렉토리
                    File dir = new File(result);
                    if(!dir.exists()){
                        dir.mkdir();
                    }
                }
            }
        }

        return result;
    }



    void fileCopy(String from, String to ){

        File inFile = new File(from);
        File outFile = new File(to);
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;

        try{
            if(checkIsFileByName(to)){
                inputStream = new FileInputStream(inFile);
                outputStream = new FileOutputStream(outFile);
                int data = 0;

                while((data=inputStream.read()) != -1){
                    outputStream.write(data);
                }
            }else{
                File[] outArray = inFile.listFiles();
                for(File file : outArray){
                    File temp = new File(outFile.getAbsolutePath() + File.separator + file.getName());
                    if(file.isDirectory()){
                        temp.mkdir();
                        fileCopy(file.getCanonicalPath(), temp.getCanonicalPath());
                    }else{
                        inputStream = new FileInputStream(file);
                        outputStream = new FileOutputStream(temp);
                        byte[] b = new byte[4096];
                        int cnt = 0;
                        while((cnt=inputStream.read(b)) != -1){
                            outputStream.write(b,0,cnt);
                        }
                    }
                }
            }
        }catch(Exception e){
            println(Color.RED.getValue() + e.toString() + Color.RESET.getValue());
            errorList.add(e.toString());
        }finally {
            try{
                if(inputStream != null){
                    inputStream.close();
                }
                if(outputStream != null){
                    outputStream.close();
                }
            }catch (Exception e1){
                println(Color.RED.getValue() + e1.toString() + Color.RESET.getValue());
                errorList.add(e1.toString());
            }
        }
    }


    void generateResultFile(String rootDirName){

        File result = new File(rootDirName + File.separator + "copyResult");

        try{
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(result),"MS949"));

            try{
                if(newFileList.size() > 0){
                    writer.append("******************* \n");
                    writer.append("** NEW FILE LIST ** \n");
                    writer.append("******************* \n");
                    writer(writer, newFileList);

                }

                if(modFileList.size() > 0){
                    writer.append("************************ \n");
                    writer.append("** MODIFIED FILE LIST ** \n");
                    writer.append("************************ \n");
                    writer(writer, modFileList);
                }

                if(delFileList.size() > 0){
                    writer.append("*********************** \n");
                    writer.append("** DELETED FILE LIST ** \n");
                    writer.append("*********************** \n");
                    writer(writer, delFileList);
                }

                if(errorList.size() > 0){
                    writer.append("*********************** \n");
                    writer.append("** ERROR DURING COPY ** \n");
                    writer.append("*********************** \n");
                    writer(writer, errorList);
                }

                writer.flush();
                writer.close();
            }catch(Exception e1){
                e1.printStackTrace();
            }finally {
                if(writer != null){
                    writer.close();
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    BufferedWriter writer(BufferedWriter writer, List<String> list) throws Exception{

        for(String word : list){
            writer.append(word);
            writer.append("\n");
        }

        return writer;
    }



}
