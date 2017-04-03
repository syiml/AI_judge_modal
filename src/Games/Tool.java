package Games;

import net.sf.json.JSONObject;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 杂项功能
 * Created by Administrator on 2015/11/24 0024.
 */
public class Tool {
    public static int sleep(int t){
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            return 1;
        }
        return 1;
    }
    public static String nowDate(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }
    public static Timestamp now(){
        return new Timestamp(System.currentTimeMillis());
    }
    public static void debug(String s){
        debug(s,2);
    }
    private static FileWriter LogFile = null;
    private static FileWriter openFile(String filePath,String fileName){
        FileWriter fw = null;
        try {
            fw = new FileWriter(filePath+fileName, true);
        }catch (FileNotFoundException e){
            File f = new File(filePath);
            if(!f.exists()){
                f.mkdirs();
            }
            File file = new File(filePath,fileName);
            if(!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException ee) {
                    // TODO Auto-generated catch block
                    ee.printStackTrace();
                }
            }
            try {
                fw = new FileWriter(filePath+fileName, true);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }catch (Exception ignored){}
        return fw;
    }
    private static void debug(String s, int stackDepth){
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        Thread current = Thread.currentThread();
        String nowTime = now().toString();
        while(nowTime.length() < 23) nowTime+="0";
        try {
            LogFile = openFile("/data/out/","log");
            LogFile.write("【"+nowTime+"|"+current.getId()+"】"+s+"["+stacks[stackDepth]+"]\n");
            LogFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Timestamp getTimestamp(String d, String s, String m){
        //System.out.println(d + " " + s + ":" + m + ":00");
        return Timestamp.valueOf(d + " " + s + ":" + m + ":00");
    }
    public static int randNum(int l,int r){
        if(l>r) return 0;
        return (int)(Math.random()*(r-l+1)+l);
    }
    public static JSONObject readJsonFromFile(String Path){
        BufferedReader reader = null;
        String laststr = "";
        try{
            FileInputStream fileInputStream = new FileInputStream(Path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while((tempString = reader.readLine()) != null){
                laststr += tempString;
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return JSONObject.fromObject(laststr);
    }
}
