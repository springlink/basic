package com.github.springlink.basic.core.translate;

import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtilsBean;

public interface TranslatorContext extends Map<String, Object> {
	Set<String> configSet();

	BeanUtilsBean beanUtils();
}
