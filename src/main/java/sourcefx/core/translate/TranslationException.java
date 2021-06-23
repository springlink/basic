package sourcefx.core.translate;

public class TranslationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public TranslationException() {
	}

	public TranslationException(String message) {
		super(message);
	}

	public TranslationException(Throwable cause) {
		super(cause);
	}

	public TranslationException(String message, Throwable cause) {
		super(message, cause);
	}
}
