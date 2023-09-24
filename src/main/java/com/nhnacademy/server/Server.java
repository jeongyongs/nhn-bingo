package com.nhnacademy.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import com.nhnacademy.game.BingoGame;

public class Server {

    int port;
    Socket socket;
    BufferedReader reader;
    BufferedWriter writer;

    private Server(int port) {
        this.port = port;
    }

    public static Server from(int port) {
        return new Server(port);
    }

    public void run() {
        try {
            preprocess();
            process();
            postprocess();
        } catch (IOException e) {
        }
    }

    private void preprocess() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    private void process() throws IOException {
        BingoGame.of(reader, writer).play();
    }

    private void postprocess() throws IOException {
        socket.close();
        reader.close();
        writer.close();
    }
}
