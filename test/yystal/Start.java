package test.yystal;

import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.client.YUdpClient;
import pisi.unitedmeows.yystal.networking.events.CUdpDataReceived;
import pisi.unitedmeows.yystal.ui.YWindow;
import pisi.unitedmeows.yystal.utils.kThread;

import java.nio.charset.StandardCharsets;

import static pisi.unitedmeows.yystal.parallel.Async.*;

public enum Start {
	gaming; /* :D */

	public static void main(final String[] args) {
		YUdpClient yUdpClient = new YUdpClient();
		yUdpClient.connect(IPAddress.parse("djxmmx.net"), 17);
		yUdpClient.onDataReceived.bind(new CUdpDataReceived() {
			@Override
			public void onDataReceived(byte[] data) {
				System.out.println("<<" + new String(data));
			}
		});


		kThread.sleep(1000);
		
		while (true) {
			yUdpClient.send("test data".getBytes(StandardCharsets.UTF_8));
			kThread.sleep(100);
		}


	}


}