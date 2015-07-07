package mauro.rodriguez.visualizadorrss.datos;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Mauro Rodriguez on 01/06/2015.
 */
public class MyThread extends Thread {
    private Handler h;
    private String link;
    private int pos;
    public  MyThread(Handler h,String link){
        this.h=h;
        this.link=link;
        this.pos=-1;
    }
    public MyThread(Handler h,String link, int pos){
        this(h,link);
        this.pos=pos;
    }
    @Override
    public void  run(){
        if(this.pos == -1){
            String msg;
            HttpManager httpManager = new HttpManager(link);
            try {
                msg = httpManager.getStrDataByGET();
                if (msg != "Falla"){
                    Message message = new Message();
                    message.obj = msg;
                    message.arg1=0;
                    h.sendMessage(message);
                }else{
                    Message message = new Message();
                    message.arg1=4;
                    h.sendMessage(message);
                }
            } catch(Exception ex){
                Log.d("hilo","falla");
                Message message = new Message();
                message.arg1=-1;
                h.sendMessage(message);
            }
        }else{
            Object img;
            HttpManager httpManager = new HttpManager(link);
            try{
                img=httpManager.getBytesDataByGET();
                Message message = new Message();
                message.obj=img;
                message.arg1=1;
                message.arg2=this.pos;
                h.sendMessage(message);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
