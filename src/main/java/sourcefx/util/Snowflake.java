package sourcefx.util;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Snowflake {
	private static final Snowflake instance = new Snowflake(0L);

	private final long datacenterIdBits = 10L;
	private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
	private final long sequenceBits = 12L;

	private final long datacenterIdShift = sequenceBits;
	private final long timestampLeftShift = sequenceBits + datacenterIdBits;
	private final long sequenceMask = -1L ^ (-1L << sequenceBits);

	private final long twepoch = 1288834974657L;
	private long datacenterId;

	private volatile long lastTimestamp = -1L;
	private volatile long sequence = 0L;

	public static Snowflake getInstance() {
		return instance;
	}

	public Snowflake(long datacenterId) {
		if (datacenterId == 0) {
			try {
				this.datacenterId = getDatacenterId();
			} catch (SocketException | UnknownHostException | NullPointerException e) {
				log.warn("SNOWFLAKE: could not determine machine address; using random datacenter ID");
				Random rnd = new Random();
				this.datacenterId = rnd.nextInt((int) maxDatacenterId) + 1;
			}
		} else {
			this.datacenterId = datacenterId;
		}

		if (this.datacenterId > maxDatacenterId || datacenterId < 0) {
			log.warn("SNOWFLAKE: datacenterId > maxDatacenterId; using random datacenter ID");
			Random rnd = new Random();
			this.datacenterId = rnd.nextInt((int) maxDatacenterId) + 1;
		}
		log.info("SNOWFLAKE: initialised with datacenter ID {}", this.datacenterId);
	}

	protected long tilNextMillis(long lastTimestamp) {
		long timestamp = System.currentTimeMillis();
		while (timestamp <= lastTimestamp) {
			timestamp = System.currentTimeMillis();
		}
		return timestamp;
	}

	protected long getDatacenterId() throws SocketException, UnknownHostException {
		NetworkInterface network = null;

		Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
		while (en.hasMoreElements()) {
			NetworkInterface nint = en.nextElement();
			if (!nint.isLoopback() && nint.getHardwareAddress() != null) {
				network = nint;
				break;
			}
		}

		byte[] mac = network.getHardwareAddress();

		Random rnd = new Random();
		byte rndByte = (byte) (rnd.nextInt() & 0x000000FF);

		// take the last byte of the MAC address and a random byte as datacenter ID
		return ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) rndByte) << 8))) >> 6;
	}

	public synchronized long next() {
		long timestamp = System.currentTimeMillis();
		if (timestamp < lastTimestamp) {
			log.warn("Clock moved backwards. Refusing to generate id for {} milliseconds.", (lastTimestamp - timestamp));
			try {
				Thread.sleep((lastTimestamp - timestamp));
			} catch (InterruptedException e) {
			}
		}
		if (lastTimestamp == timestamp) {
			sequence = (sequence + 1) & sequenceMask;
			if (sequence == 0) {
				timestamp = tilNextMillis(lastTimestamp);
			}
		} else {
			sequence = 0;
		}
		lastTimestamp = timestamp;
		long id = ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | sequence;

		if (id < 0) {
			log.warn("ID is smaller than 0: {}", id);
		}
		return id;
	}
}