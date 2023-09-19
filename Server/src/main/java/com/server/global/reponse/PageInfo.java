package com.server.global.reponse;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageInfo {

	private int page;
	private int size;
	private int totalPage;
	private int totalSize;
	private boolean first;
	private boolean last;
	private boolean hasNext;
	private boolean hasPrevious;

	public static PageInfo of(Page<?> page){
		return new PageInfo(
			page.getNumber() + 1,
			page.getSize(),
			page.getTotalPages(),
			(int) page.getTotalElements(),
			page.isFirst(),
			page.isLast(),
			page.hasNext(),
			page.hasPrevious()
		);
	}
}
