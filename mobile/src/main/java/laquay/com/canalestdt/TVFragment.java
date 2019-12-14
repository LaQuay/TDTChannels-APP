package laquay.com.canalestdt;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.ArrayList;

import laquay.com.canalestdt.component.ChannelList;
import laquay.com.canalestdt.controller.APIController;
import laquay.com.canalestdt.controller.SharedPreferencesController;
import laquay.com.canalestdt.controller.VolleyController;
import laquay.com.canalestdt.model.Channel;
import laquay.com.canalestdt.model.Community;
import laquay.com.canalestdt.model.Country;

import static laquay.com.canalestdt.DetailChannelActivity.EXTRA_MESSAGE;
import static laquay.com.canalestdt.DetailChannelActivity.EXTRA_TYPE;
import static laquay.com.canalestdt.DetailChannelActivity.TYPE_TV;

public class TVFragment extends Fragment implements APIController.ResponseServerCallback {
    public static final String TAG = TVFragment.class.getSimpleName();
    private View rootView;
    private GridView channelGridView;
    private CountryArrayAdapter arrayAdapter;
    private ArrayList<Country> countries;
    private ArrayList<Community> communities;
    private ArrayList<ChannelList> channelLists;

    public static TVFragment newInstance() {
        return new TVFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true);
        setUpElements();
        setUpListeners();

        APIController.getInstance().loadChannels(APIController.TypeOfRequest.TV, false, getContext(), this);

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
        this.countries = countries;

        createChannelList();

        drawSelectedChannelLists();

        Log.i(TAG, "Redrawing channels - End");
    }

    private void createChannelList() {
        channelLists = new ArrayList<>();
        for (int i = 0; i < countries.size(); ++i) {
            communities = countries.get(i).getCommunities();

            for (int j = 0; j < communities.size(); ++j) {
                ArrayList<Channel> channels = communities.get(j).getChannels();

                boolean isCommunityShown = SharedPreferencesController.getInstance().getValue("" + communities.get(j).getName(), Boolean.class, true);
                if (isCommunityShown) {
                    for (int k = 0; k < channels.size(); ++k) {
                        channelLists.add(new ChannelList(countries.get(i).getName(),
                                communities.get(j).getName(), channels.get(k)));
                    }
                }
            }
        }
    }

    private void drawSelectedChannelLists() {
        if (getContext() != null) {
            arrayAdapter = new CountryArrayAdapter(getContext(), channelLists);
            channelGridView.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.fragment_tv, menu);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                if (getContext() != null) {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getContext());
                    View v = getActivity().getLayoutInflater().inflate(R.layout.alert_filter_channels, null);

                    final LinearLayout filterLL = v.findViewById(R.id.filters_alert_filter_channel_ll);

                    for (int i = 0; i < countries.size(); ++i) {
                        communities = countries.get(i).getCommunities();

                        TextView country = new TextView(getActivity());
                        country.setText(countries.get(i).getName());
                        country.setTextSize(18);
                        country.setPadding(0, 0, 0, 0);
                        country.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
                        LinearLayout.LayoutParams countryParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        if (i == 0) {
                            countryParams.setMargins(0, 0, 0, 10);
                        } else {
                            countryParams.setMargins(0, 20, 0, 10);
                        }
                        country.setLayoutParams(countryParams);
                        filterLL.addView(country);

                        LinearLayout rootLayout = new LinearLayout(getContext());
                        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        rootLayout.setOrientation(LinearLayout.HORIZONTAL);

                        LinearLayout leftLayout = new LinearLayout(getContext());
                        leftLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                        leftLayout.setOrientation(LinearLayout.VERTICAL);

                        LinearLayout rightLayout = new LinearLayout(getContext());
                        rightLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                        rightLayout.setOrientation(LinearLayout.VERTICAL);

                        for (int j = 0; j < communities.size(); ++j) {
                            boolean isCommunityShown = SharedPreferencesController.getInstance().getValue("" + communities.get(j).getName(), Boolean.class, true);

                            CheckBox community = new CheckBox(getActivity());
                            community.setText(communities.get(j).getName());
                            community.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
                            community.setChecked(isCommunityShown);
                            community.setMaxLines(1);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            params.gravity = Gravity.NO_GRAVITY;
                            community.setLayoutParams(params);

                            if (j % 2 == 0) {
                                leftLayout.addView(community);
                            } else {
                                rightLayout.addView(community);
                            }
                        }

                        rootLayout.addView(leftLayout);
                        if (rightLayout.getChildCount() > 0) {
                            rootLayout.addView(rightLayout);
                        }
                        filterLL.addView(rootLayout);
                    }
                    builder.setView(v);
                    builder.create();
                    builder.setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            filterLL.getChildCount();

                            for (int i = 0; i < filterLL.getChildCount(); ++i) {
                                if (filterLL.getChildAt(i) instanceof LinearLayout) {
                                    LinearLayout linearLayout = (LinearLayout) filterLL.getChildAt(i);

                                    for (int j = 0; j < linearLayout.getChildCount(); ++j) {
                                        if (linearLayout.getChildAt(j) instanceof LinearLayout) {
                                            LinearLayout columnLayout = (LinearLayout) linearLayout.getChildAt(j);

                                            for (int z = 0; z < columnLayout.getChildCount(); ++z) {
                                                if (columnLayout.getChildAt(z) instanceof CheckBox) {
                                                    CheckBox checkBox = (CheckBox) columnLayout.getChildAt(z);
                                                    // TODO This should be inside a "lib"/"controller"
                                                    SharedPreferencesController.getInstance().putValue("" + checkBox.getText(), checkBox.isChecked());

                                                    createChannelList();
                                                    drawSelectedChannelLists();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    builder.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            holder.imageView.setImageResource(R.mipmap.ic_launcher);

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
                            finalHolder.imageView.setImageResource(R.mipmap.ic_launcher);
                        }
                    });
            VolleyController.getInstance(getContext()).addToQueue(request);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick " + filteredChannels.get(position).getChannel().getName());
                    Intent intent = new Intent(getActivity(), DetailChannelActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, filteredChannels.get(position));
                    intent.putExtra(EXTRA_TYPE, TYPE_TV);
                    startActivity(intent);
                }
            });

            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return getCount();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
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
