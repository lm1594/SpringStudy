package springbook.user.sqlservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import springbook.user.dao.UserDao;
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
 */
public class XmlSqlService implements SqlService {
	private String sqlmapFile;
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}
	
	// 설정파일에 <map>으로 정의된 SQL 정보를 가져오도록 프로퍼티로 등록해둔다.
	private Map<String, String> sqlMap = new HashMap<String, String>();				// 읽어온 SQL을 저장해둘 맵
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		String sql = sqlMap.get(key);
		if(sql == null) {
			throw new SqlRetrievalFailureException(key + "에 대한 SQL을 찾을 수 없습니다.");
		}else {
			return sql;
		}
	}
	
	/**
	 * 리스트 7-22 생성자 대신 사용할 초기화 메소드
	 */
	@PostConstruct	// loadSql() 메소드를 빈의 초기화 메소드로 지정한다.
	public void loadSql() {
		// UserDao와 같은 클래스패스의 sqlmap.xml 파일을 변환한다.
		String contextPath = Sqlmap.class.getPackage().getName();
		try {
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile);
			Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(is);
			for(SqlType sql : sqlmap.getSql()) {
				sqlMap.put(sql.getKey(), sql.getValue());							// 읽어온 SQL을 맵으로 저장해둔다.
			}
		}catch (JAXBException e) {
			throw new RuntimeException(e);											// JAXBException은 복구 불가능한 예외다. 불필요한 throws를 피하도록 런타임 예외로 포장해서 던진다.
		}
	}

}
