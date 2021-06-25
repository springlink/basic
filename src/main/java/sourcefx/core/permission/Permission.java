package sourcefx.core.permission;

import lombok.Value;

@Value
public class Permission {
	private final String name;

	private final String title;

	private final String description;
}
