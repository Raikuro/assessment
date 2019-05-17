package bowling.src;

import java.util.Optional;

public class Roll {

    private Integer score;

    public Integer score() {
        return score;
    }

    public Optional<Integer> score2() {
        return Optional.ofNullable(score);
    }

    void roll(int pins) {
        score = pins;
    }
}
