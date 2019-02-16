package laquay.com.canalestdt.model;

import java.util.ArrayList;

public class Community {
    private String name;
    private ArrayList<Channel> channels;

    public Community(String name, ArrayList<Channel> channels) {
        this.name = name;
        this.channels = channels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Channel> getChannels() {
        return channels;
    }

    public void setChannels(ArrayList<Channel> channels) {
        this.channels = channels;
    }

    public void addCommunity(Channel channel) {
        this.channels.add(channel);
    }

    public void removeCommunity(Channel channel) {
        this.channels.remove(channel);
    }
}
