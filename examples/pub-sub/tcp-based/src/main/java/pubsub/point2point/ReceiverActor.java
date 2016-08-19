package pubsub.point2point;

import org.nustaq.kontraktor.*;
import org.nustaq.kontraktor.remoting.websockets.WebSocketConnectable;
import org.nustaq.kontraktor.util.Log;

/**
 * Created by moelrue on 28.09.2015.
 *
 * subscribes and sends broadcasts cyclically on topic "defaultTopic"
 *
 */
public class ReceiverActor extends Actor<ReceiverActor> {

    String myName = "Receiver_"+((int)(Math.random()*100_000)); // is unsafe, demo only

    MediatorActor mediator;

    public void init() {
        Log.sInfo(this, "my name is " + myName);
        mediator = (MediatorActor) new WebSocketConnectable(MediatorActor.class,"ws://localhost:9090/")
            .connect( null, acc -> {
                Log.sInfo(this, "lost connection, stopping");
                self().stop();
                System.exit(0);
            }).await();
        mediator.subscribe( "defaultTopic", self() );
        pingLoop();
    }

    public void pingLoop() {
        if ( ! isStopped() ) {
            mediator.tellSubscribers( self(), "defaultTopic", "Hello from "+myName );
            delayed(5000, () -> pingLoop() );
        }
    }

    public IPromise receiveAsk(String topic, Object message) {
        return new Promise("reply from "+myName+" to "+topic+" "+message);
    }

    public void receiveTell(String topic, Object message) {
        System.out.println("have been told on topic:"+topic+" the message:"+message);
    }

    public static void main(String[] args) {
        ReceiverActor receiver = Actors.asActor(ReceiverActor.class);
        receiver.init();
    }

}
