import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Player {
    private int id;
    private List<Piece> pieces;
    private List<Integer> yutResult;
    private boolean hasWon;

    public Player(int id, int pieceCount){
        this.id = id;
        this.pieces = new ArrayList<>();
        this.yutResult = new ArrayList<>();
        this.hasWon = false;
        
        int color = (id - 1) % 4 + 1;  // 플레이어 id에 따라 색상 1~4를 자동 배정

        for (int i = 0; i < pieceCount; i++) {
            pieces.add(new Piece(id, color, i));  // 정해진 color로 Piece 생성
        }

    }
    
    public void throwYut(Yut yut) {
        int result;
        System.out.println("플레이어 " + id + "번이 윷을 던집니다!");

        do {
            result = yut.getResult();
            yutResult.add(result);

            // 결과를 문자열로 변환해서 출력
            String resultStr;
            switch (result) {
                case -1: resultStr = "빽도"; break;
                case 1: resultStr = "도"; break;
                case 2: resultStr = "개"; break;
                case 3: resultStr = "걸"; break;
                case 4: resultStr = "윷 (한 번 더!)"; break;
                case 5: resultStr = "모 (한 번 더!)"; break;
                default: resultStr = "알 수 없음"; break;
            }

            System.out.println("→ 결과: " + resultStr);

        } while (result == 4 || result == 5); // 윷이나 모면 반복
    }


    public Piece selectPiece() {
        Scanner scanner = new Scanner(System.in);

        // 움직일 수 있는 말 목록 보여주기
        System.out.println("현재 이동 가능한 말 목록:");
        for (int i = 0; i < pieces.size(); i++) {
            Piece p = pieces.get(i);
            if (!p.isFinished()) {
                System.out.println("[" + i + "] 위치: " + p.getPosition() + " (도착 안함)");
            }
        }

        int index = -1;
        while (true) {
            System.out.print("이동시킬 말의 번호를 입력하세요: ");
            try {
                index = Integer.parseInt(scanner.nextLine());
                if (index >= 0 && index < pieces.size() && !pieces.get(index).isFinished()) {
                    break;
                } else {
                    System.out.println("잘못된 선택입니다. 다시 입력해주세요.");
                }
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }

        return pieces.get(index);
    }


    public boolean checkWin() {
        if (hasWon) {
            return true; // 이미 승리 상태면 확인 생략
        }

        if (pieces == null || pieces.isEmpty()) {
            return false; // 말이 없으면 승리 불가능
        }

        for (Piece p : pieces) {
            if (!p.isFinished()) {
                return false; // 하나라도 도착 안 했으면 아직 승리 아님
            }
        }

        hasWon = true;
        System.out.println("🎉 플레이어 " + id + "번이 모든 말을 도착시켰습니다! 승리! 🎉");
        return true;
    }

    
    public int getId() {
        return id;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public boolean hasWon() {
        return hasWon;
    }
}
