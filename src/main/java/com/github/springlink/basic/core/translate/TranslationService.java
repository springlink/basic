package com.github.springlink.basic.core.translate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;

import com.github.springlink.basic.core.translate.TranslationMetadata.PhaseDefinition;
import com.github.springlink.basic.core.translate.TranslationMetadata.TargetProperty;
import com.github.springlink.basic.core.translate.basic.JoinTranslator;
import com.github.springlink.basic.core.translate.basic.KeyTranslator;
import com.github.springlink.basic.core.translate.basic.SplitTranslator;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConstructorUtils;

import lombok.RequiredArgsConstructor;

public class TranslationService {
	private final Map<String, Translator> translators = new HashMap<>();
	private final BeanUtilsBean beanUtils = new BeanUtilsBean();

	public TranslationService() {
		addTranslator("key", new KeyTranslator());
		addTranslator("split", new SplitTranslator());
		addTranslator("join", new JoinTranslator());
	}

	public synchronized void addTranslator(String name, Translator translator) {
		String trimName = name.trim();
		if (trimName.isEmpty()) {
			throw new IllegalArgumentException("Translator name cannot be blank");
		}
		if (translators.containsKey(trimName)) {
			throw new IllegalArgumentException("Translator [" + name + "] already exists");
		}
		translators.put(trimName, translator);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> translate(Class<T> targetType, List<?> values) {
		TranslationMetadata metadata = TranslationMetadata.forTargetType(targetType);
		List<TargetProperty> properties = metadata.getTargetProperties();
		Map<String, Set<String>> configSets = metadata.getTranslatorConfigSets();

		List<Object> results = new ArrayList<>(values.size());
		for (Object item : values) {
			Map<String, Object> map = new HashMap<>();
			properties.stream().map(TargetProperty::getName).forEach(name -> {
				map.put(name, item);
			});
			results.add(map);
		}

		Map<String, TranslatorContext> contexts = new HashMap<>();
		for (List<PhaseDefinition> phaseGroup : metadata.getPhaseGroups()) {
			for (PhaseDefinition phase : phaseGroup) {
				String name = phase.getTranslator();
				String config = phase.getConfig();
				TranslatorContext context = contexts.computeIfAbsent(name, k -> {
					TranslatorContext ctx = new TranslatorContextImpl(configSets.get(k));
					try {
						getTranslator(name).beforeTranslation(ctx);
					} catch (Exception e) {
						throw new TranslationException("Exception caught on '" + name + "' before translation", e);
					}
					return ctx;
				});

				String propertyName = phase.getTargetProperty().getName();
				for (Map<String, Object> result : (List<Map<String, Object>>) (List<?>) results) {
					Object value = unwrapValue(result.get(propertyName));
					try {
						value = translate(phase, context, config, value);
					} catch (Exception e) {
						throw new TranslationException("Failed to translate value on property [" + propertyName
								+ "] with [" + name + "(" + config + ")]", e);
					}
					result.put(propertyName, value);
				}
			}
		}
		for (int i = 0; i < results.size(); i++) {
			Map<String, Object> mapResult = (Map<String, Object>) results.get(i);
			T target = newInstance(targetType);
			for (TargetProperty property : properties) {
				Object value = unwrapValue(mapResult.get(property.getName()));
				setPropertyValue(target, property.getName(), value);
			}
			results.set(i, target);
		}
		contexts.forEach((translatorName, context) -> {
			try {
				getTranslator(translatorName).afterTranslation(context);
			} catch (Exception e) {
				throw new TranslationException("Exception caught on '" + translatorName + "' after translation", e);
			}
		});
		return (List<T>) results;
	}

	private Translator getTranslator(String name) {
		Translator translator = translators.get(name);
		if (translator == null) {
			throw new TranslationException("Unkown translator [" + name + "]");
		}
		return translator;
	}

	private void setPropertyValue(Object target, String name, Object value) {
		try {
			beanUtils.setProperty(target, name, value);
		} catch (Exception e) {
			throw new TranslationException("Failed to set target property", e);
		}
	}

	private Object unwrapValue(Object value) {
		if (value instanceof Supplier) {
			return ((Supplier<?>) value).get();
		}
		return value;
	}

	private <T> T newInstance(Class<T> targetType) {
		try {
			return ConstructorUtils.invokeConstructor(targetType, null);
		} catch (Exception e) {
			throw new TranslationException("Failed to create target instance", e);
		}
	}

	@SuppressWarnings("unchecked")
	public <E> Collection<E> newCollection(Class<?> collType, int capacity) {
		try {
			return (Collection<E>) ConstructorUtils.invokeConstructor(collType, null);
		} catch (Exception ex) {
			if (List.class.isAssignableFrom(collType)) {
				return new ArrayList<>(capacity);
			}
			if (SortedSet.class.isAssignableFrom(collType) || NavigableSet.class.isAssignableFrom(collType)) {
				return new TreeSet<>();
			}
			if (Set.class.isAssignableFrom(collType)) {
				return new LinkedHashSet<>(capacity);
			}
			throw new TranslationException("Unsupported Collection type: " + collType.getName());
		}
	}

	private Object translate(PhaseDefinition phase, TranslatorContext context, String config, Object value)
			throws Exception {
		Translator translator = getTranslator(phase.getTranslator());
		if (phase.isIterative()) {
			if (value instanceof Collection) {
				Collection<?> coll = (Collection<?>) value;
				Collection<Object> newColl = newCollection(value.getClass(), coll.size());
				for (Object item : coll) {
					newColl.add(translator.translate(context, phase.getConfig(), item));
				}
				return newColl;
			}
			if (value instanceof Object[]) {
				Object[] array = (Object[]) value;
				Object[] newArray = new Object[array.length];
				for (int i = 0; i < array.length; i++) {
					newArray[i] = translator.translate(context, phase.getConfig(), array[i]);
				}
				return newArray;
			}
		}
		return translator.translate(context, config, value);
	}

	@RequiredArgsConstructor
	private class TranslatorContextImpl extends HashMap<String, Object> implements TranslatorContext {
		private final Set<String> configSet;

		@Override
		public Set<String> configSet() {
			return configSet;
		}

		@Override
		public BeanUtilsBean beanUtils() {
			return beanUtils;
		}
	}
}
