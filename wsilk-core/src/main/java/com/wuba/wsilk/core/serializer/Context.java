package com.wuba.wsilk.core.serializer;

import com.wuba.wsilk.core.SourceEntityMeta;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Context {

	private SourceEntityMeta entity;
	private int index;
	private int capacity;

	public boolean over() {
		return index == capacity;
	}

}
