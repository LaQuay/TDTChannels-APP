package laquay.com.canalestdt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import laquay.com.canalestdt.component.ChannelList;
import laquay.com.canalestdt.model.Channel;

public class DetailChannelActivity extends AppCompatActivity {
    public static final String TAG = DetailChannelActivity.class.getSimpleName();
    public static final String EXTRA_MESSAGE = "laquay.com.canalestdt.CHANNEL_DETAIL";
    private Channel channel;
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
