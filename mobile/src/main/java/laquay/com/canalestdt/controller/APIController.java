package laquay.com.canalestdt.controller;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import laquay.com.canalestdt.model.Channel;
import laquay.com.canalestdt.model.ChannelOptions;
import laquay.com.canalestdt.model.Community;
import laquay.com.canalestdt.model.Country;
import laquay.com.canalestdt.utils.APIUtils;

public class APIController {
    public static final String TAG = APIController.class.getSimpleName();
    private static APIController instance;
    private ArrayList<Country> countries;

    private APIController() {
    }

    public static APIController getInstance() {
        if (instance == null) {
            createInstance();
        }
        return instance;
    }

    private synchronized static void createInstance() {
        if (instance == null) {
            instance = new APIController();
        }
    }

    public void loadChannels(boolean forceUpdate, final Context context, final ResponseServerCallback responseServerCallback) {
        if (!forceUpdate && countries != null && !countries.isEmpty()) {
            Log.i(TAG, "Load channels from cache");
            responseServerCallback.onChannelLoadServer(countries);
        } else {
            Log.i(TAG, "Load channels from server: " + APIUtils.CHANNELS_URL);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    APIUtils.CHANNELS_URL,
                    null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.i(TAG, "Response OK");

                            try {
                                countries = new ArrayList<>();
                                for (int i = 0; i < response.length(); ++i) {
                                    //Log.i(TAG, response.getJSONObject(i).toString());
                                    JSONObject jsonElement = response.getJSONObject(i);

                                    boolean isCountry = jsonElement.has("name");
                                    if (isCountry) {
                                        String countryName = jsonElement.getString("name");
                                        JSONArray communitiesArray = jsonElement.getJSONArray("ambits");
                                        ArrayList<Community> communities = new ArrayList<>();

                                        for (int j = 0; j < communitiesArray.length(); ++j) {
                                            JSONObject communityJson = communitiesArray.getJSONObject(j);

                                            String communityName = communityJson.getString("name");
                                            JSONArray channelsArray = communityJson.getJSONArray("channels");
                                            ArrayList<Channel> channels = new ArrayList<>();

                                            for (int k = 0; k < channelsArray.length(); ++k) {
                                                JSONObject channelJson = channelsArray.getJSONObject(k);

                                                String channelName = channelJson.getString("name");
                                                String channelWeb = channelJson.getString("web");
                                                String channelLogo = channelJson.getString("logo");
                                                String channelResolution = channelJson.getString("resolution");
                                                String channelEPG = channelJson.getString("epg_id");
                                                JSONArray channelOptionsJson = channelJson.getJSONArray("options");
                                                ArrayList<ChannelOptions> channelOptions = new ArrayList<>();
                                                String channelExtraInfo = channelJson.getString("extra_info");

                                                for (int z = 0; z < channelOptionsJson.length(); ++z) {
                                                    JSONObject optionJson = channelOptionsJson.getJSONObject(z);

                                                    String optionFormat = optionJson.getString("format");
                                                    String optionURL = optionJson.getString("url");

                                                    channelOptions.add(new ChannelOptions(optionFormat, optionURL, channelResolution));
                                                }

                                                Channel channel = new Channel(channelName, channelWeb, channelLogo,
                                                        channelEPG, channelOptions, channelExtraInfo);
                                                //Log.i(TAG, "Adding channel: " + channel.toString());
                                                channels.add(channel);
                                            }
                                            communities.add(new Community(communityName, channels));
                                        }
                                        countries.add(new Country(countryName, communities));
                                    }
                                }

                                responseServerCallback.onChannelLoadServer(countries);
                            } catch (JSONException e) {
                                Log.e(TAG, "ERROR Parsing JSON");
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Do something when error occurred
                            Log.e(TAG, "Error while accessing to: " + APIUtils.CHANNELS_URL);
                        }
                    }
            );

            // Add JsonArrayRequest to the RequestQueue
            VolleyController.getInstance(context).addToQueue(jsonArrayRequest);
        }
    }

    public interface ResponseServerCallback {
        void onChannelLoadServer(ArrayList<Country> countries);
    }
}
