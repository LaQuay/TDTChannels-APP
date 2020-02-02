package laquay.com.canalestdt.utils;

import laquay.com.canalestdt.controller.SharedPreferencesController;

public class SourcesManagement {
    public static boolean isChannelFavorite(String channelName) {
        return SharedPreferencesController.getInstance().getValue("" + channelName, Boolean.class, false);
    }

    public static void setChannelFavorite(String channelName, boolean isFavorite) {
        SharedPreferencesController.getInstance().putValue("" + channelName, isFavorite);
    }

    public static boolean isCommunitySelected(String communityName) {
        return SharedPreferencesController.getInstance().getValue("" + communityName, Boolean.class, true);
    }

    public static void setCommunitySelected(String communityName, boolean isSelected) {
        SharedPreferencesController.getInstance().putValue("" + communityName, isSelected);
    }
}
