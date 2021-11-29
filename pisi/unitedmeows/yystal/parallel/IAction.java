package pisi.unitedmeows.yystal.parallel;

@FunctionalInterface
public interface IAction {
	void execute(Future<?> future);
}
