package com.song.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChartServer {

    private Integer port;

    private ServerSocket serverSocket;

    private ExecutorService executorService;

    public ChartServer(Integer port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        executorService = Executors.newCachedThreadPool();
    }

    public void acceptClient(){
        while (true){
            try {
                final Socket socket = serverSocket.accept();
                executorService.submit(new ChartTask(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            ChartServer chartServer = new ChartServer(9999);
            chartServer.acceptClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
