package pisi.unitedmeows.yystal.logger;

public interface ILogger {
	void warn(String text);

	void info(String text);

	void print(String text);

	void fatal(String text);

	void debug(String text);

	void warn(String text, Object... args);

	void info(String text, Object... args);

	void print(String text, Object... args);

	void fatal(String text, Object... args);

	void debug(String text, Object... args);

	void log(Enum<?> type, String text);
}
