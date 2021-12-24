package pisi.unitedmeows.yystal.networking.basic;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.clazz.event;
import pisi.unitedmeows.yystal.clazz.ref;
import pisi.unitedmeows.yystal.networking.IPAddress;
import pisi.unitedmeows.yystal.networking.events.SDataReceivedEvent;
import pisi.unitedmeows.yystal.networking.events.SEchoEvent;
import pisi.unitedmeows.yystal.networking.server.YSocketClient;
import pisi.unitedmeows.yystal.networking.server.YTcpServer;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

/* simply echo's the string data to client */
public class YTcpEchoServer {

	protected YTcpServer tcpServer;

	public event<SEchoEvent> echoEvent = new event<>();

	public YTcpEchoServer(IPAddress bindAddress, int port) {
		tcpServer = new YTcpServer(bindAddress, port);
		tcpServer.dataReceiveEvent.bind(new SDataReceivedEvent() {
			@Override
			public void onClientDataReceived(YSocketClient client, byte[] data) {
				if (data.length < 8192) {
					ref<Boolean> canceled = YYStal.reference(false);
					echoEvent.fire(client, data, canceled);
					if (!canceled.get()) {
						client.write(data);
					}
				}
			}
		});

	}

	public void close() {
		tcpServer.close();
	}

	public boolean listen() {
		return tcpServer.listen();
	}

	public YTcpServer tcpServer() {
		return tcpServer;
	}

	/* maybe put this in a util class */
	protected static boolean looksLikeUTF8(byte[] utf8)
	{
		Pattern p = Pattern.compile("\\A(\n" +
				"  [\\x09\\x0A\\x0D\\x20-\\x7E]             # ASCII\\n" +
				"| [\\xC2-\\xDF][\\x80-\\xBF]               # non-overlong 2-byte\n" +
				"|  \\xE0[\\xA0-\\xBF][\\x80-\\xBF]         # excluding overlongs\n" +
				"| [\\xE1-\\xEC\\xEE\\xEF][\\x80-\\xBF]{2}  # straight 3-byte\n" +
				"|  \\xED[\\x80-\\x9F][\\x80-\\xBF]         # excluding surrogates\n" +
				"|  \\xF0[\\x90-\\xBF][\\x80-\\xBF]{2}      # planes 1-3\n" +
				"| [\\xF1-\\xF3][\\x80-\\xBF]{3}            # planes 4-15\n" +
				"|  \\xF4[\\x80-\\x8F][\\x80-\\xBF]{2}      # plane 16\n" +
				")*\\z", Pattern.COMMENTS);

		String phonyString = null;
		try {
			phonyString = new String(utf8, "ISO-8859-1");
		} catch(UnsupportedEncodingException e) {
			return false;
		}
		return p.matcher(phonyString).matches();
	}

}
