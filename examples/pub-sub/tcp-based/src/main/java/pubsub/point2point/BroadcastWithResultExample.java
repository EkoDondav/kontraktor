package pubsub.point2point;

import org.nustaq.kontraktor.Actors;
import org.nustaq.kontraktor.util.Log;

/**
 * Created by ruedi on 28/09/15.
 *
 * does a broadcast and receives one result per receiver
 *
 */
public class BroadcastWithResultExample extends ReceiverActor {

    // avoid generics dilemma
    public BroadcastWithResultExample self() {
        return (BroadcastWithResultExample) super.self();
    }

    /**
     * instead of broadcasting fire-and-forget (tell), this one send a request
     * and receives results from each actor.
     */
    public void pingLoop() {
        if ( ! isStopped() ) {
            Log.sInfo(this, "----------- START MULTI RESPONSE REQUEST ------------");
            mediator.askSubscribers(self(), "defaultTopic", "Hello from " + myName,
                (reply, error) -> {
                    if (Actors.isComplete(error)) {
                        Log.sInfo(this, "----------- ALL RESULTS RECEIVED ------------");
                        delayed(5000, () -> pingLoop());
                    } else {
                        Log.sInfo(this, "=>" + reply);
                    }
                });
        }
    }

    public void receiveTell(String topic, Object message) {
        // just ignore other broadcasts for cleaner output
    }

    public static void main(String[] args) {
        BroadcastWithResultExample receiver = Actors.asActor(BroadcastWithResultExample.class);
        receiver.init();
    }

}
