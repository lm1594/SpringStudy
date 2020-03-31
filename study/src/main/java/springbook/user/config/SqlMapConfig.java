package springbook.user.config;

import org.springframework.core.io.Resource;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 *  7장 스프링 핵심 기술의 응용
 *   7.6 스프링 3.1의 DI
 *    - 7.6.6 빈 설정의 재사용과 @Enable*
 */
public interface SqlMapConfig {
	Resource getSqlMapResource();
}
