package springbook.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {
		"classpath:database.properties"
})
//리스트 7-126 프로퍼티 소스를 이용한 치환자 설정용 빈
public class ResourceConfig {
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertSourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		propertySourcesPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(false);
		return propertySourcesPlaceholderConfigurer;
	}
}
