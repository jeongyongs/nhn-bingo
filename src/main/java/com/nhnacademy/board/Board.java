package com.nhnacademy.board;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;

public class Board {
    private int size = 5;
    private int[] board = new int[size * size];
    private int[] temp = new int[board.length];
    private char[] bingo = new char[] { 'B', 'I', 'N', 'G', 'O' };
    private StringBuilder result;
    private String message = " 빙고판을 생성합니다.\n";

    private Board(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            board[i] = jsonArray.getInt(i);
        }
        paint();
    }

    private Board(String string) {
        List<Integer> list = IntStream.range(1, 26).boxed().collect(Collectors.toList());
        Collections.shuffle(list);
        board = list.stream().mapToInt(i -> i).toArray();
        paint();
    }

    public static Board from(JSONArray jsonArray) {
        return new Board(jsonArray);
    }

    public static Board from(String random) {
        return new Board(random);
    }

    public String getMessage() {
        return message;
    }

    public void paint() {
        result = new StringBuilder();
        StringJoiner boardJoiner = new StringJoiner("\n");

        int count = 0;
        int j = 0;
        for (int i = 0; i < size * size; i++) {
            if (temp[i] == -1) {
                result.append(String.format("[%02d]", board[i]));
                count++;
            } else if (temp[i] == -2) {
                result.append(" XX ");
                count++;
            } else if (temp[i] == -3) {
                result.append(String.format(" %2s ", bingo[j % 5]));
                j++;
                count++;
            } else {
                result.append(String.format(" %02d ", board[i]));
                count++;
            }
            if (count != 0 && count % 5 == 0) {
                boardJoiner.add(result.toString());
                result = new StringBuilder();
            }
        }

        result = new StringBuilder(boardJoiner.toString());
    }

    private void isDuplicateNum(int location) {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == location) {
                if (temp[i] == -1 || temp[i] == -2) {
                    throw new DuplicateNumException();
                }
            }
        }
    }

    public void mark(int location, int mark) {
        try {
            isDuplicateNum(location);
            for (int i = 0; i < size * size; i++) {
                if (board[i] == location) {
                    temp[i] = mark;
                }
            }
            // paint();
            if (mark == -1) {
                message = " host가 " + location + "을 선택하였습니다.\n";
            }
            if (mark == -2) {
                message = " player가 " + location + "을 선택하였습니다.\n";
            }
            isBingo(mark);
        } catch (DuplicateNumException e) {
            location = (int) (Math.random() * (board.length - 1));
            mark(location, mark);
        }

    }

    // 빙고 확인
    public boolean isBingo(int mark) {
        if (checkRow(mark) || checkCol(mark) || checkDiagonal(mark)) {
            if (mark == -1) {
                message = message + "\n ┏━━━━━━━━━━━━━━━━━━┓\n" + " ┃    host WINS!    ┃\n" + " ┗━━━━━━━━━━━━━━━━━━┛\n";
            }
            if (mark == -2) {
                message = message + "\n ┏━━━━━━━━━━━━━━━━━━┓\n" + " ┃   player WINS!   ┃\n" + " ┗━━━━━━━━━━━━━━━━━━┛\n";
            }
            paint();
            return true;
        }
        paint();
        return false;
    }

    private boolean checkRow(int mark) {
        // 가로줄 체크
        for (int i = 0; i < size; i++) {
            boolean bool = true;
            for (int j = 0; j < size; j++) {
                int location = j + 5 * i;
                if (temp[location] != mark) {
                    bool = false;
                    break;
                }
            }
            if (bool) {
                for (int j = 0; j < size; j++) {
                    int location = j + 5 * i;
                    temp[location] = -3;
                }
                return bool;
            }
        }
        return false;
    }

    private boolean checkCol(int mark) {
        // 세로줄 체크
        for (int i = 0; i < size; i++) {
            boolean bool = true;
            for (int j = 0; j < size; j++) {
                int location = i + 5 * j;
                if (temp[location] != mark) {
                    bool = false;
                    break;
                }
            }
            if (bool) {
                for (int j = 0; j < size; j++) {
                    int location = i + 5 * j;
                    temp[location] = -3;
                    paint();
                }
                return bool;
            }
        }
        return false;
    }

    private boolean  checkDiagonal(int mark) {
        return checkDiagonal1(mark) || checkDiagonal2(mark);
    }

    private boolean checkDiagonal1(int mark) {
        // 대각선 체크
        boolean bool = true;
        for (int i = 0; i < size * size; i += 6) {
            if (temp[i] != mark) {
                bool = false;
                break;
            }
        }
        if (bool) {
            for (int j = 0; j < size * size; j += 6) {
                temp[j] = -3;
            }
            return bool;
        }

        return false;
    }

private boolean checkDiagonal2(int mark) {
        // 대각선 체크
        boolean bool = true;
        for (int i = 4; i < size * size -1; i += 4) {
            if (temp[i] != mark) {
                bool = false;
                break;
            }
        }
        if (bool) {
            for (int j = 4; j < size * size-1; j += 4) {
                temp[j] = -3;
            }
            return bool;
        }

        return false;
    }

    @Override
    public String toString() {
        return result.toString() + "\n" + message;
    }

    // public static void main(String[] args) {
    //     Board b = Board.from("random");
    //     b.board = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
    //             25 };
    //     b.paint();
    //     System.out.println(b.toString());
    //     b.mark(5, -1);
    //     System.out.println(b.toString());
    //     b.mark(9, -1);
    //     System.out.println(b.toString());
    //     b.mark(13, -1);
    //     System.out.println(b.toString());
    //     b.mark(17, -1);
    //     System.out.println(b.toString());
    //     b.mark(21, -1);
    //     System.out.println(b.toString());
    // }
}
