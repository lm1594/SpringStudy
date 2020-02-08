package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * 토비의스프링
 * @author 이경민
 * @since 2020.02.08
 * @history
 *  3.5.3 템플릿 / 콜백의 응용
 */
public interface BufferedReaderCallback {
	Integer doSomethingWithReader(BufferedReader br) throws IOException;
}
