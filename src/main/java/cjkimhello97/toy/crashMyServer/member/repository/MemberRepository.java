package cjkimhello97.toy.crashMyServer.member.repository;

import cjkimhello97.toy.crashMyServer.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface MemberRepository extends Repository<Member, Long> {

    @Query("SELECT m FROM Member m")
    List<Member> findAll();

    Optional<Member> findByMemberId(Long memberId);

    Optional<Member> findByNickname(String nickname);

    boolean existsByNickname(String nickname);

    Member save(Member member);
}
