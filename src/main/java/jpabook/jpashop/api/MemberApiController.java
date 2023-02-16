package jpabook.jpashop.api;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

	private final MemberService memberService;

	// 엔티티가 변경되면 API 스펙이 변경되는 문제가 있다
	// 가입에도 여러 종류가 생길 수 있는데 엔티티 하나로 해결할 수 없다
	// API가 엔티티를 노출하면 위험하다
	@PostMapping("/api/v1/members")
	public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}

	// 엔티티가 파라미터로 전달되면 API 문서를 보지 않으면 정확히 뭐가 사용되는지 알수가 없다
	// DTO를 사용하면 엔티티가 변경되도 API 스펙에 영향을 주지 않는다
	// DTO를 사용하면 API에 꼭 필요한 파라미터를 파악할 수 있고, 상황에 맞게 조건을 다양하게 Validation할 수 있다
	@PostMapping("/api/v2/members")
	public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
		Member member = new Member();
		member.setName(request.getName());

		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}

	@Data
	static class CreateMemberRequest {
		@NotEmpty
		private String name;
	}

	@Data
	@AllArgsConstructor
	static class CreateMemberResponse {
		private Long id;
	}

}
