package laquay.com.canalestdt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
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

public class RadioFragment extends Fragment implements APIController.ResponseServerCallback {
    public static final String TAG = RadioFragment.class.getSimpleName();
    private View rootView;
    private GridView channelGridView;
    private CountryArrayAdapter arrayAdapter;

    public static RadioFragment newInstance() {
        return new RadioFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true);
        setUpElements();
        setUpListeners();

        APIController.getInstance().loadChannels(APIController.TypeOfRequest.RADIO, false, getContext(), this);

        return rootView;
    }

    private void setUpElements() {
        channelGridView = rootView.findViewById(R.id.channel_main_lv);
    }

    private void setUpListeners() {

    }

    @Override
    public void onChannelsLoadServer(ArrayList<Country> countries) {
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

            arrayAdapter = new CountryArrayAdapter(getContext(), channelLists);
            channelGridView.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
        }

        Log.i(TAG, "Redrawing channels - End");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.fragment_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        // Change color of the search button
        if (getContext() != null) {
            Drawable drawable = DrawableCompat.wrap(searchItem.getIcon());
            DrawableCompat.setTint(drawable, ContextCompat.getColor(getContext(), R.color.white));
            menu.findItem(R.id.action_search).setIcon(drawable);
        }

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // This will be fired every time you input any character.
                if (arrayAdapter != null) {
                    arrayAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });
    }

    public class CountryArrayAdapter extends ArrayAdapter<ChannelList> implements Filterable {
        private final ArrayList<ChannelList> channels;
        private ArrayList<ChannelList> filteredChannels;
        private ItemFilter mFilter = new ItemFilter();

        public CountryArrayAdapter(@NonNull Context context, ArrayList<ChannelList> channels) {
            super(context, 0, channels);
            this.channels = channels;
            this.filteredChannels = channels;
        }

        @NonNull
        public Filter getFilter() {
            return mFilter;
        }

        @Override
        public int getCount() {
            return filteredChannels.size();
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_list_channels, parent, false);

                holder.imageView = convertView.findViewById(R.id.channel_icon);
                holder.titleView = convertView.findViewById(R.id.channel_title);
                holder.subtitleView = convertView.findViewById(R.id.channel_description);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.titleView.setText(filteredChannels.get(position).getChannel().getName());
            holder.subtitleView.setText(filteredChannels.get(position).getCountryName() + " - " + filteredChannels.get(position).getCommunityName());
            holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);

            // Temporary fix
            String imageUrl = filteredChannels.get(position).getChannel().getLogo();
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
                    //TODO FIX
                    /*Log.d(TAG, "onClick " + filteredChannels.get(position).getChannel().getName());
                    Intent intent = new Intent(getActivity(), DetailChannelActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, filteredChannels.get(position));
                    startActivity(intent);*/
                }
            });

            return convertView;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();

                final ArrayList<ChannelList> filteredChannels = new ArrayList<>();

                String channelNameToFilter;
                for (int i = 0; i < channels.size(); i++) {
                    ChannelList channelList = channels.get(i);
                    channelNameToFilter = channelList.getChannel().getName();

                    if (channelNameToFilter.toLowerCase().contains(filterString)) {
                        filteredChannels.add(channelList);
                    }
                }

                results.values = filteredChannels;
                results.count = filteredChannels.size();
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredChannels = (ArrayList<ChannelList>) results.values;
                notifyDataSetChanged();
            }
        }

        class ViewHolder {
            ImageView imageView;
            TextView titleView;
            TextView subtitleView;
        }
    }
}
