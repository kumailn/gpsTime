package com.kumailn.prayertime;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class dynamicAlarmService extends Service {
    public dynamicAlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        return;
    }
}
