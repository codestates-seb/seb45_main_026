package com.server.global.restdocs;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnumDocs {

	// 문서화하고 싶은 모든 enum값을 명시
	Map<String,String> oauthprovider;

}
