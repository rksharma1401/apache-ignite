package com.learn.ignitedemo;

import java.util.Collections;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

import com.learn.ignitedemo.objects.StringObjects;

public class IgnitClient implements Runnable {
	private static final String OBJ_CACHE = "objCache";

	public static void main(String[] args) throws IgniteException {
		// Preparing IgniteConfiguration using Java APIs
		IgniteConfiguration cfg = new IgniteConfiguration();

		// The node will be started as a client node.
		cfg.setClientMode(true);

		// Classes of custom Java logic will be transferred over the wire from this app.
		cfg.setPeerClassLoadingEnabled(true);

		// set working directory
		cfg.setWorkDirectory("/home/WORLDPAY.LOCAL/sharmar250/tools/apache-ignite-2.9.1-bin/w");

		// Setting up an IP Finder to ensure the client can locate the servers.
		TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
		ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
		cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

		// Starting the node
		Ignite ignite = Ignition.start(cfg);

		// get an IgniteCache
		IgniteCache<String, StringObjects> cache = ignite.cache(OBJ_CACHE);

		System.out.println(">> get the cache ");

		
		cache.put("xyz", new StringObjects("xyz", "alpha", "client"));
		int i = 0;
		while (i < 5000) {
			System.out.println(">> Printing cache value");
			cache.forEach(e -> System.out.println(e.getKey() + ":" + e.getValue()));
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

	@Override
	public void run() {
		main(null);

	}
}