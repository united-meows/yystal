package pisi.unitedmeows.yystal.logger.impl;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import pisi.unitedmeows.yystal.file.YFile;
import pisi.unitedmeows.yystal.logger.ILogger;

import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

public class YLogger implements ILogger {

	private Time time = Time.DAY_MONTH_YEAR_FULL;
	private final Class<?> clazz;
	private String name;
	private boolean prefix;
	private boolean save;
	private int bufferSize;
	private YFile file;
	private String[] buffer;
	private boolean colored = false;
	private int bufferIndex;
	private static final Ansi.Color FATAL_COLOR;

	static {
		final String os = System.getProperty("os.name").toLowerCase();
		boolean linux = (os.contains("nix") || os.contains("nux") || os.contains("aix"));
		if (linux) {
			FATAL_COLOR = Ansi.Color.MAGENTA;
		} else {
			FATAL_COLOR = Ansi.Color.RED;
			/* todo: ghost check this (on linux MAGENTA makes RED background im not sure if it's same for windows)*/
		}
	}

	public YLogger(final Class<?> _clazz) {
		clazz = _clazz;
		name = "[" + clazz.getSimpleName().toUpperCase(Locale.ROOT) + "]";
	}

	public YLogger(final Class<?> _clazz, final String _name) {
		clazz = _clazz;
		name = "[" + _name + "]";
	}


	private void post(String text) {
		if (save) {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("[").append(generateTime()).append("] ");
			if (prefix) {
				stringBuilder.append(name).append(" ");
			}
			stringBuilder.append(text);


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
			System.out.print(Ansi.ansi().eraseScreen().fg(Ansi.Color.CYAN).a("[").a(generateTime()).a("] "));

			if (prefix) {
				System.out.print(name);
				System.out.print(" ");
			}
			System.out.print(Ansi.ansi().fg(Ansi.Color.GREEN).a("[INFO] "));
			System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(text));
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
		post("[INFO] " + text);
	}

	@Override
	public void warn(String text) {
		if (colored) {
			System.out.print(Ansi.ansi().eraseScreen().fg(Ansi.Color.CYAN).a("[").a(generateTime()).a("] "));

			if (prefix) {
				System.out.print(name);
				System.out.print(" ");
			}
			System.out.print(Ansi.ansi().fg(Ansi.Color.YELLOW).a("[WARN] "));
			System.out.println(Ansi.ansi().fg(Ansi.Color.YELLOW).a(text));
			Ansi.ansi().reset();
		} else {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("[").append(generateTime()).append("] ");
			if (prefix) {
				stringBuilder.append(name).append(" ");
			}
			stringBuilder.append("[WARN] ");
			stringBuilder.append(text);
			System.out.println(stringBuilder);
		}
		post("[WARN] " + text);
	}

	@Override
	public void print(String text) {

	}

	@Override
	public void fatal(String text) {
		if (colored) {
			System.out.print(Ansi.ansi().eraseScreen().bg(FATAL_COLOR).fg(Ansi.Color.BLACK).bold().a("[").a(generateTime()).a("] "));

			if (prefix) {
				System.out.print(name);
				System.out.print(" ");
			}
			System.out.print(Ansi.ansi().fg(Ansi.Color.BLACK).a("[FATAL] "));
			System.out.println(Ansi.ansi().fg(Ansi.Color.BLACK).a(text));
			System.out.print(Ansi.ansi().bgDefault());
			Ansi.ansi().reset();
		} else {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("[").append(generateTime()).append("] ");
			if (prefix) {
				stringBuilder.append(name).append(" ");
			}
			stringBuilder.append("[FATAL] ");
			stringBuilder.append(text);
			System.out.println(stringBuilder.toString());
		}
		post("[FATAL] " + text);
	}

	@Override
	public void debug(String text) {
		if (colored) {
			System.out.print(Ansi.ansi().eraseScreen().fg(Ansi.Color.CYAN).a("[").a(generateTime()).a("] "));

			if (prefix) {
				System.out.print(name);
				System.out.print(" ");
			}
			System.out.print(Ansi.ansi().fg(Ansi.Color.CYAN).a("[DEBUG] "));
			System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).a(text));
			Ansi.ansi().reset();
		} else {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("[").append(generateTime()).append("] ");
			if (prefix) {
				stringBuilder.append(name).append(" ");
			}
			stringBuilder.append("[DEBUG] ");
			stringBuilder.append(text);
			System.out.println(stringBuilder.toString());
		}
		post("[DEBUG] " + text);
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
			case YEAR_MONTH_DAY_FULL: {
				LocalDateTime localDateTime = LocalDateTime.now();
				return localDateTime.getYear() + "-" + localDateTime.getMonthValue() + "-"
						+ localDateTime.getDayOfMonth() + " " + localDateTime.getHour() + ":" + localDateTime.getMinute() + ":" + localDateTime.getSecond();
			}
			case DAY_MONTH_YEAR_FULL: {
				LocalDateTime localDateTime = LocalDateTime.now();
				return localDateTime.getDayOfMonth() + "-" + localDateTime.getMonthValue() + "-"
						+ localDateTime.getYear() + " " + localDateTime.getHour() + ":" + localDateTime.getMinute() + ":" + localDateTime.getSecond();
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

			boolean installed = false;
			try {
				installed = AnsiConsole.isInstalled();
			} catch (Exception ex3) {
				try {
					final Field installedField = AnsiConsole.class.getDeclaredField("installed");
					installedField.setAccessible(true);
					installed = ((int) installedField.get(null) > 0);
				} catch (Exception ex) {

				}
			}


			if (!installed) {
				System.setProperty("jansi.passthrough", "true");
				System.setProperty("org.jline.terminal.dumb", "true");
				AnsiConsole.systemInstall();
			}

		}
		return this;
	}


	public YLogger outputToFile(File file, int buffer) {
		return outputToFile(new YFile(file), buffer);
	}

	public YLogger outputToFile(File file) {
		return outputToFile(new YFile(file), 1);
	}
	public YLogger outputToFile(YFile file) {
		return outputToFile(file, 1);
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
		DAY_MONTH_YEAR_FULL,
		YEAR_MONTH_DAY_FULL,
		NO_TIME
	}
}
