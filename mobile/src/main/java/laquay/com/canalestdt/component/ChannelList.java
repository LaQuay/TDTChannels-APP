package laquay.com.canalestdt.component;

import java.io.Serializable;

import laquay.com.canalestdt.model.Channel;

public class ChannelList implements Serializable {
    private String countryName;
    private String communityName;
    private Channel channel;

    public ChannelList(String countryName, String communityName, Channel channel) {
        this.countryName = countryName;
        this.communityName = communityName;
        this.channel = channel;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
