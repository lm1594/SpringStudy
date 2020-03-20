package springbook.user.sqlservice;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import springbook.user.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.03.18
 * @history
 * 7장 스프링 핵심기술의 응용
 *   7.3장 서비스 추상화 적용
 *    - 7.3.2 OXM 서비스 추상화 적용
 */
public class OxmSqlService implements SqlService{
	private final BaseSqlService baseSqlService = new BaseSqlService();				// SqlService의 실제 구현 부분을 위임할 대상인 BaseSqlService를 인스턴스 변수로 정의해둔다.
	private final OxmSqlReader oxmSqlReader = new OxmSqlReader();
	
	// oxmSqlReader와 달리 단지 디폴트 오브젝트로 만들어진 프로퍼티다. 따라서 필요에 따라 DI를 통해 교체 가능하다.
	private SqlRegistry sqlRegistry = new HashMapSqlRegistry();
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	
	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.oxmSqlReader.setUnmarshaller(unmarshaller);
	}
	public void setSqlmap(Resource sqlmap) {
		this.oxmSqlReader.setSqlmap(sqlmap);
	}
	
	// SqlService 인터페이스에 대한 구현 코드는 BaseSqlService와 같다.
	@PostConstruct
	public void loadSql() {
		// OxmSqlService의 프로퍼티를 통해서 초기화된 SqlReader와 SqlRegistry를 실제 작업을 위임할 대상인 baseSqlService에 주입한다.
		this.baseSqlService.setSqlReader(this.oxmSqlReader);
		this.baseSqlService.setSqlRegistry(this.sqlRegistry);
		
		// SQL을 등록하는 초기화 작업을 baseSqlService에 위임한다.
		this.baseSqlService.loadSql();
	}
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		return this.baseSqlService.getSql(key);
	}
	
	private class OxmSqlReader implements SqlReader {
		private Unmarshaller unmarshaller;
		private static final String DEFAULT_SQLMAP_FILE = "sqlmap.xml";			// 굳이 상수로 만들지 않고 바로 sqlmapFile의 값으로 넣어도 상관없지만 이렇게 해주면 의도가 코드에 분명히 드러나고 코드도 폼이 난다.
		private Resource sqlmap = new ClassPathResource(DEFAULT_SQLMAP_FILE, UserDao.class);
		
		public void setSqlmap(Resource sqlmap) {
			this.sqlmap = sqlmap;												// sqlMapFile은 SqlReader의 특정 구현 방법에 종속되는 프로퍼티가 된다.
		}
		public void setUnmarshaller(Unmarshaller unmarshaller) {
			this.unmarshaller = unmarshaller;
		}
		
		@Override
		public void read(SqlRegistry sqlRegistry) {
			try {
				Source source = new StreamSource(sqlmap.getInputStream());
			
				Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);	// 어떤 OXM 기술이든 언마샬은 이 한 줄이면 끝이다.
				for(SqlType sql : sqlmap.getSql()) {
					sqlRegistry.registerSql(sql.getKey(), sql.getValue());
				}
			
			}catch(IOException e) {
				// 언마샬 작업 중 IO 에러가 났다면 설정을 통해 제공받은 XML 파일 이름이나 정보가 잘못됐을 가능성이 제일 높다. 이런 경우에 가장 적합한 런타임 예외 중 하나인 IllegalArgumentException으로 포장해서 던진다.
				throw new IllegalArgumentException(this.sqlmap + "을 가져올 수 없습니다.", e);
			}
		}
		
	}
}
