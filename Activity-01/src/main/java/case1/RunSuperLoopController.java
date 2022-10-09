package case1;

import smart_room.Event;
import smart_room.centralized.SinglelBoardSimulator;

public class RunSuperLoopController {

	public static void main(String[] args) throws Exception {

		SinglelBoardSimulator board = new SinglelBoardSimulator();
		board.init();
	
		board.register((Event ev) -> {
			System.out.println("New event: " + ev);
		});
		
		SmartRoomSuperLoopBasedAgent agent = new SmartRoomSuperLoopBasedAgent("my-controller", board, 0.2);
		agent.start();
	}

}
