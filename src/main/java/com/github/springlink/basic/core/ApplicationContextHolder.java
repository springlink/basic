package com.github.springlink.basic.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextHolder implements ApplicationContextAware, DisposableBean {
	private static ApplicationContext applicationContext;

	@Override
	public synchronized void setApplicationContext(ApplicationContext ctx) throws BeansException {
		applicationContext = ctx;
	}

	@Override
	public void destroy() throws Exception {
		applicationContext = null;
	}

	public static ApplicationContext getApplicationContext() {
		if (applicationContext == null) {
			throw new IllegalStateException("ApplicationContext unavailable");
		}
		return applicationContext;
	}
}
