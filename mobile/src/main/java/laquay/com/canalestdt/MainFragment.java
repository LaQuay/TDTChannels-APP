package laquay.com.canalestdt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.ArrayList;

import laquay.com.canalestdt.component.ChannelList;
import laquay.com.canalestdt.controller.APIController;
import laquay.com.canalestdt.controller.VolleyController;
import laquay.com.canalestdt.model.Channel;
import laquay.com.canalestdt.model.Community;
import laquay.com.canalestdt.model.Country;

import static laquay.com.canalestdt.DetailChannelActivity.EXTRA_MESSAGE;

public class MainFragment extends Fragment implements APIController.ResponseServerCallback {
    public static final String TAG = MainFragment.class.getSimpleName();
    private View rootView;
    private ListView channelListView;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        setUpElements();
        setUpListeners();

        APIController.getInstance().loadChannels(false, getContext(), this);

        return rootView;
    }

    private void setUpElements() {
        channelListView = rootView.findViewById(R.id.channel_main_lv);
    }

    private void setUpListeners() {

    }

    @Override
    public void onChannelLoadServer(ArrayList<Country> countries) {
        Log.i(TAG, "Redrawing channels - Start");

        if (getContext() != null) {
            ArrayList<ChannelList> channelLists = new ArrayList<>();

            for (int i = 0; i < countries.size(); ++i) {
                ArrayList<Community> communities = countries.get(i).getCommunities();

                for (int j = 0; j < communities.size(); ++j) {
                    ArrayList<Channel> channels = communities.get(j).getChannels();

                    for (int k = 0; k < channels.size(); ++k) {
                        channelLists.add(new ChannelList(countries.get(i).getName(),
                                communities.get(j).getName(), channels.get(k)));
                    }
                }
            }

            CountryArrayAdapter arrayAdapter = new CountryArrayAdapter(getContext(), channelLists);
            channelListView.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
        }

        Log.i(TAG, "Redrawing channels - End");
    }

    public class CountryArrayAdapter extends ArrayAdapter<ChannelList> {
        private final ArrayList<ChannelList> channels;

        public CountryArrayAdapter(@NonNull Context context, ArrayList<ChannelList> channels) {
            super(context, 0, channels);
            this.channels = channels;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_list, parent, false);

                holder.imageView = convertView.findViewById(R.id.channel_icon);
                holder.titleView = convertView.findViewById(R.id.channel_title);
                holder.subtitleView = convertView.findViewById(R.id.channel_description);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.titleView.setText(channels.get(position).getChannel().getName());
            holder.subtitleView.setText(channels.get(position).getCountryName() + " - " + channels.get(position).getCommunityName());
            holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);

            String imageUrl = channels.get(position).getChannel().getLogo();
            // Temporary fix
            imageUrl = imageUrl.replace("http://graph.facebook.com", "https://graph.facebook.com");

            final ViewHolder finalHolder = holder;
            ImageRequest request = new ImageRequest(imageUrl,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            finalHolder.imageView.setImageBitmap(bitmap);
                        }
                    }, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            finalHolder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
                        }
                    });
            VolleyController.getInstance(getContext()).addToQueue(request);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick " + channels.get(position).getChannel().getName());
                    Intent intent = new Intent(getActivity(), DetailChannelActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, channels.get(position));
                    startActivity(intent);
                }
            });

            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView titleView;
            TextView subtitleView;
        }
    }
}
