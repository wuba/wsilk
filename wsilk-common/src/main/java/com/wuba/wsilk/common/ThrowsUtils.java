package com.wuba.wsilk.common;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ThrowsUtils {

	public static String string(Exception e) {
		String error = null;
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			error = sw.toString();
			sw.close();
			pw.close();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return error;
	}

}
