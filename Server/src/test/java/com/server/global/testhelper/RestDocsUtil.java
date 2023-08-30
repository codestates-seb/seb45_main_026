package com.server.global.testhelper;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.restdocs.payload.FieldDescriptor;

public class RestDocsUtil {
	private static final FieldDescriptor[] pageInfoFields = new FieldDescriptor[]{
		fieldWithPath("pageInfo").description("페이지네이션 정보"),
		fieldWithPath("pageInfo.page").description("현재 페이지"),
		fieldWithPath("pageInfo.size").description("페이지 사이즈"),
		fieldWithPath("pageInfo.totalPage").description("전체 페이지 수"),
		fieldWithPath("pageInfo.totalSize").description("전체 데이터 개수"),
		fieldWithPath("pageInfo.first").description("첫 페이지 여부"),
		fieldWithPath("pageInfo.last").description("마지막 페이지 여부"),
		fieldWithPath("pageInfo.hasNext").description("다음 페이지 존재 여부"),
		fieldWithPath("pageInfo.hasPrevious").description("이전 페이지 존재 여부")
	};

	private static final FieldDescriptor[] responseStatusFields = new FieldDescriptor[]{
		fieldWithPath("code").description("응답 코드"),
		fieldWithPath("status").description("응답 상태"),
		fieldWithPath("message").description("응답 메시지")
	};

	public static FieldDescriptor[] getPageResponseFields(FieldDescriptor[] responseFields) {
		List<FieldDescriptor> allFields = new ArrayList<>();
		allFields.addAll(Arrays.asList(responseFields));
		allFields.addAll(Arrays.asList(pageInfoFields));
		allFields.addAll(Arrays.asList(responseStatusFields));
		return allFields.toArray(new FieldDescriptor[0]);
	}
}
