package bowling.test;

import bowling.src.Frame;
import bowling.src.Game;
import bowling.src.Roll;
import bowling.src.TenthFrame;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class BowlingGameTest {

    @Test
    public void AGameShouldHave10Frames() {
        Game game = new Game();

        Frame[] frames = game.getFrames();

        assertEquals(10, frames.length);
    }

    @Test
    public void everyFrameOfAGameShouldHave2Rolls() {
        Game game = new Game();

        Frame[] frames = game.getFrames();

        Arrays.stream(frames).forEach(frame -> assertEquals(2, frame.getRolls().length));
    }

    @Test
    public void aTenthFrameShouldBeAExtensionOfFrame() {
        assertThat(new TenthFrame(), instanceOf(Frame.class));
    }

    @Test
    public void aTenthFrameShouldHaveBonusRoll() {
        TenthFrame tenthFrame = new TenthFrame();

        assertThat(tenthFrame.getBonusRoll(), instanceOf(Roll.class));
    }

    @Test
    public void justThe10thFrameOfAGameShouldBeATenthFrame() {
        Game game = new Game();

        Frame[] frames = game.getFrames();

        for (int i = 0; i < frames.length - 1; i++) {
            assertFalse(frames[i] instanceof TenthFrame);
        }
        assertTrue(frames[9] instanceof TenthFrame);
    }

    @Test
    public void everyTimeABallIsRolledItShouldBeStoredInTheNextRollAvailableIfThereAreNoStrikesOrSpares() {
        Game game = new Game();
        List<Integer> knockedPins = generatePinsKnockedArrayButNoStrikesOrSpares();

        knockedPins.forEach(game::roll);

        for (int i = 0; i < game.getFrames().length; i++) {
            for (int j = 0; j < game.getFrames()[i].getRolls().length; j++) {
                assertEquals((int) knockedPins.get(i * 2 + j), (int) game.getFrames()[i].getRolls()[j].score());
            }
        }
    }

    @Test
    public void theSumOfTheScoresOfTheRollsOfAFrameShouldNotBeMoreThan10() {
        for (int pinsKockedDownFirstRoll = 1; pinsKockedDownFirstRoll < 10; pinsKockedDownFirstRoll++) {
            for (int pinsKockedDownSecondRoll = 10; pinsKockedDownSecondRoll + pinsKockedDownFirstRoll > 10; pinsKockedDownSecondRoll--) {
                Game game = new Game();
                game.roll(pinsKockedDownFirstRoll);
                int finalPinsKockedDownSecondRoll = pinsKockedDownSecondRoll;

                assertThrows(IllegalArgumentException.class, () -> game.roll(finalPinsKockedDownSecondRoll));
            }
        }
    }

    @Test
    public void ifAFrameIsAStrikeAndItsNotA10thFrameTheNextRollShouldGoToNextFrame() {
        Game game = new Game();

        game.roll(10);
        game.roll(10);

        assertNull(game.getFrames()[0].getRolls()[1].score());
        assertEquals(10, game.getFrames()[1].getRolls()[0].score());
    }

    @Test
    public void ifAFrameIsAStrikeAndItsA10thFrameItShouldBeASecondRollOnThatFrame() {
        List<Integer> knockedPins = generatePinsKnockedArrayButNoStrikesOrSpares();
        knockedPins.set(18, 10);
        knockedPins.set(19, 10);

        Game game = new Game();
        knockedPins.forEach(game::roll);

        assertEquals(10, game.getFrames()[9].getRolls()[0].score());
        assertEquals(10, game.getFrames()[9].getRolls()[1].score());
    }

    @Test
    public void shouldHaveBonusRollIfThe10thFrameIsAStrike() {
        List<Integer> knockedPins = generatePinsKnockedArrayButNoStrikesOrSpares();
        knockedPins.set(18, 10);
        knockedPins.set(19, 10);
        knockedPins.add(10);
        Game game = new Game();

        knockedPins.forEach(game::roll);

        assertEquals(knockedPins.get(20), ((TenthFrame) game.getFrames()[9]).getBonusRoll().score());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfThereIsNoSpaceForARoll() {
        List<Integer> knockedPins = generatePinsKnockedArrayButNoStrikesOrSpares();
        knockedPins.add(5);

        Game game = new Game();

        assertThrows(IllegalArgumentException.class, () -> knockedPins.forEach(game::roll));
    }

    @Test
    public void shouldHaveBonusRollIfThe10thFrameIsASpare() {
        List<Integer> knockedPins = generatePinsKnockedArrayButNoStrikesOrSpares();
        knockedPins.set(18, 9);
        knockedPins.set(19, 1);
        knockedPins.add(10);
        Game game = new Game();

        knockedPins.forEach(game::roll);

        assertEquals(knockedPins.get(20), ((TenthFrame) game.getFrames()[9]).getBonusRoll().score());
    }

    @Test
    public void theScoreOfAGameWithouStrikesOrSparesShouldBeTheSumOfAllTheFrames() {
        List<Integer> knockedPins = generatePinsKnockedArrayButNoStrikesOrSpares();
        Game game = new Game();

        knockedPins.forEach(game::roll);

        assertEquals(knockedPins.stream().reduce(Integer::sum).get(), game.score());
    }

    @Test
    public void theScoreOfAGameWithBonusRollShouldBeTheSumOfAllTheFramesPlusTheBonus() {
        List<Integer> knockedPins = generatePinsKnockedArrayButNoStrikesOrSpares();
        knockedPins.set(18, 9);
        knockedPins.set(19, 1);
        knockedPins.add(10);
        Game game = new Game();

        knockedPins.forEach(game::roll);

        assertEquals(knockedPins.stream().reduce(Integer::sum).get(), game.score());
    }

    @Test
    public void theScoreOfAFrameWithSpareShouldBe10PlusNextRoll() {
        List<Integer> knockedPins = generatePinsKnockedArrayButNoStrikesOrSpares();
        knockedPins.set(0, 9);
        knockedPins.set(1, 1);
        Game game = new Game();
        knockedPins.forEach(game::roll);
        assertEquals(knockedPins.stream().reduce(Integer::sum).get() + knockedPins.get(2), game.score());
    }

    @Test
    public void theScoreOfAFrameWithStrikeShouldBe10PlusNext2Rolls() {
        List<Integer> knockedPins = generatePinsKnockedArrayButNoStrikesOrSpares();
        knockedPins.set(0, 10);
        knockedPins.remove(1);
        Game game = new Game();
        knockedPins.forEach(game::roll);
        assertEquals(knockedPins.stream().reduce(Integer::sum).get() + knockedPins.get(1) + knockedPins.get(2), game.score());
    }

    @Test
    public void theScoreOfAGameWith12StrikesIs300() {
        Game game = new Game();
        for (int i = 0; i < 12; i++) {
            game.roll(10);
        }
        assertEquals(300, game.score());
    }

    private List<Integer> generatePinsKnockedArrayButNoStrikesOrSpares() {
        List<Integer> pinsKnocked = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            pinsKnocked.add(i);
            pinsKnocked.add(9 - i);
        }
        return pinsKnocked;
    }
}
