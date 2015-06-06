package org.ligi.passandroid;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import net.danlew.android.joda.JodaTimeAndroid;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.ligi.passandroid.model.AndroidFileSystemPassStore;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.Settings;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;

public class App extends Application {

    private static Bus bus;
    private static Settings settings;
    private static PassStore passStore;
    private static App instance;
    private static BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();

        JodaTimeAndroid.init(this);

        org.ligi.passandroid.Tracker.init(this);
        initTraceDroid();

        instance = this;
        bus = new Bus(ThreadEnforcer.ANY);
        settings = new Settings(this);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        //beaconManager.startMonitoringBeaconsInRegion(new Region());
    }

    private void initTraceDroid() {
        TraceDroid.init(this);
        Log.setTAG("PassAndroid");
    }

    public static Bus getBus() {
        return bus;
    }

    public static Settings getSettings() {
        return settings;
    }

    public static PassStore getPassStore() {
        if (passStore == null) {
            passStore = new AndroidFileSystemPassStore(instance);
        }
        return passStore;
    }

    public static void replacePassStore(PassStore newPassStore) {
        passStore = newPassStore;
    }

    public static String getPassesDir(final Context ctx) {
        return ctx.getFilesDir().getAbsolutePath() + "/passes";
    }

    public static String getShareDir() {
        return Environment.getExternalStorageDirectory() + "/tmp/passbook_share_tmp/";
    }

    public static App getInstance() {
        return instance;
    }

    public static BeaconManager getBeaconManager() {
        return beaconManager;
    }
}
