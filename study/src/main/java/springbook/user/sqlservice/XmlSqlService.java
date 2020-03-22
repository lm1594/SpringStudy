package springbook.user.sqlservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import springbook.user.dao.UserDao;
import springbook.user.exception.SqlRetrievalFailureException;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.03.18
 * @history
 * 7장 스프링 핵심기술의 응용
 *   7.2장 인터페이스의 분리와 자기참조 빈
 *    - 7.2.2 XML파일을 이용하는 SQL 서비스
 *    - 7.2.3 빈의 초기화 작업
 *    - 7.2.5 자기참조 빈으로 시작하기
 */
public class XmlSqlService implements SqlService, SqlRegistry, SqlReader {
	
	/**
	 * 리스트 7-32 SqlService의 DI 코드
	 */
	private SqlReader sqlReader;
	private SqlRegistry sqlRegistry;
	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	
	/**
	 * 리스트 7-35 SqlService 인터페이스 구현 부분
	 */
	@PostConstruct
	public void loadSql() {
		this.sqlReader.read(this.sqlRegistry);
	}
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		try {
			return this.sqlRegistry.findSql(key);
		}catch(SqlRetrievalFailureException e) {
			throw e;
		}
	}
	
	/**
	 * 리스트 7-33 SqlRegistry의 구현 부분
	 */
	private Map<String, String> sqlMap = new HashMap<String, String>();				// sqlMap은 SqlRegistry 구현의 일부가 된다. 따라서 외부에서 직접 접근할 수 없다.
	@Override
	public String findSql(String key) throws SqlRetrievalFailureException {
		String sql = sqlMap.get(key);
		if(sql == null) {
			throw new SqlRetrievalFailureException(key + "에 대한 SQL을 찾을 수 없습니다.");
		}else {
			return sql;
		}
	}
	@Override
	public void registerSql(String key, String sql) {			// HashMap이라는 저장소를 사용하는 구체적인 구현 방법에서 독립될 수 있도록 인터페이스의 메소드로 접근하게 해준다.
		sqlMap.put(key, sql);
	}
	
	/**
	 * 리스트 7-34 SqlReader의 구현 부분
	 */
	private String sqlmapFile;
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;							// sqlMapFile은 SqlReader 구현의 일부가 된다. 따라서 SqlReader 구현 메소드를 통하지 않고는 접근하면 안된다.
	}
	@Override
	public void read(SqlRegistry sqlRegistry) {					// loadSql()에 있던 코드를 SqlReader메소드로 가져온다. 초기화를 위해 무엇을 할 것인가와 SQL을 어떻게 읽는지를 분리한다.
		// UserDao와 같은 클래스패스의 sqlmap.xml 파일을 변환한다.
		String contextPath = Sqlmap.class.getPackage().getName();
		try {
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile);
			Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(is);
			for(SqlType sql : sqlmap.getSql()) {
				sqlRegistry.registerSql(sql.getKey(), sql.getValue()); 				// SQL 저장 로직 구현에 독립적인 인터페이스 메소드를 통해 읽어들인 SQL과 키를 전달한다.
			}
		}catch (JAXBException e) {
			throw new RuntimeException(e);											// JAXBException은 복구 불가능한 예외다. 불필요한 throws를 피하도록 런타임 예외로 포장해서 던진다.
		}
	}

}
