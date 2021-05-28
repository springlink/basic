package com.github.springlink.basic.core.translate.basic;

import com.github.springlink.basic.core.translate.Translator;
import com.github.springlink.basic.core.translate.TranslatorContext;

public class SplitTranslator implements Translator {
	@Override
	public Object translate(TranslatorContext context, String config, Object value) throws Exception {
		String strValue = context.beanUtils().getConvertUtils().convert(value);
		if (strValue == null) {
			return null;
		}
		return strValue.split(config);
	}

	@Override
	public void beforeTranslation(TranslatorContext context) throws Exception {
	}

	@Override
	public void afterTranslation(TranslatorContext context) throws Exception {
	}
}