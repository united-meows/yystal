package pisi.unitedmeows.yystal.networking;


import pisi.unitedmeows.yystal.clazz.prop;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPAddress {

	public static final IPAddress LOOPBACK = new IPAddress("127.0.0.1");
	public static final IPAddress ANY = new IPAddress("0.0.0.0");

	private final String address;
	public prop<InetAddress> inetAddress = new prop<InetAddress>(null) {
		@Override
		public InetAddress get() {
			if (value == null) {
				try {
					value = InetAddress.getByName(getAddress());
				} catch (UnknownHostException e) {

				}
			}
			return value;
		}

		@Deprecated
		@Override
		public void set(InetAddress newValue) {}
	};


	protected IPAddress(String _address) {
		address = _address;
	}

	public static IPAddress from(String address) {
		return new IPAddress(address);
	}

	public static IPAddress parse(String address) {
		try {
			return new IPAddress(InetAddress.getByName(address).getHostAddress());
		} catch (UnknownHostException e) {
			return null;
		}
	}

	public String getAddress() {
		return address;
	}

}
