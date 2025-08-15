package com.ansbeno.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;

@EnableWebMvc
@Configuration
@ComponentScan("com.ansbeno.controllers")
class WebConfig implements WebMvcConfigurer {

      @Bean
      public SpringResourceTemplateResolver templateResolver() {
            SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
            resolver.setPrefix("classpath:/templates/");
            resolver.setSuffix(".html");
            resolver.setTemplateMode("HTML");
            resolver.setCharacterEncoding("UTF-8");
            resolver.setCacheable(false);
            return resolver;
      }

      @Bean
      public SpringTemplateEngine templateEngine() {
            SpringTemplateEngine engine = new SpringTemplateEngine();
            engine.setTemplateResolver(templateResolver());
            engine.setEnableSpringELCompiler(true);
            // Add Ultraq Layout Dialect to the template engine
            engine.addDialect(new LayoutDialect());
            return engine;
      }

      @Bean
      public ViewResolver viewResolver() {
            ThymeleafViewResolver resolver = new ThymeleafViewResolver();
            resolver.setTemplateEngine(templateEngine());
            resolver.setCharacterEncoding("UTF-8");
            return resolver;
      }

      @Override
      public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/css/**")
                        .addResourceLocations("classpath:/static/css/")
                        .setCacheControl(CacheControl.noCache());

            registry.addResourceHandler("/js/**")
                        .addResourceLocations("classpath:/static/js/")
                        .setCacheControl(CacheControl.noCache());

            registry.addResourceHandler("/images/**")
                        .addResourceLocations("classpath:/static/images/")
                        .setCacheControl(CacheControl.noCache());

            registry.addResourceHandler("/webjars/**")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/");

      }

}
