package laquay.com.open.canalestdt.controller;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import laquay.com.open.canalestdt.model.Channel;
import laquay.com.open.canalestdt.model.ChannelOptions;
import laquay.com.open.canalestdt.model.Community;
import laquay.com.open.canalestdt.model.Country;
import laquay.com.open.canalestdt.utils.APIUtils;

public class APIController {
    public static final String TAG = APIController.class.getSimpleName();
    private static APIController instance;
    private ArrayList<Country> televisionChannels;
    private ArrayList<Country> radioChannels;

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

    public void loadChannels(TypeOfRequest typeOfRequest, boolean forceUpdate, final Context context, final ResponseServerCallback responseServerCallback) {
        if (typeOfRequest.equals(TypeOfRequest.TV)) {
            if (!forceUpdate && televisionChannels != null && !televisionChannels.isEmpty()) {
                Log.i(TAG, "Load tv channels from cache");
                responseServerCallback.onChannelsLoadServer(televisionChannels);
            } else {
                Log.i(TAG, "Load tv channels from server: " + APIUtils.TV_URL);
                televisionChannels = new ArrayList<>();
                downloadChannels(APIUtils.TV_URL, televisionChannels, context, responseServerCallback);
            }
        } else {
            if (typeOfRequest.equals(TypeOfRequest.RADIO)) {
                if (!forceUpdate && radioChannels != null && !radioChannels.isEmpty()) {
                    Log.i(TAG, "Load radio channels from cache");
                    responseServerCallback.onChannelsLoadServer(radioChannels);
                } else {
                    Log.i(TAG, "Load radio channels from server: " + APIUtils.RADIO_URL);
                    radioChannels = new ArrayList<>();
                    downloadChannels(APIUtils.RADIO_URL, radioChannels, context, responseServerCallback);
                }
            }
        }
    }

    private void downloadChannels(final String URL, final ArrayList<Country> channelsToMatch, final Context context, final ResponseServerCallback responseServerCallback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "Response OK");

                        try {
                            JSONArray countriesJsonArray = response.getJSONArray("countries");
                            for (int i = 0; i < countriesJsonArray.length(); ++i) {
                                JSONObject country = countriesJsonArray.getJSONObject(i);

                                String countryName = country.getString("name");
                                JSONArray communitiesArray = country.getJSONArray("ambits");
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
                                        String channelEPG = channelJson.getString("epg_id");
                                        JSONArray channelOptionsJson = channelJson.getJSONArray("options");
                                        String channelExtraInfo = channelJson.getString("extra_info");

                                        ArrayList<ChannelOptions> channelOptions = new ArrayList<>();
                                        for (int z = 0; z < channelOptionsJson.length(); ++z) {
                                            JSONObject optionJson = channelOptionsJson.getJSONObject(z);

                                            String optionFormat = optionJson.getString("format");
                                            String optionURL = optionJson.getString("url");

                                            channelOptions.add(new ChannelOptions(optionFormat, optionURL));
                                        }

                                        Channel channel = new Channel(channelName, channelWeb, channelLogo,
                                                channelEPG, channelOptions, channelExtraInfo);
                                        //Log.i(TAG, "Adding channel: " + channel.toString());
                                        channels.add(channel);
                                    }
                                    communities.add(new Community(communityName, channels));
                                }
                                channelsToMatch.add(new Country(countryName, communities));
                            }

                            responseServerCallback.onChannelsLoadServer(channelsToMatch);
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
                        Log.e(TAG, "Error while accessing to URL " + URL);
                    }
                }
        );

        // Add JsonArrayRequest to the RequestQueue
        VolleyController.getInstance(context).addToQueue(jsonObjectRequest);
    }

    public enum TypeOfRequest {
        TV,
        RADIO
    }

    public interface ResponseServerCallback {
        void onChannelsLoadServer(ArrayList<Country> countries);
    }
}
