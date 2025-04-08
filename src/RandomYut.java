import java.util.Random;

public class RandomYut extends Yut{
    private Random random = new Random();

    @Override
    public Integer getResult() {
        int rand = random.nextInt(100); // 0~99까지 랜덤

        if (rand < 5) {
            return -1; // 빽도 5%
        } else if (rand < 30) {
            return 1; // 도 25%
        } else if (rand < 55) {
            return 2; // 개 25%
        } else if (rand < 80) {
            return 3; // 걸 25%
        } else if (rand < 90) {
            return 4; // 윷 10%
        } else {
            return 5; // 모 10%
        }
    }
}
