//	metrics-influxdb
//
//	Written in 2014 by David Bernard <dbernard@novaquark.com>
//
//	[other author/contributor lines as appropriate]
//
//	To the extent possible under law, the author(s) have dedicated all copyright and
//	related and neighboring rights to this software to the public domain worldwide.
//	This software is distributed without any warranty.
//
//	You should have received a copy of the CC0 Public Domain Dedication along with
//	this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
package com.pretius.metrics_influxdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * A client to send data to a InfluxDB server via HTTP protocol.
 * <p/>
 * The usage :
 * <p/>
 * <pre>
 *   Influxdb influxdb = new Influxdb(...);
 *
 *   influxdb.appendSeries(...);
 *   ...
 *   influxdb.appendSeries(...);
 *   influxdb.sendRequest();
 *
 *   influxdb.appendSeries(...);
 *   ...
 *   influxdb.appendSeries(...);
 *   influxdb.sendRequest();
 *
 * </pre>
 */
public class InfluxdbHttp extends Influxdb {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfluxdbHttp.class);

    public final URL url;
    public SeriesBuilder seriesBuilder;
    private boolean suppressExceptions = true;
    private InfluxdbVersion version;

    /**
     * @throws IOException If the URL is malformed
     */
    public InfluxdbHttp(InfluxdbHttpConfig config) throws Exception {
        this.version = config.getVersion();
        this.seriesBuilder = version.newSeriesBuilder();
        this.url = version.createHttpURL(config.getHost(), config.getPort(), config.getPath(),
                config.getDatabase(), config.getUserName(),
                config.getPassword(), toTimePrecision(config.getTimePrecision()));
    }

    public static String toTimePrecision(TimeUnit t) {
        switch (t) {
            case SECONDS:
                return "s";
            case MILLISECONDS:
                return "ms";
            case MICROSECONDS:
                return "u";
            default:
                throw new IllegalArgumentException("time precision should be SECONDS or MILLISECONDS or MICROSECONDS");
        }
    }

    public boolean hasSeriesData() {
        return seriesBuilder.hasSeriesData();
    }

    @Override
    public void resetRequest() {
        seriesBuilder.reset();
    }

    @Override
    public void appendSeries(String namePrefix, String name, String nameSuffix, String[] columns, Object[][] points) {
        seriesBuilder.appendSeries(namePrefix, name, nameSuffix, columns, points, tags);
    }

    @Override
    public int sendRequest() throws Exception {
        String series = seriesBuilder.toString();

        if (debug) {
            LOGGER.info(series);
        }

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        // Send post request
        con.setDoOutput(true);
        OutputStream wr = con.getOutputStream();
        wr.write(series.getBytes(UTF_8));
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // ignore Response content
            con.getInputStream().close();
        } else if (!suppressExceptions) {
            throw new IOException("Server returned HTTP response code: " + responseCode + "for URL: " + url + " with content :'" + con.getResponseMessage() + "'");
        }
        return responseCode;
    }

    @Override
    public void suppressExceptions(boolean suppressExceptions) {
        this.suppressExceptions = suppressExceptions;
    }

    @Override
    public long convertTimestamp(long timestamp) {
        return timestamp;
    }
}
