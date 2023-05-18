package com.epam.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * Configuration class for managing the locale settings.
 */
@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    /**
     * Configures the locale resolver to use session-based locale resolution.
     * Sets the default locale to Locale.US.
     *
     * @return The configured LocaleResolver bean.
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.US);
        return slr;
    }

    /**
     * Configures the locale change interceptor to use the "lang" request parameter for locale change.
     *
     * @return The configured LocaleChangeInterceptor bean.
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    /**
     * Adds the locale change interceptor to the interceptor registry.
     *
     * @param registry The InterceptorRegistry to register the interceptor.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
