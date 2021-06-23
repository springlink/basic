package sourcefx.core.translate;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TranslationMetadata {
	private final Class<?> targetType;

	private final Map<String, Property> properties;

	private final List<Map<String, Phase>> indexedPhases;

	public static TranslationMetadata forType(Class<?> targetType) {
		Map<String, Property> props = new LinkedHashMap<>();
		for (PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(targetType)) {
			props.put(pd.getName(), Property.resolve(targetType, pd));
		}
		List<Map<String, Phase>> indexedPhases = new ArrayList<>();
		for (int i = 0;; i++) {
			Map<String, Phase> phases = new LinkedHashMap<>();
			for (Property prop : props.values()) {
				if (prop.getPhases().size() > i) {
					phases.put(prop.getName(), prop.getPhases().get(i));
				}
			}
			if (phases.isEmpty()) {
				break;
			}
			indexedPhases.add(Collections.unmodifiableMap(phases));
		}
		return new TranslationMetadata(
				targetType,
				Collections.unmodifiableMap(props),
				Collections.unmodifiableList(indexedPhases));
	}

	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
	public static class Property {
		private final String name;

		private final Class<?> type;

		private final List<Phase> phases;

		static Property resolve(Class<?> targetType, PropertyDescriptor pd) {
			Translate anno = findAnnotation(Translate.class,
					findPropertyField(targetType, pd),
					pd.getWriteMethod());
			return new Property(pd.getName(), pd.getPropertyType(),
					Phase.parse(pd.getName(), anno != null ? anno.value() : ""));
		}

		static Field findPropertyField(Class<?> type, PropertyDescriptor pd) {
			try {
				return type.getDeclaredField(pd.getName());
			} catch (NoSuchFieldException e) {
				return null;
			}
		}

		static <T extends Annotation> T findAnnotation(Class<T> annoType, AnnotatedElement... elements) {
			for (AnnotatedElement element : elements) {
				if (element == null) {
					continue;
				}
				T annotation = element.getDeclaredAnnotation(annoType);
				if (annotation != null) {
					return annotation;
				}
			}
			return null;
		}
	}

	@Getter
	@EqualsAndHashCode
	@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
	public static class Phase {
		private final String translatorName;

		private final String translatorOption;

		private final boolean iterative;

		static List<Phase> parse(String propertyName, String def) {
			List<Phase> phases = new ArrayList<>();
			int start = 0, pos = 0;
			for (; pos <= def.length(); pos++) {
				if (pos < def.length() && def.charAt(pos) != '.') {
					continue;
				}
				String text = def.substring(start, pos).trim();
				if (text.isEmpty()) {
					if (phases.isEmpty()) {
						phases.add(new Phase("key", propertyName, false));
					}
					start = pos + 1;
					continue;
				}
				if (text.matches("^[^()]+$")) {
					phases.add(new Phase("key", text, false));
					start = pos + 1;
					continue;
				}
				if (text.matches("^[^(]+\\(.*\\)$")) {
					int leftBrace = text.indexOf('(');
					int rightBrace = text.lastIndexOf(')');
					String name = text.substring(0, leftBrace).trim();
					boolean iterative = false;
					if (name.endsWith("*")) {
						name = name.substring(0, name.length() - 1);
						iterative = true;
					}
					phases.add(new Phase(name, text.substring(leftBrace + 1, rightBrace), iterative));
					start = pos + 1;
				}
			}
			if (start != pos) {
				throw new IllegalArgumentException("Invalid translation definition [" + def + "]");
			}
			return Collections.unmodifiableList(phases);
		}
	}
}
