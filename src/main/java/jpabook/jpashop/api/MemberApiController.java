package jpabook.jpashop.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
	@GetMapping("api/v1/members")
	public List<Member> memberesV1() {
		return memberService.findMembers();
	}

	// 엔티티가 파라미터로 전달되면 API 문서를 보지 않으면 정확히 뭐가 사용되는지 알수가 없다
	// DTO를 사용하면 엔티티가 변경되도 API 스펙에 영향을 주지 않는다
	// DTO를 사용하면 API에 꼭 필요한 파라미터를 파악할 수 있고, 상황에 맞게 조건을 다양하게 Validation할 수 있다
	@GetMapping("api/v2/members")
	public Result membersV2() {
		List<Member> findMembers = memberService.findMembers();
		List<MemberDto> collect = findMembers.stream()
			.map(m -> new MemberDto(m.getName()))
			.collect(Collectors.toList());
		return new Result(collect);
	}

	@Data
	@AllArgsConstructor
	static class Result<T> {
		private T data;
	}

	//확장가능한 유연한 응답을 보내는 것이 좋다
	@Data
	@AllArgsConstructor
	static class MemberDto {
		private String name;
	}

	// 엔티티를 반환하면 안된다
	@PostMapping("/api/v1/members")
	public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}

	@PostMapping("/api/v2/members")
	public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
		Member member = new Member();
		member.setName(request.getName());

		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}

	@Data
	@AllArgsConstructor
	static class CreateMemberResponse {
		private Long id;
	}

	@Data
	static class CreateMemberRequest {
		@NotEmpty
		private String name;
	}

	@PutMapping("/api/v2/members/{id}")
	public UpdateMemberResponse updateMemberV2(
		@PathVariable("id") Long id,
		@RequestBody @Valid UpdateMemberRequest request
	) {
		memberService.update(id, request.getName());
		Member findMember = memberService.findOne(id);
		return new UpdateMemberResponse(findMember.getId(), findMember.getName());
	}

	@Data
	@AllArgsConstructor
	static class UpdateMemberResponse {
		private Long id;
		private String name;
	}

	@Data
	static class UpdateMemberRequest {
		@NotEmpty
		private String name;
	}
}

