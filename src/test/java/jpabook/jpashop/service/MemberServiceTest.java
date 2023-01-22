package jpabook.jpashop.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;

@RunWith(SpringRunner.class) //단위테스트 말고 Spring으로 테스트
@SpringBootTest //단위테스트 말고 Spring으로 테스트
@Transactional  //테스트케이스에 있으면 DB에 커밋하지 않고 데이터 롤백
//JPA에서 같은 트랜젝션안에서 같은 엔티티(같은 PK)면 같은 영속성 컨텐스트에서 한 개로 관리
public class MemberServiceTest {

	@Autowired
	MemberService memberService;
	@Autowired
	MemberRepository memberRepository;

	@Test
	// @Rollback(value = false) //DB에 커밋(insert 쿼리 볼 수 있음)
	public void 회원가입() throws Exception {
		//given
		Member member = new Member();
		member.setName("park");

		//when
		Long saveId = memberService.join(member);
		//기본적으로 persist한다고 해도 DB에 insert 되는 것은 아님
		//트랜젝션이 commit될 때 반영됨
		//@Transactional에 의해 실제 DB에 insert 되지(insert 쿼리 안 보임) 않음

		//then
		assertEquals(member, memberRepository.findOne(saveId));
	}

	@Test(expected = IllegalStateException.class)
	public void 중복_회원_예외() throws Exception {
		//given
		Member member1 = new Member();
		member1.setName("park");

		Member member2 = new Member();
		member2.setName("park");

		//when
		memberService.join(member1);
		memberService.join(member2); //예외가 발생해야 한다!!

		//then
		fail("예외가 발생해서 fail이 실행되면 안된다.");
	}
}
