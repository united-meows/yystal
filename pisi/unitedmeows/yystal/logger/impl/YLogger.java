package pisi.unitedmeows.yystal.logger.impl;

import static java.lang.System.*;
import static java.util.Locale.*;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import pisi.unitedmeows.yystal.file.YFile;
import pisi.unitedmeows.yystal.logger.ILogger;

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
	private static final Ansi.Color FATAL_COLOR = RED;

	public YLogger(final Class<?> _clazz) {
		clazz = _clazz;
		name = "[" + clazz.getSimpleName().toUpperCase(ROOT) + "]";
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
			out.print(ansi().eraseScreen().fg(CYAN).a("[").a(generateTime()).a("] "));
			if (prefix) {
				out.print(name);
				out.print(" ");
			}
			out.print(ansi().fg(GREEN).a("[INFO] "));
			out.println(ansi().fg(GREEN).a(text));
			ansi().reset();
		} else {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("[").append(generateTime()).append("] ");
			if (prefix) {
				stringBuilder.append(name).append(" ");
			}
			stringBuilder.append("[INFO] ");
			stringBuilder.append(text);
			out.println(stringBuilder.toString());
		}
		post("[INFO] " + text);
	}

	@Override
	public void warn(String text) {
		if (colored) {
			out.print(ansi().eraseScreen().fg(CYAN).a("[").a(generateTime()).a("] "));
			if (prefix) {
				out.print(name);
				out.print(" ");
			}
			out.print(ansi().fg(YELLOW).a("[WARN] "));
			out.println(ansi().fg(YELLOW).a(text));
			ansi().reset();
		} else {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("[").append(generateTime()).append("] ");
			if (prefix) {
				stringBuilder.append(name).append(" ");
			}
			stringBuilder.append("[WARN] ");
			stringBuilder.append(text);
			out.println(stringBuilder);
		}
		post("[WARN] " + text);
	}

	@Override
	public void print(String text) {}

	@Override
	public void fatal(String text) {
		if (colored) {
			out.print(ansi().eraseScreen().fg(CYAN).bold().a("[").a(generateTime()).a("] "));
			if (prefix) {
				out.print(name);
				out.print(" ");
			}
			out.print(ansi().bg(FATAL_COLOR).fg(BLACK).a("[FATAL] "));
			out.println(ansi().bg(FATAL_COLOR).fg(BLACK).a(text));
			out.print(ansi().bg(DEFAULT));
			ansi().reset();
		} else {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("[").append(generateTime()).append("] ");
			if (prefix) {
				stringBuilder.append(name).append(" ");
			}
			stringBuilder.append("[FATAL] ");
			stringBuilder.append(text);
			out.println(stringBuilder.toString());
		}
		post("[FATAL] " + text);
	}

	@Override
	public void debug(String text) {
		if (colored) {
			out.print(ansi().eraseScreen().fg(CYAN).a("[").a(generateTime()).a("] "));
			if (prefix) {
				out.print(name);
				out.print(" ");
			}
			out.print(ansi().fg(YELLOW).a("[DEBUG] "));
			out.println(ansi().fg(YELLOW).a(text));
			ansi().reset();
		} else {
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("[").append(generateTime()).append("] ");
			if (prefix) {
				stringBuilder.append(name).append(" ");
			}
			stringBuilder.append("[DEBUG] ");
			stringBuilder.append(text);
			out.println(stringBuilder.toString());
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
			return localDateTime.getYear() + "-" + localDateTime.getMonthValue() + "-" + localDateTime.getDayOfMonth() + " " + localDateTime.getHour() + ":" + localDateTime.getMinute() + ":"
						+ localDateTime.getSecond();
		}
		case DAY_MONTH_YEAR_FULL: {
			LocalDateTime localDateTime = LocalDateTime.now();
			return localDateTime.getDayOfMonth() + "-" + localDateTime.getMonthValue() + "-" + localDateTime.getYear() + " " + localDateTime.getHour() + ":" + localDateTime.getMinute() + ":"
						+ localDateTime.getSecond();
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
				final Field installedField = AnsiConsole.class.getDeclaredField("installed");
				installedField.setAccessible(true);
				installed = ((int) installedField.get(null) > 0);
			}
			catch (Exception ex) {
				ex.printStackTrace();
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
		if (file.file().exists()) {
			file.delete();
		}
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

	@Override
	public void warn(String text, Object... args) {
		warn(String.format(text, args));
	}

	@Override
	public void info(String text, Object... args) {
		info(String.format(text, args));
	}

	@Override
	public void print(String text, Object... args) {
		print(String.format(text, args));
	}

	@Override
	public void fatal(String text, Object... args) {
		fatal(String.format(text, args));
	}

	@Override
	public void debug(String text, Object... args) {
		debug(String.format(text, args));
	}
}
