package com.jevin.jmart.helpers;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.jevin.jmart.models.CartProduct;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

public class MyApplication extends Application {

    private static Context context;
    private Channel channelCart;
    private Pusher pusher;

    private static ICartListener iCartListener;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();

        init();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        pusher.disconnect();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    private void init() {
        setupPusher();
        initCartListeners();
    }

    private void setupPusher() {

        String CHANNEL_USER_CART = "cart" + SharedPreferencesManager.getCartId(context);

        pusher = PusherClient.getPusher();
        channelCart = pusher.subscribe(CHANNEL_USER_CART);
    }

    private void initCartListeners() {

        SubscriptionEventListener itemAddedListener = new SubscriptionEventListener() {
            @Override
            public void onEvent(String channel, String event, final String data) {
                new Handler(Looper.getMainLooper()).post(() -> {
//                        System.out.println("DATA ====>>" + data);
                    Gson gson = new Gson();
                    CartProduct cartProduct = gson.fromJson(data, CartProduct.class);
                    iCartListener.itemAdded(cartProduct);
                });
            }
        };

        SubscriptionEventListener itemUpdatedListener = new SubscriptionEventListener() {
            @Override
            public void onEvent(String channel, String event, final String data) {
                new Handler(Looper.getMainLooper()).post(() -> {
//                        System.out.println("DATA ====>>" + data);
                    Gson gson = new Gson();
                    CartProduct cartProduct = gson.fromJson(data, CartProduct.class);
                    iCartListener.itemUpdated(cartProduct);
                });
            }
        };

        channelCart.bind("itemAdded", itemAddedListener);
        channelCart.bind("itemUpdated", itemUpdatedListener);
//        channelCart.bind("itemRemoved", eventListener);
    }


    public static void setiCartListener(ICartListener cartListener) {
        iCartListener = cartListener;
    }
}
