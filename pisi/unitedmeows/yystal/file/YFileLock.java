package pisi.unitedmeows.yystal.file;

import pisi.unitedmeows.yystal.exception.YExManager;
import pisi.unitedmeows.yystal.exception.impl.YexIO;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class YFileLock {

	private File file;
	private FileLock lock;

	public YFileLock(File _file) {
		file = _file;
	}

	public YFileLock(YFile yFile) {
		this(yFile.file());
	}

	public boolean lock() {
		FileChannel channel;
		try {
			channel = new RandomAccessFile(file, "rw").getChannel();
		} catch (IOException ex) {
			YExManager.pop(new YexIO("Couldn't get the file channel " + file.getAbsolutePath()));
			return false;
		}

		try {
			lock = channel.lock();
			return true;
		} catch (IOException e) {
			YExManager.pop(new YexIO("Couldn't lock the file " + file.getAbsolutePath()));
			return false;
		}
	}

	public boolean free() {
		try {
			lock.close();
			return true;
		} catch (Exception ex) {
			YExManager.pop(new YexIO("Couldn't free the file " + file.getAbsolutePath()));
			return false;
		}
	}
}
