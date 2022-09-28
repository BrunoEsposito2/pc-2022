package smart_room.centralized;

import smart_room.Event;

public class CentralizedSingleBoardSimulator {
    private static final double T_VALUE = 0.3;
    static boolean status = false;

    public static void main(String[] args) {
        SinglelBoardSimulator board = new SinglelBoardSimulator();
        board.init();

        // Super-loop

        /*new Thread(() -> {
            while (true) {
                if (!board.presenceDetected())
                    board.off();
                else
                    if (board.getLuminosity() < T_VALUE)
                        board.on();
                    else
                        board.off();
            }
        }).start();*/

        // Event-loop
        board.register((Event ev) -> {
            if (ev instanceof PresenceDetected && board.getLuminosity() < T_VALUE) {
                status = true;
                board.on();
            } else if (ev instanceof PresenceDetected && board.getLuminosity() >= T_VALUE) {
                status = true;
                board.off();
            } else if (ev instanceof PresenceNoMoreDetected) {
                status = false;
                board.off();
            } else{
                if(status && board.getLuminosity() < T_VALUE){
                    board.on();
                }else {
                    board.off();
                }
            }
        });
    }

}
