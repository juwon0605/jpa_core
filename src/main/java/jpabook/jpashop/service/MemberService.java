package jpabook.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true) // 몇가지 단계를 생략한 단순 조회용 성능 최적회(데이터 변경 안 됨 주의)
@RequiredArgsConstructor
// public 메서드에 자동 적용
public class MemberService {

	private final MemberRepository memberRepository;

	/**
	 * 회원 가입
	 */
	@Transactional // default가 readOnly = false
	public Long join(Member member) {
		validateDuplicateMember(member); // 중복 회원 검증
		memberRepository.save(member);
		return member.getId();
	}

	private void validateDuplicateMember(Member member) {
		// 실무에서는 멀티스레드나 여러 WAS를 고려하여 DB에 유니크 조건을 추가함
		List<Member> findMembers = memberRepository.findByName(member.getName());

		if (!findMembers.isEmpty()) {
			throw new IllegalStateException("이미 존재하는 회원입니다.");
		}
	}

	/**
	 * 회원 조회
	 */
	public List<Member> findMembers() {
		return memberRepository.findAll();
	}

	public Member findOne(Long memberId) {
		return memberRepository.findOne(memberId);
	}
}
