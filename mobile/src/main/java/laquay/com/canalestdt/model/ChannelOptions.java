package laquay.com.canalestdt.model;

import java.io.Serializable;

public class ChannelOptions implements Serializable {
    private String format;
    private String url;
    private String resolution;

    public ChannelOptions(String format, String url, String resolution) {
        this.format = format;
        this.url = url;
        this.resolution = resolution;
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

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    @Override
    public String toString() {
        return format + ", " + url + ", " + resolution;
    }
}
