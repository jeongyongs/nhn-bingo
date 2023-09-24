package com.nhnacademy.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONArray;
import org.json.JSONObject;
import com.nhnacademy.board.Board;

/**
 * 빙고 게임을 진행하는 클래스입니다.
 */
@SuppressWarnings("squid:S106")
public class BingoGame {

    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final BufferedReader scanner;

    private BingoGame(BufferedReader reader, BufferedWriter writer) {
        this.reader = reader;
        this.writer = writer;
        scanner = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * 빙고 게임을 설정합니다.
     * 
     * @param reader 데이터를 받을 클라이언트의 reader입니다.
     * @param writer 데이터를 보낼 클라이언트의 writer입니다.
     * @return 설정된 빙고 게임 객체를 반환합니다.
     */
    public static BingoGame of(BufferedReader reader, BufferedWriter writer) {
        return new BingoGame(reader, writer);
    }

    /**
     * 빙고 게임을 시작합니다.
     * 
     * @throws IOException
     */
    public void play() throws IOException {
        // 게임 시작을 알립니다.
        clear();
        send("command:start");

        // 내 빙고판을 만듭니다.
        String[] board = receiveInput("빙고판을 입력해주세요.\n예) 1 7 2 3 6...\n예) random").split(" ");
        Board myBoard = Board.from("random");
        if (board.length == 25) {
            myBoard = Board.from(new JSONArray(board));
        }
        clearAndPrint(myBoard.toString());

        // 클라이언트의 빙고판을 받습니다.
        Board clientBoard = Board.from("random");
        JSONObject clientBoardResponse = new JSONObject(reader.readLine());
        if (!clientBoardResponse.getBoolean("random")) {
            clientBoard = Board.from(clientBoardResponse.getJSONArray("board"));
        }
        send("command:repaint", "board:" + clientBoard.toString());

        while (!Thread.interrupted()) {
            // 번호를 선택합니다.
            int selectedNumber = Integer.parseInt(receiveInput("선택할 번호를 입력해주세요."));
            markBoard(myBoard, clientBoard, selectedNumber, -1);

            // 평가합니다.
            if (myBoard.isBingo(-3)) {
                send("command:repaint", "board:" + myBoard.toString());
                send("command:lose");
                break;
            }

            // 클라이언트가 번호를 선택합니다.
            send("command:select");
            int clientSelectedNumber = new JSONObject(reader.readLine()).getInt("selected");
            markBoard(myBoard, clientBoard, clientSelectedNumber, -2);

            // 평가합니다.
            if (clientBoard.isBingo(-3)) {
                clearAndPrint(clientBoard.toString());
                send("command:win");
                break;
            }
        }
    }

    private void markBoard(Board myBoard, Board clientBoard, int selectedNumber, int mark)
            throws IOException {
        myBoard.mark(selectedNumber, mark);
        clientBoard.mark(selectedNumber, mark);
        clearAndPrint(myBoard.toString());
        send("command:repaint", "board:" + clientBoard.toString());
    }

    private void clearAndPrint(String message) {
        clear();
        System.out.println(message);
    }

    private String receiveInput(String message) throws IOException {
        System.out.println(message);
        System.out.print("> ");
        return scanner.readLine();
    }

    private void send(String... datas) throws IOException {
        JSONObject jsonData = new JSONObject();

        for (String data : datas) {
            String[] splittedData = data.split(":");
            jsonData.put(splittedData[0], splittedData[1]);
        }

        writer.write(jsonData.toString());
        writer.newLine();
        writer.flush();
    }

    private void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
