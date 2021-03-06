package com.tunjid.rcswitchcontrol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.pio.PeripheralManager;
import com.tunjid.rcswitchcontrol.broadcasts.Broadcaster;
import com.tunjid.rcswitchcontrol.services.ClientNsdService;

import java.util.List;

/**
 * App Singleton.
 * <p>
 * Created by tj.dahunsi on 3/25/17.
 */

public class App extends android.app.Application {

    private static App instance;

    private WifiStatusReceiver receiver;

    @Override @SuppressLint("CheckResult")
    public void onCreate() {
        super.onCreate();
        instance = this;

        //noinspection ResultOfMethodCallIgnored
        Broadcaster.listen(ClientNsdService.ACTION_START_NSD_DISCOVERY)
                .subscribe(intent -> { if (receiver != null) receiver.onReceive(this, intent); },
                        Throwable::printStackTrace);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

            @Override public void onActivityStarted(Activity activity) {
                // Necessary because of background restrictions, services may only be started in
                // the foreground
                if (receiver != null) return;

                IntentFilter filter = new IntentFilter();
                filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                filter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);

                registerReceiver(receiver = new WifiStatusReceiver(), filter);
            }

            @Override public void onActivityResumed(Activity activity) {}

            @Override public void onActivityPaused(Activity activity) {}

            @Override public void onActivityStopped(Activity activity) {}

            @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

            @Override public void onActivityDestroyed(Activity activity) {}
        });
    }

    public static App getInstance() {
        return instance;
    }

    public static boolean isServiceRunning(Class<? extends Service> service) {
        final ActivityManager activityManager = (ActivityManager) instance.getSystemService(Context.ACTIVITY_SERVICE);
        final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (RunningServiceInfo info : services) {
            if (info.service.getClassName().equals(service.getName())) return true;
        }
        return false;
    }

    public static boolean isAndroidThings() {
        try { PeripheralManager.getInstance(); }
        catch (NoClassDefFoundError e) { return false; } // Thrown on non Android things devices
        return true;
    }

    public static void catcher(String tag, String log, Catchable runnable) {
        try { runnable.run(); }
        catch (Exception e) { Log.e(tag, log, e); }
    }

    @FunctionalInterface
    public interface Catchable {
        void run() throws Exception;
    }
}
