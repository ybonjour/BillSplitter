package ch.pantas.billsplitter.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.inject.Inject;

import ch.pantas.billsplitter.services.MigrationService;
import roboguice.receiver.RoboBroadcastReceiver;

public class UpdateReceiver extends RoboBroadcastReceiver {

    private final static String SPLITTY_PACKAGE = "ch.pantas.splitty";

    @Inject
    private MigrationService migrationService;

    @Override
    public void handleReceive(Context context, Intent intent) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(SPLITTY_PACKAGE, 0);
            migrationService.migrateToVersion(pInfo.versionCode, context);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
