package laquay.com.canalestdt.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class VideoUtils {
    public static void watchYoutubeVideo(Context context, String url) {
        // try YouTube app mode, if not installed then launch in browser
        try {
            launchIntent(context, Uri.parse("vnd.youtube:" + url.split("/")[3]));
        } catch (ActivityNotFoundException ex) {
            launchIntent(context, Uri.parse("http://www.youtube.com/watch?v=" + url));
        }
    }

    public static void watchUnknownVideo(Context context, String url) {
        launchIntent(context, Uri.parse(url));
    }

    private static void launchIntent(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
