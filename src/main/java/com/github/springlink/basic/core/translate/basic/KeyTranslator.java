package com.github.springlink.basic.core.translate.basic;

import com.github.springlink.basic.core.translate.Translator;
import com.github.springlink.basic.core.translate.TranslatorContext;

import org.apache.commons.beanutils.PropertyUtilsBean;

public class KeyTranslator implements Translator {
	@Override
	public Object translate(TranslatorContext context, String config, Object value) throws Exception {
		PropertyUtilsBean propertyUtils = context.beanUtils().getPropertyUtils();
		String key = config.trim();
		if (key.startsWith("[") && key.endsWith("]")) {
			String[] keys = key.substring(1, key.length() - 1).split(",");
			Object[] values = new Object[keys.length];
			if (value != null) {
				for (int i = 0; i < keys.length; i++) {
					values[i] = propertyUtils.getProperty(value, keys[i].trim());
				}
			}
			return values;
		}
		if (value == null) {
			return null;
		}
		return propertyUtils.getProperty(value, key.trim());
	}

	@Override
	public void beforeTranslation(TranslatorContext context) throws Exception {
	}

	@Override
	public void afterTranslation(TranslatorContext context) throws Exception {
	}
}