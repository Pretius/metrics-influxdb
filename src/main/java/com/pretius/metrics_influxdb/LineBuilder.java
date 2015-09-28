package com.pretius.metrics_influxdb;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Line protocol for InfluxDB 0.9.1+
 */
public class LineBuilder implements SeriesBuilder {

    private final StringBuilder lines = new StringBuilder();

    @Override
    public boolean hasSeriesData() {
        return lines.length() > 0;
    }

    @Override
    public void reset() {
        lines.setLength(0);
    }

    @Override
    public String toString() {
        return lines.toString();
    }

    private boolean isNullValue(Object value) {
        return (value instanceof Collection) && ((Collection<?>) value).size() < 1
                || value instanceof Float && Float.isInfinite((float) value)
                || value instanceof Double && Double.isInfinite((double) value);
    }

    private StringBuilder appendValue(StringBuilder lines, Object value) {
        if (value instanceof String) {
            lines.append('"').append(Sanitizer.sanitize((String)value)).append('"');
        } else if (isNullValue(value)) {
            lines.append("null");
        } else {
            lines.append(value);
        }
        return lines;
    }

    @Override
    public void appendSeries(String namePrefix, String name, String nameSuffix, String[] columns, Object[][] points, Map<String, String> tags) {
        for (int i = 0; i < points.length; i++) {
            if (lines.length() > 0) {
                lines.append("\n");
            }
            // measurement
            lines.append(Sanitizer.sanitize(namePrefix)).append(Sanitizer.sanitize(name)).append(Sanitizer.sanitize(nameSuffix));

            // tags
            if (tags != null && !tags.isEmpty()) {
                Iterator<Map.Entry<String, String>> iter = tags.entrySet().iterator();
                int j = 0;
                while (iter.hasNext()) {
                    if (j++ == 0) {
                        lines.append(',');
                    }
                    Map.Entry<String, String> entry = iter.next();
                    lines.append(Sanitizer.sanitize(entry.getKey())).append("=").append(Sanitizer.sanitize(entry.getValue()));
                }
            }

            // data
            lines.append(" ");
            Object[] row = points[i];

            for (int j = 1; j < row.length; j++) {
                if (j > 1) {
                    lines.append(',');
                }
                lines.append(Sanitizer.sanitize(columns[j])).append("=").append("");
                appendValue(lines, row[j]);
            }

            // timestamp
            lines.append(" ").append(row[0]);
        }

    }
}
