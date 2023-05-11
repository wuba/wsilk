/*
 * Copyright (C) 2005-present, 58.com.  All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wuba.wsilk.codegen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

import com.wuba.wsilk.codegen.parser.JavaUnit;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;

/**
 * java合并
 * 
 * @author mindashuang
 */
@CommonsLog
@Getter
@Setter
public class JavaMergeCode implements MergeCode {

	private String code;

	private JavaUnit codeUnit;

	public static Code getCustomCode(String oldCode, String javaName, String childName) {
		Code code = null;
		int start = 0;
		int startEnd = 0;
		int end = 0;
		String p = "(public|protected)(\\s+)class(\\s+)" + javaName + "(\\s+)extends(\\s+)" + childName + "(\\s?)\\{";
		Pattern pt = Pattern.compile(p);
		Matcher mt = pt.matcher(oldCode);
		if (mt.find()) {
			start = mt.start();
			startEnd = mt.end();
		}
		if (start > 0) {
			code = new Code();
			String top = oldCode.substring(0, start);
			code.top = top;
			pt = Pattern.compile("\\}(\\s+)(abstract(\\s+))?class(\\s+)" + childName);
			mt = pt.matcher(oldCode);
			if (mt.find()) {
				end = mt.start();
			}
			if (end > 0 && end > startEnd) {
				code.custom = StringUtils.trimToEmpty(oldCode.substring(startEnd, end));
			}
		}
		return code;
	}

	public static JavaMergeCode create(SerializeInfo serializeInfo, String javaName, String childName) {
		JavaMergeCode customerCode = null;
		if (StringUtils.isNotEmpty(serializeInfo.getJavaCode()) && childName != null) {
			Code code = getCustomCode(serializeInfo.getJavaCode(), javaName, childName);
			if (code != null && StringUtils.isNoneEmpty(code.top)) {
				if (log.isDebugEnabled()) {
					log.debug(javaName);
				}
				JavaUnit javaUnit = JParser.parser(code.top);
				if (javaUnit != null) {
					customerCode = new JavaMergeCode();
					customerCode.setCodeUnit(javaUnit);
					customerCode.setCode(code.custom);
				}
			}
		}
		return customerCode;
	}

	@NoArgsConstructor
	static class Code {
		String top;
		String custom;
	}

}
