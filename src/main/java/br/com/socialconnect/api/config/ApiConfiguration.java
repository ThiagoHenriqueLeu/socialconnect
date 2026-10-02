package br.com.socialconnect.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import java.util.List;
import java.util.Locale;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class ApiConfiguration {
    @Bean
    public OpenAPI socialConnectOpenAPI() {
        return new OpenAPI().info(new Info().title("SocialConnect API")
            .description("Gestão de beneficiários e estoque de doações. Avaliação A1: módulo de Produtos.")
            .version("0.0.1-SNAPSHOT"));
    }

    @Bean
    public LocaleResolver localeResolver() {
        var resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.forLanguageTag("pt-BR"));
        resolver.setSupportedLocales(List.of(Locale.forLanguageTag("pt-BR"), Locale.forLanguageTag("pt"), Locale.ENGLISH));
        return resolver;
    }
}
