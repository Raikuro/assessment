package bowling.src;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Game {

    private Frame[] frames;

    private int rollCounter;

    public Game() {
        List<Frame> frames = Arrays.stream(new Frame[9]).map(x -> new Frame()).collect(Collectors.toList());
        frames.add(new TenthFrame());
        this.frames = frames.toArray(new Frame[0]);
    }

    public Frame[] getFrames() {
        return this.frames;
    }

    public void roll(int pins) {
        Arrays.stream(frames)
                .filter(Frame::thereIsRollMissing)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .roll(pins);
    }

    public int score() {
        int result = 0;
        for (int i = 0; i < 9; i++) {
            Roll nextRoll = frames[i + 1].getRolls()[0];
            Roll twoNextRoll = frames[i + 1].getRolls()[1];
            if (twoNextRoll.score() == null) {
                twoNextRoll = frames[i + 2].getRolls()[0];
            }
            result += frames[i].score(nextRoll, twoNextRoll);
        }
        return result + ((TenthFrame) frames[9]).score();
    }
}
