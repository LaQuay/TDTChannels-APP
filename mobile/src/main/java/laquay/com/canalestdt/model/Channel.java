package laquay.com.canalestdt.model;

import java.util.ArrayList;

public class Channel {
    private String name;
    private String web;
    private String logo;
    private String epgID;
    private String extraInfo;
    private ArrayList<ChannelOptions> options;

    public Channel(String name, String web, String logo, String epgID, ArrayList<ChannelOptions> options, String extraInfo) {
        this.name = name;
        this.web = web;
        this.logo = logo;
        this.epgID = epgID;
        this.options = options;
        this.extraInfo = extraInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeb() {
        return web;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getEpgID() {
        return epgID;
    }

    public void setEpgID(String epgID) {
        this.epgID = epgID;
    }

    public ArrayList<ChannelOptions> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<ChannelOptions> options) {
        this.options = options;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    @Override
    public String toString() {
        return name + ", " + web + ", " + logo + ", " + epgID + ", " + options.toString() + ", " + extraInfo;
    }
}
