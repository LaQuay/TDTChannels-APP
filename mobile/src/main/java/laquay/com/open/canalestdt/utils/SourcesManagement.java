package laquay.com.open.canalestdt.utils;

import laquay.com.open.canalestdt.controller.SharedPreferencesController;

public class SourcesManagement {
    public static boolean isTVChannelFavorite(String channelName) {
        return isChannelFavorite("TV" + channelName);
    }

    public static void setTVChannelFavorite(String channelName, boolean isFavorite) {
        setChannelFavorite("TV" + channelName, isFavorite);
    }

    public static boolean isTVCommunitySelected(String communityName) {
        return isCommunitySelected("TV" + communityName);
    }

    public static void setTVCommunitySelected(String communityName, boolean isSelected) {
        setCommunitySelected("TV" + communityName, isSelected);
    }

    public static boolean isRadioChannelFavorite(String channelName) {
        return isChannelFavorite("RADIO" + channelName);
    }

    public static void setRadioChannelFavorite(String channelName, boolean isFavorite) {
        setChannelFavorite("RADIO" + channelName, isFavorite);
    }

    public static boolean isRadioCommunitySelected(String communityName) {
        return isCommunitySelected("RADIO" + communityName);
    }

    public static void setRadioCommunitySelected(String communityName, boolean isSelected) {
        setCommunitySelected("RADIO" + communityName, isSelected);
    }

    private static boolean isChannelFavorite(String channelName) {
        return SharedPreferencesController.getInstance().getValue("" + channelName, Boolean.class, false);
    }

    private static void setChannelFavorite(String channelName, boolean isFavorite) {
        SharedPreferencesController.getInstance().putValue("" + channelName, isFavorite);
    }

    private static boolean isCommunitySelected(String communityName) {
        return SharedPreferencesController.getInstance().getValue("" + communityName, Boolean.class, true);
    }

    private static void setCommunitySelected(String communityName, boolean isSelected) {
        SharedPreferencesController.getInstance().putValue("" + communityName, isSelected);
    }
}
