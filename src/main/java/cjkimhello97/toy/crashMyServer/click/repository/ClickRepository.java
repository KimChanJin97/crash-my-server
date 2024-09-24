package cjkimhello97.toy.crashMyServer.click.repository;

import cjkimhello97.toy.crashMyServer.click.domain.Click;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface ClickRepository extends Repository<Click, Long> {

    Click save(Click click);

    Optional<Click> findByMemberMemberId(Long memberId);

    @Query("SELECT c FROM Click c ORDER BY c.count DESC")
    List<Click> findTop10ClicksByCountDesc(Pageable pageable);
}
