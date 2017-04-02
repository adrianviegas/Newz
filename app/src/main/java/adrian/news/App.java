package adrian.news;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.TypedValue;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Adrian on 06/08/2016.
 */
public class App extends Application {
    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;
    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Thread thread = new Thread() {
            @Override
            public void run() {
                prefs = PreferenceManager.getDefaultSharedPreferences(App.this);
                editor = prefs.edit();
            }
        };

        thread.start();


        context = App.this;
    }

    public static int getDP(int pixels) {
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                pixels,
                r.getDisplayMetrics()
        );
        return px;
    }

    private Tracker mTracker;

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.setDryRun(true);
            mTracker = analytics.newTracker("XXXX");
            mTracker.enableExceptionReporting(true);
            mTracker.enableAdvertisingIdCollection(true);
            mTracker.enableAutoActivityTracking(true);
        }
        return mTracker;
    }
}
