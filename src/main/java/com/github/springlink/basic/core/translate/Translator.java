package com.github.springlink.basic.core.translate;

public interface Translator {
	Object translate(TranslatorContext context, String config, Object value) throws Exception;

	void beforeTranslation(TranslatorContext context) throws Exception;

	void afterTranslation(TranslatorContext context) throws Exception;
}
