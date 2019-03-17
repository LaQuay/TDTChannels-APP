package laquay.com.canalestdt;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class VideoDialogFragment extends DialogFragment {
    private static final String CHANNEL_KEY = "CHANNEL_URL";
    private SimpleExoPlayer player;
    private PlayerView channelVideoView;
    private DefaultDataSourceFactory dataSourceFactory;

    public static VideoDialogFragment newInstance(String streamURL) {
        VideoDialogFragment videoDialogFragment = new VideoDialogFragment();

        Bundle args = new Bundle();
        args.putString(CHANNEL_KEY, streamURL);
        videoDialogFragment.setArguments(args);

        return videoDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_dialog, container, false);

        channelVideoView = rootView.findViewById(R.id.channel_video_detail_exoplayer);

        if (getArguments() != null) {
            String channelUrl = getArguments().getString(CHANNEL_KEY);
            if (getActivity() != null && getContext() != null && channelUrl != null) {
                player = ExoPlayerFactory.newSimpleInstance(getContext());

                // Produces DataSource instances through which media data is loaded.
                dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                        Util.getUserAgent(getContext(), "laquay.com.canalestdt"));

                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                loadVideo(channelUrl);
            }
        }
        return rootView;
    }

    public void loadVideo(String streamURL) {
        //channelSourceTV.setText(getString(R.string.channel_detail_currenty_playing) + " - Source: " + (sourceNumber + 1));
        channelVideoView.setPlayer(player);

        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(streamURL));

        // Prepare the player with the source.
        player.prepare(videoSource);
    }

    // Activity onStop, player must be release because of memory saving
    @Override
    public void onStop() {
        super.onStop();

        if (getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            if (player != null) {
                player.release();
            }
        }
    }
}
