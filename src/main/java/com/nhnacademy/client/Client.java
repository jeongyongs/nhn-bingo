package com.nhnacademy.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * 서버 접속을 처리하는 클래스입니다.
 */
public class Client {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private Client(String hostname, int port) {
        try {
            socket = new Socket(hostname, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException ignore) {
            // ignore
        }
    }

    /**
     * 클라이언트를 설정합니다.
     * 
     * @param hostname 접속할 서버의 주소입니다.
     * @param port 접속할 서버의 포트 번호입니다.
     * @return <code>hostname:port</code>로 접속하는 클라이언트 객체를 반환합니다.
     */
    public static Client of(String hostname, int port) {
        return new Client(hostname, port);
    }

    /**
     * 클라이언트로 동작합니다.
     */
    public void run() {
        try {
            process();
        } catch (IOException ignore) {
            // ignore
        }
        postprocess();
    }

    private void postprocess() {
        try {
            writer.close();
            reader.close();
            socket.close();
        } catch (IOException ignore) {
            // ignore
        }
    }

    private void process() throws IOException {
        CommandHandler.of(reader, writer).run();
    }
}
