package com.pretius.metrics_influxdb;

public class Sanitizer {

    public static String sanitize(String src) {
        if (src == null) {
            return src;
        }
        return src.replaceAll("[^a-zA-Z0-9_.]", "_");
    }
}
