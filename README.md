Fork of [davidB](https://github.com/davidB/metrics-influxdb) library enabling support for InfluxDB v0.9.1+

The library provide

* a lighter client than influxdb-java to push only series to an [InfluxDB](http://influxdb.org) server.
* A reporter for [metrics](http://metrics.codahale.com/) which announces measurements.

The library provide a lighter client than influxdb-java to push only metrics.

## Dependencies

* slf4j-api for logging.
* metrics-core, to provide, if you use InfluxdbReporter.

## Installation & dependency
```
mvn clean install
```

```xml
<dependency>
	<groupId>com.pretius</groupId>
	<artifactId>metrics-influxdb</artifactId>
	<version>0.9-SNAPSHOT</version>
</dependency>
```

## Usage sample
```java
private static InfluxdbReporter startInfluxdbReporter(MetricRegistry registry) throws Exception {
	InfluxdbHttpConfig config = new InfluxdbHttpConfig();
	config.setHost("127.0.0.1");
	config.setPort(8086);
	config.setDatabase("db");
	
	final InfluxdbHttp influxdb = new InfluxdbHttp(config); // http transport
	
	// final InfluxdbUdp influxdb = new InfluxdbUdp("127.0.0.1", 8089); // udp transport
	
	influxdb.setDebug(true); // to log processing series
	
	influxdb.addTag("application", "app"); // only for 0.9.1+
	influxdb.addTag("instance", "instance1");
	
	final InfluxdbReporter reporter = InfluxdbReporter
			.forRegistry(registry)
			.prefixedWith("test")
			.convertRatesTo(TimeUnit.SECONDS)
			.convertDurationsTo(TimeUnit.MILLISECONDS)
			.filter(MetricFilter.ALL)
			.skipIdleMetrics(true) // Only report metrics that have changed.
			.build(influxdb);
	reporter.start(10, TimeUnit.SECONDS);
	return reporter;
}
```

## Field names change
To avoid wrapping in double quotes in queries, field names now starts with letter and doesn't contain dashes.
The change affects following fields:

| old name | new name |
| -------- | -------- |
| std-dev | std_dev |
| 50-percentile | percentile_50 |
| 75-percentile | percentile_75 |
| 95-percentile | percentile_95 |
| 99-percentile | percentile_99 |
| 999-percentile | percentile_999 |
| one-minute | one_minute |
| five-minute | five_minute |
| fifteen-minute | fifteen_minute |
| mean-rate | mean_rate |
| run-count | run_count |

<p xmlns:dct="http://purl.org/dc/terms/">
  <a rel="license"
     href="http://creativecommons.org/publicdomain/zero/1.0/">
    <img src="http://i.creativecommons.org/p/zero/1.0/88x31.png" style="border-style: none;" alt="CC0" />
  </a>
  <br />
  To the extent possible under law,
  <a rel="dct:publisher"
     href="https://github.com/orgs/novaquark">
    <span property="dct:title">Novaquark</span></a>
  has waived all copyright and related or neighboring rights to
  this work.
</p>

