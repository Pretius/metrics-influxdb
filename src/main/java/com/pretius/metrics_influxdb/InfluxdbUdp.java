package com.pretius.metrics_influxdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;

public class InfluxdbUdp extends Influxdb {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfluxdbHttp.class);
    private boolean suppressExceptions = true;
    protected final ArrayList<SeriesBuilder> seriesBuilders;
    private final String host;
    private final int port;
    private InfluxdbVersion version;

    public InfluxdbUdp(String host, int port) {
        this(host, port, InfluxdbVersion.VERSION_0_9);
    }

    public InfluxdbUdp(String host, int port, InfluxdbVersion version) {
        seriesBuilders = new ArrayList<>();
        this.host = host;
        this.port = port;
        this.version = version;
    }

    @Override
    public void resetRequest() {
        seriesBuilders.clear();
    }

    @Override
    public boolean hasSeriesData() {
        return !seriesBuilders.isEmpty();
    }

    @Override
    public void appendSeries(String namePrefix, String name, String nameSuffix, String[] columns, Object[][] points) {
        SeriesBuilder seriesBuilder = version.newSeriesBuilder();
        seriesBuilder.reset();
        seriesBuilder.appendSeries(namePrefix, name, nameSuffix, columns, points, tags);
        seriesBuilders.add(seriesBuilder);
    }

    @Override
    public int sendRequest() throws Exception {
        DatagramChannel channel = null;

        try {
            channel = DatagramChannel.open();
            InetSocketAddress socketAddress = new InetSocketAddress(host, port);

            for (SeriesBuilder builder : seriesBuilders) {
                String series = builder.toString();

                if (debug) {
                    LOGGER.info(series);
                }

                ByteBuffer buffer = ByteBuffer.wrap(series.getBytes());
                channel.send(buffer, socketAddress);
                buffer.clear();
            }
        } catch (Exception e) {
            if (!suppressExceptions) {
                throw e;
            }
        } finally {
            if (channel != null) {
                channel.close();
            }
        }

        return 0;
    }

    @Override
    public void suppressExceptions(boolean suppressExceptions) {
        this.suppressExceptions = suppressExceptions;
    }
}
