import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;

import com.wuba.wsilk.codegen.JParser;
import com.wuba.wsilk.codegen.parser.JavaUnit;

/**
 * 测试
 * 
 * @author mindashuang
 */
public class ImportAndAnnoataion {

	public static void main(String[] args) {

		try {

			String content = FileUtils.readFileToString(
					new File(ImportAndAnnoataion.class.getResource("1.txt").getFile()), Charset.defaultCharset());

			JavaUnit javaUnit = JParser.parser(content);


		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
