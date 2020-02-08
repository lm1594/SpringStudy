package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 토비의스프링
 * @author 이경민
 * @since 2020.02.08
 * @history
 *  3.5.3 템플릿 / 콜백의 응용
 */
public class Calculator {
	
	/**
	 * 합계 구하기 / 템플릿 콜백
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	public Integer calcSum(String filepath) throws IOException {
		BufferedReaderCallback sumCallback = new BufferedReaderCallback() {
			public Integer doSomethingWithReader(BufferedReader br) 
					throws IOException{
				Integer sum = 0;
				String line = null;
				while((line = br.readLine()) != null) {
					sum += Integer.parseInt(line);
				}
				return sum;
			}
		};
		
		return fileReadTemplate(filepath, sumCallback);
	}
	
	/**
	 * 곱 구하기 / 템플릿 콜백 
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	public Integer calcMultiply(String filepath) throws IOException{
		BufferedReaderCallback multiplyCallback = new BufferedReaderCallback() {
			public Integer doSomethingWithReader(BufferedReader br)
				throws IOException {
				Integer multiply = 1;
				String line = null;
				while((line = br.readLine()) != null) {
					multiply *= Integer.valueOf(line); 
				}
				return multiply;
			}
		};
		return fileReadTemplate(filepath, multiplyCallback);
	}
	
	/**
	 * 템플릿 메소드
	 * @param filepath
	 * @param callback
	 * @return
	 * @throws IOException
	 */
	public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) 
			throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filepath));
			Integer ret = callback.doSomethingWithReader(br);
			return ret;
		}catch(IOException e) {
			System.out.println(e.getMessage());
			throw e;
		}finally {
			if(br != null) {
				try{br.close();}
				catch(IOException e) {System.out.println(e.getMessage());} 
			}
		}
	}
}
