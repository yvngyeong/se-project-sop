public class Piece {
    private int position;
    private int color;
    private int ownerId;
    private boolean isFinished;
    private int groupId;
    private boolean hasMoved;      // 한 번이라도 움직였는지
}

public Piece(int ownerId, int color, int groupId) {
    this.position = 0;
    this.color = color;
    this.ownerId = ownerId;
    this.groupId = groupId;
    this.isFinished = false;
    this.hasMoved = false;
}

public int getPosition() {
    return position;
}

public void setPosition(int newPosition) {
    // 움직였다는 표시
    if (newPosition != this.position) {
        hasMoved = true;
    }

    this.position = newPosition;

    // 도착 조건: 움직인 적 있고, 다시 0으로 돌아온 경우
    if (hasMoved && newPosition == 0) {
        this.isFinished = true;
    }
}

public boolean isFinished() {
    return isFinished;
}

public void finish() {
    this.isFinished = true;
}

public int getColor() {
    return color;
}

public int getOwnerId() {
    return ownerId;
}

public int getGroupId() {
    return groupId;
}

public boolean hasMoved() {
    return hasMoved;
}
