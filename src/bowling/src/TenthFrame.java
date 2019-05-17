package bowling.src;

public class TenthFrame extends Frame {

    Roll bonusRoll;

    public TenthFrame() {
        bonusRoll = new Roll();
    }

    public Roll getBonusRoll() {
        return bonusRoll;
    }

    @Override
    boolean thereIsRollMissing() {
        if (couldHaveABonusRoll()) {
            return bonusRoll.score() == null;
        }
        if (getRollScoreInASafeWay(rolls[0]) == 10) return true;
        return super.thereIsRollMissing();
    }

    @Override
    boolean isAIllegalRoll(int pins) {
        if (couldHaveABonusRoll()) {
            return bonusRoll.score() != null;
        }
        return super.isAIllegalRoll(pins);
    }

    @Override
    void roll(int pins) {
        if (getRollScoreInASafeWay(rolls[0]) == 10 && rolls[1].score() == null) {
            rolls[1].roll(pins);
        } else if (couldHaveABonusRoll()) {
            bonusRoll.roll(pins);
        } else {
            super.roll(pins);
        }
    }

    private boolean couldHaveABonusRoll() {
        return frameIsASpare() || secondRollIsAStrike();
    }

    private boolean secondRollIsAStrike() {
        return getRollScoreInASafeWay(rolls[1]) == 10;
    }

    private boolean frameIsASpare() {
        return getRollScoreInASafeWay(rolls[0]) + getRollScoreInASafeWay(rolls[1]) == 10;
    }

    public int score() {
        return rolls[0].score() + getRollScoreInASafeWay(rolls[1]) + getRollScoreInASafeWay(bonusRoll);
    }
}
