package com.example.auth.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurer;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.server.endpoint.adapter.method.MethodReturnValueHandler;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.Collections;
import java.util.List;

@EnableWs
@Configuration
public class WebServiceConfig implements WsConfigurer {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "auth")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema authSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("AuthPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://example.com/auth");
        wsdl11Definition.setSchema(authSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema authSchema() {
        return new SimpleXsdSchema(new ClassPathResource("auth.xsd"));
    }

    /**
     * БҮХ ГАЗРААС ХАНДАХЫГ ЗӨВШӨӨРӨХ CORS FILTER
     * Энэ хэсэг байхгүй бол Browser-оос SOAP хүсэлт илгээхэд алдаа заадаг.
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false); // Frontend-д хялбар болгох үүднээс false болгов
        config.setAllowedOrigins(Collections.singletonList("*")); // Бүх хаягийг зөвшөөрөх
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setAllowedMethods(Collections.singletonList("*"));
        source.registerCorsConfiguration("/**", config);
        
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0); // Хамгийн эхэнд ажиллахаар тохируулна
        return bean;
    }

	@Override
	public void addInterceptors(List<EndpointInterceptor> interceptors) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addArgumentResolvers(List<MethodArgumentResolver> argumentResolvers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addReturnValueHandlers(List<MethodReturnValueHandler> returnValueHandlers) {
		// TODO Auto-generated method stub
		
	}
}