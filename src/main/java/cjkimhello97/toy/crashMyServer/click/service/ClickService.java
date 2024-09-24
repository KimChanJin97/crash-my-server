package cjkimhello97.toy.crashMyServer.click.service;

import cjkimhello97.toy.crashMyServer.click.domain.Click;
import cjkimhello97.toy.crashMyServer.click.exception.ClickException;
import cjkimhello97.toy.crashMyServer.click.exception.ClickExceptionType;
import cjkimhello97.toy.crashMyServer.click.repository.ClickRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickService {

    private final ClickRepository clickRepository;

    @Transactional
    public Click click(Long memberId) {
        Click click = getClickByMemberId(memberId);
        return click.addCount();
    }

    public List<Click> getTopTenClicks() {
        return clickRepository.findTop10ClicksByCountDesc(PageRequest.of(0, 10));
    }

    private Click getClickByMemberId(Long memberId) {
        return clickRepository.findByMemberMemberId(memberId)
                .orElseThrow(() -> new ClickException(ClickExceptionType.CLICK_NOT_FOUND));
    }
}
