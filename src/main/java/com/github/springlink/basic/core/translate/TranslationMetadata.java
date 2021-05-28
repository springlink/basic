package com.github.springlink.basic.core.translate;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TranslationMetadata {
	private static final Map<Class<?>, TranslationMetadata> resolvedCache = new ConcurrentHashMap<>();

	private Class<?> targetType;

	private List<TargetProperty> targetProperties;

	private List<List<PhaseDefinition>> phaseGroups;

	private Map<String, Set<String>> translatorConfigSets;

	public static TranslationMetadata forTargetType(Class<?> targetType) {
		return resolvedCache.computeIfAbsent(targetType, type -> resolve(type));
	}

	static TranslationMetadata resolve(Class<?> targetType) {
		List<TargetProperty> targetProperties = new ArrayList<>();
		List<List<PhaseDefinition>> phaseGroups = new ArrayList<>();
		Map<String, Set<String>> translatorConfigSets = new HashMap<>();
		for (PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(targetType)) {
			Method setter = pd.getWriteMethod();
			if (setter == null) {
				continue;
			}

			Field field = findPropertyField(targetType, pd);
			Translate anno = findAnnotation(Translate.class, field, setter);

			TargetProperty property = new TargetProperty(pd.getName(), pd.getPropertyType());
			targetProperties.add(property);

			List<String[]> defs = parseDefinition(pd.getName(), anno != null ? anno.value() : "");
			for (int i = 0; i < defs.size(); i++) {
				String name = defs.get(i)[0];
				String config = defs.get(i)[1];
				boolean iterative = false;
				if (name.endsWith("*")) {
					iterative = true;
					name = name.substring(0, name.length() - 1);
				}
				translatorConfigSets.computeIfAbsent(name, k -> new HashSet<>()).add(config);

				if (phaseGroups.size() <= i) {
					phaseGroups.add(new ArrayList<>());
				}
				phaseGroups.get(i).add(new PhaseDefinition(property, name, config, iterative));
			}
		}

		phaseGroups = phaseGroups.stream()
				.map(Collections::unmodifiableList)
				.collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

		translatorConfigSets = translatorConfigSets.entrySet().stream()
				.collect(Collectors.collectingAndThen(
						Collectors.toMap(
								entry -> entry.getKey(),
								entry -> Collections.unmodifiableSet(entry.getValue())),
						Collections::unmodifiableMap));

		targetProperties = Collections.unmodifiableList(targetProperties);

		return new TranslationMetadata(targetType, targetProperties, phaseGroups, translatorConfigSets);
	}

	static List<String[]> parseDefinition(String propName, String def) {
		List<String[]> defs = new ArrayList<>();
		int start = 0, pos = 0;
		for (; pos <= def.length(); pos++) {
			if (pos < def.length() && def.charAt(pos) != '.') {
				continue;
			}
			String text = def.substring(start, pos).trim();
			if (text.isEmpty()) {
				if (defs.isEmpty()) {
					defs.add(new String[] { "key", propName });
				}
				start = pos + 1;
				continue;
			}
			if (text.matches("^[^()]+$")) {
				defs.add(new String[] { "key", text });
				start = pos + 1;
				continue;
			}
			if (text.matches("^[^(]+\\(.*\\)$")) {
				int leftBrace = text.indexOf('(');
				int rightBrace = text.lastIndexOf(')');
				defs.add(new String[] {
						text.substring(0, leftBrace).trim(),
						text.substring(leftBrace + 1, rightBrace)
				});
				start = pos + 1;
			}
		}
		if (start != pos) {
			throw new IllegalArgumentException("Invalid translation definition [" + def + "]");
		}
		return defs;
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
			T annotation = element.getDeclaredAnnotation(annoType);
			if (annotation != null) {
				return annotation;
			}
		}
		return null;
	}

	@Value
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class PhaseDefinition {
		private TargetProperty targetProperty;

		private String translator;

		private String config;

		private boolean iterative;
	}

	@Value
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class TargetProperty {
		private String name;

		private Class<?> type;
	}
}
