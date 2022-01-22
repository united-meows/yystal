package test.yystal;

import pisi.unitedmeows.yystal.utils.Capsule;
import pisi.unitedmeows.yystal.utils.CoID;
import pisi.unitedmeows.yystal.utils.kThread;


import static pisi.unitedmeows.yystal.parallel.Async.*;
import static pisi.unitedmeows.yystal.YYStal.*;

public enum YTestStart {
	gaming; /* :D */

	public static void main(final String[] args) {
		System.out.println(CoID.generate());
		Capsule capsule = Capsule.of(pair("hello", 5), pair("var2", "test3"), pair("token", 12));

		System.out.println((String)capsule.get("hello"));
		capsule.ifExists("var2", o -> {
			System.out.println("var2 exists");
		});

		kThread.sleep(1099900);
	}


}