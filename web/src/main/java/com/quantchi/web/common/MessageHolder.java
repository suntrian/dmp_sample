package com.quantchi.web.common;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageHolder implements MessageSourceAware {

    private static MessageSource messageSource;

    @Override
    public void setMessageSource(MessageSource messageSource) {
        MessageHolder.messageSource = messageSource;
    }

    public static String getMessage(String key){
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String key, Object... args){
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String key, Locale locale, Object... args){
        return messageSource.getMessage(key, args, locale);
    }

    public static String getMessage(String key, String defaultMessage){
        return messageSource.getMessage(key, null, defaultMessage, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String key, String defaultMessage, Object... args){
        return messageSource.getMessage(key, args, defaultMessage, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String key, String defaultMessage, Locale locale, Object... args){
        return messageSource.getMessage(key, args, defaultMessage, locale);
    }

}
