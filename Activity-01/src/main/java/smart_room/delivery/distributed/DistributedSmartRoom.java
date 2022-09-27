package smart_room.delivery.distributed;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import mqtt_tests.MQTTVertxAgent;

public class DistributedSmartRoom extends AbstractVerticle {

    public static void main(String[] args) throws Exception {
        Vertx vertx = Vertx.vertx();
        RoomPlanner agent = new RoomPlanner();
        vertx.deployVerticle(agent);
    }

}
