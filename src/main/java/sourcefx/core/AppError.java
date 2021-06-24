package sourcefx.core;

public enum AppError {
	ENTITY_NOT_FOUND;

	public AppException newException() {
		return new AppException(name());
	}
}
