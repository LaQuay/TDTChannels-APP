package laquay.com.canalestdt.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Utils {
    public static void launchIntent(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
