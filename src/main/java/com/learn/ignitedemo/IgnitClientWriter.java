package com.learn.ignitedemo;

import java.util.Collections;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

public class IgnitClientWriter {
	private static final String MY_CACHE = "myCache";

	public static void main(String[] args) throws IgniteException {

		// Preparing IgniteConfiguration using Java APIs
		IgniteConfiguration cfg = new IgniteConfiguration();

		// The node will be started as a client node.
		cfg.setClientMode(true);

		// Classes of custom Java logic will be transferred over the wire from this app.
		cfg.setPeerClassLoadingEnabled(true);

		// set working directory
		// cfg.setWorkDirectory("/home/WORLDPAY.LOCAL/sharmar250/tools/apache-ignite-2.9.1-bin/w");

		// Setting up an IP Finder to ensure the client can locate the servers.
		TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
		ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
		cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

		// Starting the node
		Ignite ignite = Ignition.start(cfg);

		// Cache configuration to set properties of cache
		CacheConfiguration<String, String> cacheCfg = new CacheConfiguration<>();
		cacheCfg.setCacheMode(CacheMode.REPLICATED);
		cacheCfg.setName(MY_CACHE);
		ignite.addCacheConfiguration(cacheCfg);

		// Create an IgniteCache and put some values in it.
		IgniteCache<String, String> cache = ignite.getOrCreateCache(cacheCfg);

		System.out.println(">> Created the cache and now add the values.");
		int i = 0;
		while (i < 5000) {

			cache.put("1", i + " Hello");
			try {
				i++;
				Thread.sleep(1000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Disconnect from the cluster.
		ignite.close();
	}
}