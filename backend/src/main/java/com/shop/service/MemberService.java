package com.shop.service;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
//비즈니스 로직을 담당하는 서비스 계층 클래스에 @Transactional 어노테이션을 선언한다.
//로직을 처리하다가 에러가 발생하면 변경된 데이터 로직을 수행하기 이전 상태로 콜백 시켜준다.
@RequiredArgsConstructor
//
public class MemberService {
    private final MemberRepository memberRepository;
    // 빈을 주입하는 방법으로 @Autowired 어노테이션을 이용하거나, 필드 주입(Setter 주입)
    //생성자 주입을 이용하는 방법이 있다. @RequiredArgsConstructor 어노테이션은 final이나
    //@NonNull이 붙은 필드에 생성자를 생성해준다. 빈에 생성자가 한개이고 생성자의 파라미터 타입
    //이 빈으로 등록 가능 하다면 @Autowired 어노테이션 없이 의존성 주입이 가능하다.

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        //이미 가입된 회원인지 확인하는 로직 (회원의 경우 IllegalStateException 예외 발생)
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }
}
