package sourcefx.core.translate;

import java.util.Collection;

public interface Translator {
	void translate(Collection<ValueAndResult> values) throws Exception;
}
