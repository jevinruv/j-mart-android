package com.jevin.jmart.services;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;

public class PusherClient {

    private static final String APP_KEY = "809276b50cbfba68b5cc";
    private static final String CLUSTER = "ap2";
    private static Pusher pusher = null;


    private PusherClient() {
    }


    public static Pusher getPusher() {

        if (pusher == null) {

            PusherOptions pusherOptions = new PusherOptions();
            pusherOptions.setCluster(CLUSTER);
            pusherOptions.setEncrypted(true);
            pusher = new Pusher(APP_KEY, pusherOptions);

            init();
        }
        return pusher;
    }

    private static void init(){

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                System.out.println("State changed to " + change.getCurrentState() + " from " + change.getPreviousState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                System.out.println("There was a problem connecting!");
            }
        });

    }
}
