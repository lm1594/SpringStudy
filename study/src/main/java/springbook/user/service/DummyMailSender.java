package springbook.user.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * 토비의스프링
 * @author 이경민
 * @since 2020.03.03
 * @history
 *  5장 서비스 추상화
 *   5.4장 메일서비스 추상화
 *    - 5.4.3 테스트를 위한 서비스 추상화 : 테스트용 메일발송 오브젝트
 */
public class DummyMailSender implements MailSender{

	@Override
	public void send(SimpleMailMessage simpleMessage) throws MailException {
		// TODO Auto-generated method stub
		System.out.println("단건 메일 발송");
	}

	@Override
	public void send(SimpleMailMessage... simpleMessages) throws MailException {
		// TODO Auto-generated method stub
		System.out.println("다건 메일 발송");
	}
	
}
