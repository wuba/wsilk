package com.wuba.wsilk.codegen;

import java.io.Writer;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 序列化信息
 * 
 * @author mindashuang
 */
@Data
@AllArgsConstructor
public class SerializeInfo {

	private String javaCode;

	private Writer writer;

}