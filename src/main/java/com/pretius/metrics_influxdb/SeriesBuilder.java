package com.pretius.metrics_influxdb;

import java.util.Map;

interface SeriesBuilder {

	/**
	 * Returns true if this builder has series data to send.
	 */
	public abstract boolean hasSeriesData();

	/**
	 * Forget previous appendSeries.
	 */
	public abstract void reset();

	/**
	 * Append series of data into the next Request to send.
	 */
	public abstract void appendSeries(String namePrefix, String name, String nameSuffix,
                                      String[] columns, Object[][] points, Map<String, String> tags);
}