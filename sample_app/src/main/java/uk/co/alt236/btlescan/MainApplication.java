package uk.co.alt236.btlescan;

import android.app.Application;

/**
 * Created by xiaopeng on 16/6/16.
 */
public class MainApplication extends Application {
    /** A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher. */
    public static final boolean ENCRYPTED = false;

    @Override
    public void onCreate() {
        super.onCreate();

    }

}
