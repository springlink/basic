package com.github.springlink.basic.core.translate.basic;

import java.util.Arrays;
import java.util.Iterator;

import com.github.springlink.basic.core.translate.TranslationException;
import com.github.springlink.basic.core.translate.Translator;
import com.github.springlink.basic.core.translate.TranslatorContext;

import org.apache.commons.beanutils.ConvertUtilsBean;

public class JoinTranslator implements Translator {
	@Override
	public Object translate(TranslatorContext context, String config, Object value) throws Exception {
		if (value == null) {
			return null;
		}
		ConvertUtilsBean convertUtils = context.beanUtils().getConvertUtils();
		Iterator<?> iter = getIterator(value);
		StringBuilder joined = new StringBuilder();
		while (iter.hasNext()) {
			Object item = iter.next();
			joined.append(convertUtils.convert(item));
			if (iter.hasNext()) {
				joined.append(config);
			}
		}
		return joined.toString();
	}

	@Override
	public void beforeTranslation(TranslatorContext context) throws Exception {
	}

	@Override
	public void afterTranslation(TranslatorContext context) throws Exception {
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
}