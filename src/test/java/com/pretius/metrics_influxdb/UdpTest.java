package com.pretius.metrics_influxdb;

import com.codahale.metrics.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Test
public class UdpTest {

    private Influxdb influxdb;
    private MetricRegistry registry;
    private InfluxdbReporter reporter;
    private Meter meter;
    private Timer timer;
    private Counter counter;
    private Gauge<Integer> gauge;
    private Histogram histogram;
    private int count = 0;
    private static final int ITER_COUNT = 10;
    private static final int SLEEP_MS = 250;

    @BeforeClass
    public void setUp() throws InterruptedException, IOException {
        registry = new MetricRegistry();
        influxdb = new InfluxdbUdp("127.0.0.1", 8089);
        influxdb.setDebug(true);
        influxdb.addTag("tag1", "tavValue1");
        influxdb.addTag("tag2", "tavValue2");
        influxdb.addTag("tag3", "tavValue3");

        final InfluxdbReporter reporter = InfluxdbReporter
                .forRegistry(registry)
                .prefixedWith("test")
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .skipIdleMetrics(true)
                .build(influxdb);
        reporter.start(500, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testMeter() {
        System.out.println("******************************* METER *******************************");
        meter = registry.meter("meter");
        try {
            for (int i = 0; i < ITER_COUNT; i++) {
                meter.mark();
                Thread.sleep(SLEEP_MS);
            }

        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testTimer() {
        System.out.println("******************************* TIMER *******************************");
        timer = registry.timer("timer");
        try {
            for (int i = 0; i < ITER_COUNT; i++) {
                final Timer.Context context = timer.time();
                Thread.sleep(SLEEP_MS);
                context.stop();
            }

        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testCounter() {
        System.out.println("******************************* COUNTER *******************************");
        counter = registry.counter("counter");
        try {
            for (int i = 0; i < ITER_COUNT; i++) {
                counter.inc(i);
                Thread.sleep(SLEEP_MS);
            }

        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testGauge() {
        System.out.println("******************************* GAUGE *******************************");
        gauge = new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return count++;
            }
        };
        registry.register("gauge", gauge);
        try {
            for (int i = 0; i < ITER_COUNT; i++) {
                gauge.getValue();
                Thread.sleep(SLEEP_MS);
            }

        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void testHistogram() {
        System.out.println("******************************* HISTOGRAM *******************************");
        histogram = registry.histogram("histogram");
        try {
            for (int i = 0; i < ITER_COUNT; i++) {
                histogram.update(i);
                Thread.sleep(SLEEP_MS);
            }

        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
