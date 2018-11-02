package com.song.client;

import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.util.concurrent.TimeUnit;

public class ChartClient {

    private String serverIp;
    private Integer serverPort;

    private final Socket socket;

    private volatile Boolean down = false;

    public ChartClient(String serverIp, Integer serverPort) throws IOException {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        socket = new Socket(serverIp, serverPort);
    }

    public void clientActive() throws IOException {
        new Thread(()->{
            InputStream inputStream = null;
            InputStreamReader streamReader = null;
            BufferedReader bufferedReader = null;
            try {
                inputStream = socket.getInputStream();
                streamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(streamReader);
                String info = null;
                while (true && !down) {
                    if ((info = bufferedReader.readLine()) != null) {
                        System.out.println("receive:" + info);
                        if(info.contains("bye")){
                            down = true;
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
            }
        }, "reader-thread").start();

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(socket.getOutputStream());
            String send = null;
            int b;
            while (true && !down) {
                if ((b=System.in.read()) != -1) {
                    pw.write((char)b);
                    pw.flush();
                }

                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if(pw!=null){
                pw.close();
            }
        }
        System.out.println("close now");
    }



    public static void main(String[] args) {
        try {
            ChartClient client = new ChartClient("localhost", 9999);
            client.clientActive();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
