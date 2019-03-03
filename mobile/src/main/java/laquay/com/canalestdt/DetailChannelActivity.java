package laquay.com.canalestdt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import laquay.com.canalestdt.component.ChannelList;
import laquay.com.canalestdt.model.Channel;

public class DetailChannelActivity extends AppCompatActivity {
    public static final String TAG = DetailChannelActivity.class.getSimpleName();
    public static final String EXTRA_MESSAGE = "laquay.com.canalestdt.CHANNEL_DETAIL";
    private Channel channel;
    private SimpleExoPlayer player;

    private PlayerView channelVideoView;
    private TextView channelNameTV;
    private TextView channelURLTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        Intent intent = getIntent();
        ChannelList channelList = (ChannelList) intent.getSerializableExtra(EXTRA_MESSAGE);
        channel = channelList.getChannel();

        getSupportActionBar().setTitle(channelList.getChannel().getName());

        setUpElements();
        setUpListeners();

        loadChannel();
    }

    private void setUpElements() {
        channelVideoView = findViewById(R.id.channel_video_detail_tv);
        channelNameTV = findViewById(R.id.channel_name_detail_tv);
        channelURLTV = findViewById(R.id.channel_url_detail_tv);
    }

    private void setUpListeners() {

    }

    private void loadChannel() {
        channelNameTV.setText(channel.getName());
        channelURLTV.setText(channel.getWeb());

        // Load first option
        // TODO Select different option
        if (!channel.getOptions().isEmpty()) {
            loadVideo(Uri.parse(channel.getOptions().get(0).getUrl()));
        }
    }

    public void loadVideo(Uri videoUri) {
        player = ExoPlayerFactory.newSimpleInstance(this);
        channelVideoView.setPlayer(player);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "laquay.com.canalestdt"));

        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);

        // Prepare the player with the source.
        player.prepare(videoSource);
    }

    // Activity onStop, player must be release because of memory saving
    @Override
    public void onStop() {
        super.onStop();
        player.release();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
