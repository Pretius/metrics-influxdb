package com.pretius.metrics_influxdb;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public enum InfluxdbVersion {

    VERSION_0_8() {
        @Override
        public SeriesBuilder newSeriesBuilder() {
            return new JsonBuilder();
        }

        @Override
        public URL createHttpURL(String host, int port, String path, String database, String username,
                                 String password, String timePrecision) throws MalformedURLException, UnsupportedEncodingException {
            return new URL("http", host, port,
                    path + "/db/" + database
                            + "/series?u=" + URLEncoder.encode(username, Influxdb.UTF_8.name())
                            + "&p=" + password
                            + "&time_precision=" + timePrecision);
        }
    },
    VERSION_0_9() {
        @Override
        public SeriesBuilder newSeriesBuilder() {
            return new LineBuilder();
        }

        @Override
        public URL createHttpURL(String host, int port, String path, String database, String username,
                                 String password, String timePrecision) throws MalformedURLException, UnsupportedEncodingException {
            return new URL("http", host, port, path + "/write?db=" + database
                    + (username != null ? "&u=" + URLEncoder.encode(username, Influxdb.UTF_8.name()) + "&p=" + password : "")
                    + "&precision=" + timePrecision);
        }
    };

    public abstract SeriesBuilder newSeriesBuilder();

    public abstract URL createHttpURL(String host, int port, String path, String database, String username,
                                      String password, String timePrecision) throws MalformedURLException, UnsupportedEncodingException;

}
