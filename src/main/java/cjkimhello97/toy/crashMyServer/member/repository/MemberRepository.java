package cjkimhello97.toy.crashMyServer.member.repository;

import cjkimhello97.toy.crashMyServer.member.domain.Member;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface MemberRepository extends Repository<Member, Long> {

    Optional<Member> findByMemberId(Long memberId);

    Optional<Member> findByNickname(String nickname);

    Member save(Member member);
}
