package sourcefx.core.translate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConstructorUtils;

import sourcefx.core.translate.TranslationMetadata.Phase;
import sourcefx.core.translate.TranslationMetadata.Property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class TranslationService {
	private final Map<String, Translator> translators = new HashMap<>();

	private final BeanUtilsBean beanUtils = new BeanUtilsBean();

	public TranslationService() {
		addTranslator("key", this::keyTranslate);
		addTranslator("split", this::splitTranslate);
		addTranslator("join", this::joinTranslate);
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
	public <T> List<T> translate(Class<T> targetType, List<?> sourceValues) {
		TranslationMetadata meta = TranslationMetadata.forType(targetType);
		List<Object> values = prepare(meta, sourceValues);
		meta.getIndexedPhases().forEach(phases -> {
			phases.forEach((propName, phase) -> {
				collect(propName, phase, values).forEach(this::translate);
			});
		});
		convert(meta, values);
		return (List<T>) values;
	}

	private List<Object> prepare(TranslationMetadata meta, List<?> sourceValues) {
		List<Object> values = new ArrayList<>(sourceValues.size());
		for (Object sourceValue : sourceValues) {
			Map<String, Object> mapValue = new HashMap<>();
			for (Property prop : meta.getProperties().values()) {
				mapValue.put(prop.getName(), sourceValue);
			}
			values.add(mapValue);
		}
		return values;
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<ValueHolder>> collect(String propName, Phase phase, List<Object> values) {
		Map<String, List<ValueHolder>> pending = new HashMap<>();
		String option = phase.getTranslatorOption();
		values.stream().map(v -> (Map<String, Object>) v).forEach(value -> {
			List<ValueHolder> list = pending.computeIfAbsent(phase.getTranslatorName(),
					name -> new ArrayList<>());
			Object propValue = value.get(propName);
			if (phase.isIterative()) {
				if (propValue instanceof Collection) {
					Collection<?> source = (Collection<?>) propValue;
					Collection<Object> target = newCollection(propValue.getClass(), source.size());
					value.put(propName, target);
					for (Object item : source) {
						list.add(new ValueHolder(item, option,
								result -> target.add(result)));
					}
				} else if (propValue instanceof Object[]) {
					Object[] source = (Object[]) propValue;
					Object[] target = new Object[source.length];
					value.put(propName, target);
					for (int i = 0; i < source.length; i++) {
						int index = i;
						list.add(new ValueHolder(source[i], option,
								result -> target[index] = result));
					}
				} else if (propValue != null) {
					throw new TranslationException("Value is not a collection");
				}
			} else {
				list.add(new ValueHolder(propValue, option,
						result -> value.put(propName, result)));
			}
		});
		return pending;
	}

	private void translate(String translatorName, List<ValueHolder> vrs) {
		Translator translator = translators.get(translatorName);
		if (translator == null) {
			throw new TranslationException("Unkown translator [" + translatorName + "]");
		}
		try {
			translator.translate(Collections.unmodifiableList(vrs));
		} catch (Exception e) {
			throw new TranslationException("Failed to translate values", e);
		}
		vrs.forEach(vr -> {
			if (!vr.isTranslated()) {
				throw new TranslationException("Value not translated");
			}
			vr.getResultHandler().accept(vr.getResult());
		});
	}

	@SuppressWarnings("unchecked")
	private void convert(TranslationMetadata meta, List<Object> values) {
		for (int i = 0; i < values.size(); i++) {
			Map<String, Object> mapValue = (Map<String, Object>) values.get(i);
			Object target;
			try {
				target = ConstructorUtils.invokeConstructor(meta.getTargetType(), null);
			} catch (Exception e) {
				throw new TranslationException("Failed to create target instance", e);
			}
			meta.getProperties().forEach((name, prop) -> {
				Object val = beanUtils.getConvertUtils().convert(mapValue.get(name), prop.getType());
				try {
					beanUtils.getPropertyUtils().setProperty(target, prop.getName(), val);
				} catch (Exception e) {
					throw new TranslationException("Failed to set result value to property", e);
				}
			});
			values.set(i, target);
		}
	}

	@SuppressWarnings("unchecked")
	private <E> Collection<E> newCollection(Class<?> collType, int capacity) {
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

	private Iterator<?> getIterator(Object value) {
		if (value instanceof Iterator) {
			return (Iterator<?>) value;
		} else if (value instanceof Iterable) {
			return ((Iterable<?>) value).iterator();
		} else if (value instanceof Object[]) {
			return Arrays.asList((Object[]) value).iterator();
		} else {
			throw new TranslationException("Cannot perform join operation on non-iterable value");
		}
	}

	private void keyTranslate(Collection<ValueAndResult> vrs) throws Exception {
		for (ValueAndResult vr : vrs) {
			String key = vr.getOption().trim();
			if (key.startsWith("[") && key.endsWith("]")) {
				String[] keys = key.substring(1, key.length() - 1).split(",");
				Object[] values = new Object[keys.length];
				if (vr.getValue() != null) {
					for (int i = 0; i < keys.length; i++) {
						values[i] = beanUtils.getPropertyUtils().getProperty(vr.getValue(), keys[i].trim());
					}
				}
				vr.setResult(values);
			} else if (vr.getValue() != null) {
				vr.setResult(beanUtils.getPropertyUtils().getProperty(vr.getValue(), key));
			} else {
				vr.setResult(null);
			}
		}
	}

	private void joinTranslate(Collection<ValueAndResult> vrs) throws Exception {
		for (ValueAndResult vr : vrs) {
			if (vr.getValue() == null) {
				vr.setResult(null);
				continue;
			}
			Iterator<?> iter = getIterator(vr.getValue());
			StringBuilder joined = new StringBuilder();
			while (iter.hasNext()) {
				Object item = iter.next();
				joined.append(beanUtils.getConvertUtils().convert(item));
				if (iter.hasNext()) {
					joined.append(vr.getOption());
				}
			}
			vr.setResult(joined.toString());
		}
	}

	private void splitTranslate(Collection<ValueAndResult> vrs) throws Exception {
		for (ValueAndResult vr : vrs) {
			String strValue = beanUtils.getConvertUtils().convert(vr.getValue());
			vr.setResult(strValue != null ? strValue.split(vr.getOption()) : null);
		}
	}

	@Getter
	@RequiredArgsConstructor
	private static class ValueHolder implements ValueAndResult {
		private final Object value;

		private final String option;

		private final Consumer<Object> resultHandler;

		private Object result;

		private boolean translated;

		@Override
		public void setResult(Object result) {
			this.result = result;
			this.translated = true;
		}
	}
}
