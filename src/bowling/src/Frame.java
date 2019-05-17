package bowling.src;

import java.util.Arrays;
import java.util.Optional;

public class Frame {

    protected Roll[] rolls;

    public Frame() {
        rolls = Arrays.stream(new Roll[2]).map(x -> new Roll()).toArray(Roll[]::new);
    }

    public Roll[] getRolls() {
        return rolls;
    }

    void roll(int pins) {
        if (rolls[0].score() != null) {
            if (isAIllegalRoll(pins)) throw new IllegalArgumentException();
            rolls[1].roll(pins);
        } else {
            rolls[0].roll(pins);
        }
    }

    boolean isAIllegalRoll(int pins) {
        return rolls[0].score() + pins > 10;
    }

    boolean thereIsRollMissing() {
        if (rolls[0].score() == null) return true;
        return rolls[1].score() == null && rolls[0].score() != 10;
    }

    int score(Roll nextRoll, Roll twoNextRoll) {
        if (rolls[0].score() == 10) {
            return 10 + nextRoll.score() + twoNextRoll.score();
        }
        int score = rolls[0].score() + getRollScoreInASafeWay(rolls[1]);
        return score == 10 ? score + nextRoll.score() : score;
    }

    protected Integer getRollScoreInASafeWay(Roll roll) {
        return Optional.ofNullable(roll.score()).orElse(0);
    }
}
