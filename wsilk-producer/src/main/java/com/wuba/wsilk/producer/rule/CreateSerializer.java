package com.wuba.wsilk.producer.rule;

import static com.wuba.wsilk.codegen.Constants.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.wuba.wsilk.codegen.CompositeJavaWriter;
import com.wuba.wsilk.codegen.model.Types;
import com.wuba.wsilk.core.NoGenericException;
import com.wuba.wsilk.core.SourceEntityMeta;
import com.wuba.wsilk.core.WsilkConfiguration;
import com.wuba.wsilk.core.serializer.java.AbstractJavaSerializerDecorator;

import lombok.Setter;

/**
 * 创建规则
 * 
 * @author mindashuang
 */
public class CreateSerializer
		extends AbstractJavaSerializerDecorator<SourceEntityMeta, CompositeJavaWriter, CallRuleSerializer> {

	@Setter
	private String name;

	@Setter
	private List<String> lines;

	public CreateSerializer(WsilkConfiguration conf, Class<? extends Annotation> annClass, CallRuleSerializer parent) {
		super(conf, annClass, parent);
	}

	@Override
	public SourceEntityMeta init(SourceEntityMeta em) throws NoGenericException {
		em.setJavaName(StringUtils.capitalize(name) + this.getSupport().suffix());
		return super.init(em);
	}

	@Override
	public void methods(CompositeJavaWriter writer, SourceEntityMeta t) throws IOException {
		writer.beginMethod(Types.VOID, "rule");
		for (String line : lines) {
			doLine(writer, line);
		}
		writer.end();
	}

	private final static String TOKEN = "==";

	public void doLine(CompositeJavaWriter cwriter, String line) throws IOException {
		if (StringUtils.isNoneBlank(line)) {
			if (line.startsWith(TOKEN)) {
				String method = line.substring(2, line.length());
				// 方法开始
				boolean start = cwriter.isStart(method);
				if (start) {
					cwriter.line(method, BRAKETS, SEMICOLON);
					cwriter.startAppend(method);
					cwriter.out();
					cwriter.beginMethod(Types.VOID, method);
				} else {// 方法结束
					cwriter.end();
					cwriter.in();
					cwriter.endAppend(method);
				}
			} else {
				cwriter.line("System.out.println(", DOUBLE_QUOTATION, line, DOUBLE_QUOTATION, ")", SEMICOLON);
			}
		}
	}

}
