package com.pretius.metrics_influxdb;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public abstract class Influxdb {

    public static final Charset UTF_8 = Charset.forName("UTF-8");
    protected Map<String, String> tags = new HashMap<>();
    protected boolean debug = false;

    /**
     * Forgot previously appendSeries.
     */
	public abstract void resetRequest();

    /**
     * Returns true if the pending request has metrics to report.
     */
	public abstract boolean hasSeriesData();

    /**
     * Add series current series pack
     */
	public abstract void appendSeries(String namePrefix, String name, String nameSuffix, String[] columns, Object[][] points);

    /**
     * Send series do InfluxDB
     * @return response code for HTTP, 0 for UDP
     * @throws Exception
     */
	public abstract int sendRequest() throws Exception;

    /**
     * Suppres exceptions on transport / connection errors
     */
    public abstract void suppressExceptions(boolean suppressExceptions);

    /**
     * Add tag to series
     */
    public void addTag(String key, String value) {
        tags.put(key, value);
    }

    public boolean isDebug() {
        return debug;
    }

    /**
     * Print series to log
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
