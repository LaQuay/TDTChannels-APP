package laquay.com.open.canalestdt.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.net.Uri;

public class VideoUtils {
    public static void watchYoutubeVideo(Context context, String url) {
        // try YouTube app mode, if not installed then launch in browser
        try {
            Utils.launchIntent(context, Uri.parse("vnd.youtube:" + url.split("/")[3]));
        } catch (ActivityNotFoundException ex) {
            Utils.launchIntent(context, Uri.parse("http://www.youtube.com/watch?v=" + url));
        }
    }

    public static void watchUnknownVideo(Context context, String url) {
        Utils.launchIntent(context, Uri.parse(url));
    }
}
