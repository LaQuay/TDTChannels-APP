package laquay.com.open.canalestdt.model;

import java.util.ArrayList;

public class Country {
    private String name;
    private ArrayList<Community> communities;

    public Country(String name, ArrayList<Community> communities) {
        this.name = name;
        this.communities = communities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Community> getCommunities() {
        return communities;
    }

    public void setCommunities(ArrayList<Community> communities) {
        this.communities = communities;
    }

    public void addCommunity(Community community) {
        this.communities.add(community);
    }

    public void removeCommunity(Community community) {
        this.communities.remove(community);
    }
}
