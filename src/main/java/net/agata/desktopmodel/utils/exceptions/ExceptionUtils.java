package net.agata.desktopmodel.utils.exceptions;

public class ExceptionUtils {

    private ExceptionUtils() {
	super();
    }

    public static void throwIllegalArgumentException(String message, Object... args) {
	throw new IllegalArgumentException(String.format(message, args));
    }
}
