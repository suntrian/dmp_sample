package com.quantchi.web.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextHolder implements ApplicationContextAware {

  private static ApplicationContext context;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    SpringContextHolder.setContext(applicationContext);
  }


  public static ApplicationContext getContext() {
    return context;
  }

  public static void setContext(ApplicationContext context) {
    SpringContextHolder.context = context;
  }

  @SuppressWarnings("unchecked")
  public static <T> T getBean(String name) {
    return (T) context.getBean(name);
  }

  public static <T> T getBean(Class<T> beanClass) {
    return context.getBean(beanClass);
  }

  public static <T> T getBean(String name, Class<T> beanClass) {
    return context.getBean(name, beanClass);
  }

}
