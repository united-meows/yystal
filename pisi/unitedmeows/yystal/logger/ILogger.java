package pisi.unitedmeows.yystal.logger;

public interface ILogger {
	void warn(String text);
	void info(String text);
	void print(String text);
	void fatal(String text);
	void debug(String text);
	void log(Enum<?> type, String text);
}
