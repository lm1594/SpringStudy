package springbook.user.sqlservice;
/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.03.18
 * @history
 * 7장 스프링 핵심기술의 응용
 *   7.2장 인터페이스의 분리와 자기참조 빈
 *    - 7.2.6 디폴트 의존관계
 */
public class DefaultSqlService extends BaseSqlService{
	public DefaultSqlService() {
		setSqlReader(new JaxbXmlSqlReader());
		setSqlRegistry(new HashMapSqlRegistry());
	}
}
