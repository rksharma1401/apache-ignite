package com.learn.ignitedemo;

import java.util.Collections;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

public class IgniteApp {

	private static final String MY_CACHE = "myCache";
	static {
		System.setProperty("IGNITE_JETTY_PORT", System.getProperty("PORT"));
	}

	public static void main(String[] args) {
		try {
			// Preparing IgniteConfiguration using Java APIs
			IgniteConfiguration cfg = new IgniteConfiguration();

			// The node will be started as a client node.
			// cfg.setClientMode(true);

			// Classes of custom Java logic will be transferred over the wire from this app.
			cfg.setPeerClassLoadingEnabled(true);

			// set working directory
			// cfg.setWorkDirectory("/home/WORLDPAY.LOCAL/sharmar250/tools/apache-ignite-2.9.1-bin/work");

			// Setting up an IP Finder to ensure the client can locate the servers.
			TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
			ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
			cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));

			// Starting the node
			Ignite ignite = Ignition.start(cfg);

			// Cache configuration to set properties of cache
			CacheConfiguration<Integer, String> cacheCfg = new CacheConfiguration<>();
			cacheCfg.setCacheMode(CacheMode.REPLICATED);
			cacheCfg.setName(MY_CACHE);
			cacheCfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL_SNAPSHOT);

			ignite.addCacheConfiguration(cacheCfg);

			// Create an IgniteCache and put some values in it.
			IgniteCache<Integer, String> cache = ignite.getOrCreateCache(cacheCfg);

			cache.put(1, "Hello");
			cache.put(2, "World!");

			System.out.println(">> Created the cache and add the values.");

			// Cache configuration to set properties of cache
			CacheConfiguration<String, String> strCacheCfg = new CacheConfiguration<>();
			strCacheCfg.setCacheMode(CacheMode.REPLICATED);
			strCacheCfg.setName("newCache");
			strCacheCfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL_SNAPSHOT);

			ignite.addCacheConfiguration(strCacheCfg);

			// Create an IgniteCache and put some values in it.
			IgniteCache<String, String> strCache = ignite.getOrCreateCache(strCacheCfg);

			strCache.put("testKey", "Hello");

			System.out.println(">> Created the cache and add the values.");

			// Executing custom Java compute task on server nodes.
			ignite.compute(ignite.cluster().forServers()).broadcast(new RemoteTask());

			System.out.println(">> Compute task is executed, check for output on the server nodes.");

			try {
				Thread.sleep(1000 * 1000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Disconnect from the cluster.
			ignite.close();
		} catch (IgniteException e) {
		}
	}

	/**
	 * A compute tasks that prints out a node ID and some details about its OS and JRE. Plus, the code
	 * shows how to access data stored in a cache from the compute task.
	 */
	private static class RemoteTask implements IgniteRunnable {
		private static final long serialVersionUID = 1L;
		@IgniteInstanceResource
		Ignite ignite;

		@Override
		public void run() {
			while (true) {
				System.out.println(">> Executing the compute task");
				System.out.println("   Node ID: " + ignite.cluster().localNode().id() + "\n" + "   OS: " + System.getProperty("os.name") + "   JRE: " + System.getProperty("java.runtime.name"));
				IgniteCache<Integer, String> cache = ignite.cache("newCache");

				cache.forEach(e -> System.out.println(e.getKey() + ":" + e.getValue()));

				try {
					Thread.sleep(5000l);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}