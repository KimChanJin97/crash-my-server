package cjkimhello97.toy.crashMyServer.member.service;

import static cjkimhello97.toy.crashMyServer.auth.exception.AuthExceptionType.MEMBER_NOT_FOUND;

import cjkimhello97.toy.crashMyServer.auth.exception.AuthException;
import cjkimhello97.toy.crashMyServer.member.domain.Member;
import cjkimhello97.toy.crashMyServer.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new AuthException(MEMBER_NOT_FOUND));
    }

    public String getMemberNicknameByMemberId(Long memberId) {
        return getMemberByMemberId(memberId).getNickname();
    }

    public Member getMemberByMemberId(Long memberId) {
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new AuthException(MEMBER_NOT_FOUND));
    }
}
