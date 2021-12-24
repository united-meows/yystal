package pisi.unitedmeows.yystal.logger.impl;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.lwjglx.Sys;
import pisi.unitedmeows.yystal.file.YFile;
import pisi.unitedmeows.yystal.logger.ILogger;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

public class YLogger implements ILogger {

	private Time time = Time.MILLISECONDS;
	private final Class<?> clazz;
	private String name;
	private boolean prefix;
	private boolean save;
	private int bufferSize;
	private YFile file;
	private String[] buffer;
	private boolean colored = false;
	private int bufferIndex;

	public YLogger(final Class<?> _clazz) {
		clazz = _clazz;
		name = "[" + clazz.getSimpleName().toUpperCase(Locale.ROOT) + "]";
	}

	public YLogger(final Class<?> _clazz, final String _name) {
		clazz = _clazz;
		name = "[" + _name + "]";
	}


	private void post(String text) {
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[").append(generateTime()).append("] ");
		if (prefix) {
			stringBuilder.append(name).append(" ");
		}
		stringBuilder.append(text);

		if (save) {
			buffer[bufferIndex++] = stringBuilder.toString();
			if (bufferIndex >= bufferSize) {
				flush();
			}
		}
	}

	private void flush() {
		file.write(buffer);
		bufferIndex = 0;
	}

	@Override
	public void info(String text) {
		if (colored) {
			System.out.print(Ansi.ansi().eraseScreen().fg(Ansi.Color.YELLOW).a("[").a(generateTime()).a("] "));

			if (prefix) {
				System.out.print(name);
				System.out.print(" ");
			}
			System.out.print("[INFO] ");
			System.out.println(text);
			Ansi.ansi().reset();
		} else {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("[").append(generateTime()).append("] ");
			if (prefix) {
				stringBuilder.append(name).append(" ");
			}
			stringBuilder.append("[INFO] ");
			stringBuilder.append(text);
			System.out.println(stringBuilder.toString());
		}
		post(text);
	}

	@Override
	public void warn(String text) {

	}

	@Override
	public void print(String text) {

	}

	@Override
	public void fatal(String text) {

	}

	@Override
	public void debug(String text) {

	}

	@Override
	public void log(Enum<?> type, String text) {
		/* do nothing */
	}

	private String generateTime() {
		switch (time) {
			case MILLISECONDS:
				return String.valueOf(System.currentTimeMillis());

			case HOUR_MINUTES: {
				LocalDateTime localDateTime = LocalDateTime.now();
				return localDateTime.getHour() + ":" + localDateTime.getMinute();
			}
			case HOUR_MINUTES_SECONDS: {
				LocalDateTime localDateTime = LocalDateTime.now();
				return localDateTime.getHour() + ":" + localDateTime.getMinute() + ":" + localDateTime.getSecond();
			}

			case HOUR: {
				return String.valueOf(LocalDateTime.now().getHour());
			}

			default:
				return "";
		}
	}

	public YLogger setTime(Time time) {
		this.time = time;
		return this;
	}

	public YLogger setColored(boolean state) {
		this.colored = state;
		if (state) {
			if (!AnsiConsole.isInstalled()) {
				AnsiConsole.systemInstall();
			}
		}
		return this;
	}


	public YLogger outputToFile(File file, int buffer) {
		return outputToFile(new YFile(file), buffer);
	}

	public YLogger outputToFile(YFile file, int bufferSize) {
		this.file = file;
		if (file.file().exists())
			file.delete();

		this.save = true;
		this.bufferSize = bufferSize;
		if (buffer == null) {
			buffer = new String[bufferSize];
		}
		return this;
	}

	public YLogger stopOutput() {
		flush();
		save = false;
		return this;
	}


	public YLogger setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
		if (buffer != null) {
			flush();
		}
		buffer = new String[bufferSize];
		return this;
	}

	public YLogger setPrefix(String value) {
		this.prefix = true;
		name = "[" + value + "]";
		return this;
	}

	public enum Time {
		MILLISECONDS,
		HOUR_MINUTES,
		HOUR_MINUTES_SECONDS,
		HOUR,
		NO_TIME
	}
}
