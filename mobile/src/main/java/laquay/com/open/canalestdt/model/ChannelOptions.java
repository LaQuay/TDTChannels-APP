package laquay.com.open.canalestdt.model;

import java.io.Serializable;

public class ChannelOptions implements Serializable {
    private String format;
    private String url;

    public ChannelOptions(String format, String url) {
        this.format = format;
        this.url = url;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return format + ", " + url;
    }
}
