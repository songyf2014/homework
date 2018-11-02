package com.song.server;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class ChartTask implements Runnable {

    private final Socket socket;

    private volatile Boolean down = false;

    public ChartTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("add one client");
        InputStream inputStream = null;
        InputStreamReader streamReader = null;
        BufferedReader bufferedReader = null;
        PrintWriter pw = null;

        try {
            inputStream = socket.getInputStream();
            streamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(streamReader);
            pw = new PrintWriter(socket.getOutputStream());
            String info = null;
            while (true && !down) {
                if ((info = bufferedReader.readLine()) != null) {
                    System.out.println("client say:" + info);
                    pw.write("server echo:" + info +"\n");
                    pw.flush();
                    if("bye".equalsIgnoreCase(info)){
                        down = true;
                        break;
                    }
                    info = null;
                }
                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(bufferedReader!=null){
                try {
                    bufferedReader.close();
                    streamReader.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(pw!=null){
                pw.close();
            }
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("client exit");
        }
    }
}
