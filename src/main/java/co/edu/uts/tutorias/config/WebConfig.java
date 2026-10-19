package co.edu.uts.tutorias.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración Web MVC para registrar el interceptor de acceso.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AccesoInterceptor accesoInterceptor;

    public WebConfig(AccesoInterceptor accesoInterceptor) {
        this.accesoInterceptor = accesoInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accesoInterceptor)
                .addPathPatterns("/**");
    }
}
