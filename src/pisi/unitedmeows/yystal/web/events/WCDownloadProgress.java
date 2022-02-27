package pisi.unitedmeows.yystal.web.events;

import pisi.unitedmeows.yystal.clazz.delegate;

/* Download Progress Event only works when server has set Content-Length header
 * otherwise the only correct value will be currentBytes variable */
@Deprecated
public interface WCDownloadProgress extends delegate {
	@Deprecated
	public void onProgress(double percent, long currentBytes, long totalBytes);
}
