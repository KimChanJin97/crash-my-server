package cjkimhello97.toy.crashMyServer.click.controller;

import static cjkimhello97.toy.crashMyServer.click.utils.CountFormatter.format;

import cjkimhello97.toy.crashMyServer.auth.support.AuthMember;
import cjkimhello97.toy.crashMyServer.click.domain.Click;
import cjkimhello97.toy.crashMyServer.click.service.ClickService;
import cjkimhello97.toy.crashMyServer.common.exception.dto.ExceptionResponse;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRankRequest;
import cjkimhello97.toy.crashMyServer.kafka.dto.KafkaClickRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/click")
@RequiredArgsConstructor
public class ClickController {

    private final ClickService clickService;
    private final KafkaTemplate<String, KafkaClickRequest> kafkaClickRequestKafkaTemplate;
    private final KafkaTemplate<String, KafkaClickRankRequest> kafkaClickRankRequestKafkaTemplate;

    @PostMapping
    @Operation(
            summary = "[ HTTP 요청 + STOMP 응답 ] 클릭 API",
            description = """
                    STOMP 응답을 받기 위한 조건
                     1. 웹소켓 연결 : ws://도메인:8080/ws URL을 연결한 상태이어야 합니다.
                     2. 웹소켓 구독 1 : /sub/click/{본인 닉네임} URL을 구독한 상태이어야 합니다.
                     3. 웹소켓 구독 2 : /sub/click-rank URL을 구독한 상태이어야 합니다.
                    주의사항
                     1-1. 웹소켓 구독 1 URL 을 구독한 본인에게만 HTTP 응답이 아니라 STOMP 응답이 옵니다. (* 주의 본인 클릭 횟수는 본인만 보는 로직)
                     1-2. 메시지 응답 형식 : {"nickname":"aaa","count":"1"}
                     2-1. 웹소켓 구독 2 URL 을 구독한 모든 클라이언트에게 HTTP 응답이 아니라 STOMP 응답이 옵니다. (* 주의 클릭 랭크는 모두가 보는 로직)
                     2-2. 메시지 응답 형식 : {"clickRank":{"aaa":"1", "bbb":"2"}}  (* 주의 리스트 내림차순 정렬되지 않을 가능성 존재하므로 정렬 필수)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공"),
            @ApiResponse(
                    responseCode = "400, 401, 404",
                    description = "클릭 예외(400), JWT 검증/파싱 예외(401, 404)",
                    content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    public void click(@AuthMember Long memberId) {
        Click click = clickService.click(memberId);
        KafkaClickRequest kafkaClickRequest = KafkaClickRequest.builder()
                .count(format(click.getCount()))
                .nickname(click.getMember().getNickname())
                .build();
        kafkaClickRequestKafkaTemplate.send("click", null, kafkaClickRequest);

        List<Click> topTenClicks = clickService.getTopTenClicks();
        Map<String, String> clickRank = new HashMap<>();
        topTenClicks.stream().forEach(c -> clickRank.put(c.getMember().getNickname(), format(c.getCount())));
        KafkaClickRankRequest kafkaClickRankRequest = KafkaClickRankRequest.builder()
                .clickRank(clickRank)
                .build();
        kafkaClickRankRequestKafkaTemplate.send("click-rank", null, kafkaClickRankRequest);
    }
}
