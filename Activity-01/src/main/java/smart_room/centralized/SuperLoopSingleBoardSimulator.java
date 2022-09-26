package smart_room.centralized;

import smart_room.Event;

public class SuperLoopSingleBoardSimulator {
    private static final double T_VALUE = 0.3;

    public static void main(String[] args) {
        SinglelBoardSimulator board = new SinglelBoardSimulator();
        board.init();

        // Event-loop

        /* board.register((Event ev) -> {
            if (ev instanceof PresenceDetected) {
                if (board.getLuminosity() < T_VALUE) {
                    board.on();
                }
            } else if (ev instanceof PresenceNoMoreDetected) {
                board.off();
            }
        }); */


        // Super-loop

        new Thread(() -> {
            while (true) {
                if (!board.presenceDetected())
                    board.off();
                else
                    if (board.getLuminosity() < T_VALUE)
                        board.on();
            }
        }).start();
    }

}
