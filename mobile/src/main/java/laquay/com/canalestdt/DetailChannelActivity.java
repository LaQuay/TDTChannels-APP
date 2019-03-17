package laquay.com.canalestdt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.ArrayList;

import laquay.com.canalestdt.component.ChannelList;
import laquay.com.canalestdt.controller.VolleyController;
import laquay.com.canalestdt.model.Channel;
import laquay.com.canalestdt.model.ChannelOptions;

public class DetailChannelActivity extends AppCompatActivity {
    public static final String TAG = DetailChannelActivity.class.getSimpleName();
    public static final String EXTRA_MESSAGE = "laquay.com.canalestdt.CHANNEL_DETAIL";
    private Channel channel;

    private ImageView channelImageIV;
    private TextView channelNameTV;
    private TextView channelURLTV;
    private TextView channelSourceTV;
    private ListView channelSourceLV;

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
        channelImageIV = findViewById(R.id.channel_image_detail_tv);
        channelNameTV = findViewById(R.id.channel_name_detail_tv);
        channelURLTV = findViewById(R.id.channel_url_detail_tv);
        channelSourceTV = findViewById(R.id.channel_source_detail_tv);
        channelSourceLV = findViewById(R.id.channel_source_detail_lv);
    }

    private void setUpListeners() {

    }

    private void loadChannel() {
        channelNameTV.setText(channel.getName());
        channelURLTV.setText(channel.getWeb());

        // Load first option
        if (!channel.getOptions().isEmpty()) {
            // Fill available options
            ArrayList<String> sources = new ArrayList<>();
            for (ChannelOptions channelOption : channel.getOptions()) {
                sources.add(channelOption.getUrl());
            }

            ArrayAdapter<String> sourcesAdapter = new ArrayAdapter<>(this,
                    R.layout.item_list_detail_channel, android.R.id.text1, sources);
            channelSourceLV.setAdapter(sourcesAdapter);

            channelSourceLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String source = (String) channelSourceLV.getItemAtPosition(position);
                    loadVideo(source);
                }
            });
        }

        ImageRequest request = new ImageRequest(channel.getLogo(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        channelImageIV.setImageBitmap(bitmap);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        channelImageIV.setImageResource(R.drawable.ic_launcher_foreground);
                    }
                });
        VolleyController.getInstance(this).addToQueue(request);
    }

    private void loadVideo(String streamURL) {
        DialogFragment newFragment = VideoDialogFragment.newInstance(streamURL);
        newFragment.show(getSupportFragmentManager(), "VideoDialog");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
