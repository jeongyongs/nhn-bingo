package com.nhnacademy.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 서버의 명령을 처리하는 클래스입니다.
 */
@SuppressWarnings("squid:S106")
public class CommandHandler {

    private static final String COMMAND = "command";
    private BufferedReader reader;
    private BufferedWriter writer;
    private BufferedReader scanner;

    private CommandHandler(BufferedReader reader, BufferedWriter writer) {
        this.reader = reader;
        this.writer = writer;
        scanner = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * 명령 핸들러를 설정합니다.
     * 
     * @param reader 명령을 받을 버퍼 리더입니다.
     * @param writer 응답을 보낼 버퍼 라이터입니다.
     * @return 설정된 명령 핸들러 객체를 반환합니다.
     */
    public static CommandHandler of(BufferedReader reader, BufferedWriter writer) {
        return new CommandHandler(reader, writer);
    }

    /**
     * 서버의 명령을 처리합니다.
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
            scanner.close();
        } catch (IOException ignore) {
            // ignore
        }
    }

    private void process() throws IOException {
        while (!Thread.interrupted()) {
            execute(new JSONObject(reader.readLine()));
        }
    }

    private void execute(JSONObject command) throws IOException {
        if (command.getString(COMMAND).equals("start")) {
            clear();

            System.out.println("빙고판을 입력해주세요.");
            System.out.println("예) 1 7 2 3 6...");
            System.out.println("예) random");
            System.out.print("> ");
            String readLine = scanner.readLine();

            String[] board = readLine.split(" ");
            JSONObject data = new JSONObject();
            if (board.length == 25) {
                data.put("random", false);
                data.put("board", new JSONArray(board));
                response(data);
                return;
            }
            data.put("random", true);
            response(data);
            return;
        }
        if (command.getString(COMMAND).equals("repaint")) {
            clear();
            System.out.println(command.getString("board"));
            return;
        }
        if (command.getString(COMMAND).equals("select")) {
            System.out.println("선택할 번호를 입력해주세요.");
            System.out.print("> ");
            response(new JSONObject().put("selected", Integer.parseInt(scanner.readLine())));
            return;
        }
        if (command.getString(COMMAND).equals("win")) {
            System.out.println("게임에서 이겼습니다.");
            return;
        }
        if (command.getString(COMMAND).equals("lose")) {
            System.out.println("게임에서 졌습니다.");
        }
    }

    private void response(JSONObject data) throws IOException {
        writer.write(data.toString());
        writer.newLine();
        writer.flush();
    }

    private void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main(String[] args) {
        Client.of("localhost", 12345).run();
    }
}
