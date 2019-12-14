package laquay.com.canalestdt;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import laquay.com.canalestdt.component.ChannelList;
import laquay.com.canalestdt.controller.APIController;
import laquay.com.canalestdt.model.Channel;
import laquay.com.canalestdt.model.Community;
import laquay.com.canalestdt.model.Country;

import static laquay.com.canalestdt.DetailChannelActivity.EXTRA_MESSAGE;
import static laquay.com.canalestdt.DetailChannelActivity.EXTRA_TYPE;
import static laquay.com.canalestdt.DetailChannelActivity.TYPE_RADIO;

public class RadioFragment extends Fragment implements APIController.ResponseServerCallback {
    public static final String TAG = RadioFragment.class.getSimpleName();
    private View rootView;
    private RecyclerView channelRecyclerView;
    private ChannelListAdapter channelAdapter;
    private ArrayList<ChannelList> channelLists;
    private RadioFragment.ChannelItemFilter mFilter = new RadioFragment.ChannelItemFilter();

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
        channelRecyclerView = rootView.findViewById(R.id.channel_main_lv);
        channelAdapter = new ChannelListAdapter(getContext(), new ChannelListAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(ChannelList channelList) {
                showDetails(channelList);
            }
        });
        channelRecyclerView.setAdapter(channelAdapter);
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
            this.channelLists = channelLists;
            channelAdapter.submitList(this.channelLists);
        }

        Log.i(TAG, "Redrawing channels - End");
    }

    public void showDetails(ChannelList channel) {
        Intent intent = new Intent(getActivity(), DetailChannelActivity.class);
        intent.putExtra(EXTRA_MESSAGE, channel);
        intent.putExtra(EXTRA_TYPE, TYPE_RADIO);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.fragment_radio, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);

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
                if (mFilter != null) {
                    mFilter.filter(newText);
                }
                return false;
            }
        });
    }

    private class ChannelItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();

            final ArrayList<ChannelList> filteredChannels = new ArrayList<>();

            String channelNameToFilter;
            for (int i = 0; i < channelLists.size(); i++) {
                ChannelList channelList = channelLists.get(i);
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
            channelAdapter.submitList((ArrayList<ChannelList>) results.values);
        }
    }
}
