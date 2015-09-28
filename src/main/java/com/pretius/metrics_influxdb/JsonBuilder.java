package com.pretius.metrics_influxdb;

import java.util.Collection;
import java.util.Map;

/**
 * Json protocol for InfluxDB 0.8
 */
class JsonBuilder implements SeriesBuilder {
    private final StringBuilder json = new StringBuilder();
    private boolean hasSeriesData;

    @Override
    public boolean hasSeriesData() {
        return hasSeriesData;
    }

    @Override
    public void reset() {
        json.setLength(0);
        json.append('[');
        hasSeriesData = false;
    }

    @Override
    public String toString() {
        json.append(']');
        String str = json.toString();
        json.setLength(json.length() - 1);
        return str;
    }

    @Override
    public void appendSeries(String namePrefix, String name, String nameSuffix, String[] columns, Object[][] points, Map<String, String> tags) {
        hasSeriesData = true;
        if (json.length() > 1)
            json.append(',');
        json.append("{\"name\":\"").append(Sanitizer.sanitize(namePrefix)).append(Sanitizer.sanitize(name))
                .append(Sanitizer.sanitize(nameSuffix)).append("\",\"columns\":[");
        for (int i = 0; i < columns.length; i++) {
            if (i > 0)
                json.append(',');
            json.append('"').append(Sanitizer.sanitize(columns[i])).append('"');
        }
        json.append("],\"points\":[");
        for (int i = 0; i < points.length; i++) {
            if (i > 0)
                json.append(',');
            Object[] row = points[i];
            json.append('[');
            for (int j = 0; j < row.length; j++) {
                if (j > 0)
                    json.append(',');
                Object value = row[j];
                if (value instanceof String) {
                    json.append('"').append(Sanitizer.sanitize((String) value)).append('"');
                } else if ((value instanceof Collection) && ((Collection<?>) value).size() < 1) {
                    json.append("null");
                } else if (value instanceof Double && Double.isInfinite((double) value)) {
                    json.append("null");
                } else if (value instanceof Float && Float.isInfinite((float) value)) {
                    json.append("null");
                } else {
                    json.append(value);
                }
            }
            json.append(']');
        }
        json.append("]}");
    }

}
