package cjkimhello97.toy.crashMyServer.click.controller;

import cjkimhello97.toy.crashMyServer.auth.support.AuthMember;
import cjkimhello97.toy.crashMyServer.click.domain.Click;
import cjkimhello97.toy.crashMyServer.click.dto.ClickResponse;
import cjkimhello97.toy.crashMyServer.click.service.ClickService;
import cjkimhello97.toy.crashMyServer.common.exception.dto.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "클릭 조회(본인 클릭 + 클릭 랭크) / 클릭(본인 클릭 횟수+ 클릭 랭크)")
@RestController
@RequestMapping("/api/v1/click")
@RequiredArgsConstructor
public class ClickController {

    private final ClickService clickService;

    @GetMapping
    @Operation(
            summary = "[ HTTP 요청/응답 ] 클릭 조회 API"
    )
    @ApiResponse(
            responseCode = "200",
            description = """
                    ### 설명 : 메인 페이지 입장시 반환될 본인 클릭 횟수 DTO 와 와 클릭 랭크 DTO 입니다.
                    - 응답 형식 : {"count":"1","clickRank":{"bbb":"2", "ccc":"4", "ddd":"3"}} (* 주의. 리스트는 정렬되지 않은 상태로 반환되므로 내림차순 정렬하여 클릭 랭크에 렌더링)
                    """
    )
    public ResponseEntity<ClickResponse> getClick(@AuthMember Long memberId) {
        Click click = clickService.getClickByMemberId(memberId);

        Map<String, String> clickRank = new HashMap<>();
        List<Click> topTenClicks = clickService.getTopTenClicks();
        topTenClicks.stream().forEach(c -> clickRank.put(c.getMember().getNickname(), String.valueOf(c.getCount())));

        ClickResponse clickResponse = ClickResponse.builder()
                .count(String.valueOf(click.getCount()))
                .clickRank(clickRank)
                .build();

        return ResponseEntity.ok(clickResponse);
    }

    @PostMapping
    @Operation(
            summary = "[ HTTP 요청 + STOMP 응답 ] 클릭 API",
            description = """
                    # STOMP 응답을 받기 위한 조건
                    ## - 웹소켓 연결 : wss://crash-my-server.site/ws URL을 연결한 상태이어야 합니다.
                    ## - 웹소켓 구독 1 : /sub/click/{memberId} URL을 구독한 상태이어야 합니다.
                    ## - 웹소켓 구독 2 : /sub/click-rank URL을 구독한 상태이어야 합니다.
                    # 주의사항
                    ## - 웹소켓 구독 1 URL( /sub/click/{memberId} )을 구독한 본인에게만 STOMP 응답이 옵니다.
                    ## - STOMP 응답 형식 : {"nickname":"aaa","count":"1"}
                    ## - 웹소켓 구독 2 URL( /sub/click-rank )을 구독한 모든 클라이언트들에게 STOMP 응답이 옵니다.
                    ## - STOMP 응답 형식 : {"clickRank":{"aaa":"1","bbb":"2"}} (* 주의. 리스트는 정렬되지 않은 상태로 반환되므로 내림차순 정렬하여 클릭 랭크에 렌더링)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            ### 웹소켓 구독 1 URL( /sub/click/{memberId} )로 도착하는 STOMP 응답 형식 
                            - 응답 형식 : {"nickname":"aaa","count":"1"}
                            ### 웹소켓 구독 2 URL( /sub/click-rank )로 도착하는 STOMP 응답 형식 
                            - 응답 형식 : {"clickRank":{"aaa":"1","bbb":"2"}}
                            """
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            ### 설명 : 클릭한 적이 없는데 클릭 횟수를 조회할 때 반환될 예외 DTO 입니다. (발생 확률 없음)
                            - 예외 형식 : { "exceptionCode": 5001, "message":"NEVER CLICKED" }
                            """
            ),
            @ApiResponse(
                    responseCode = "401, 404",
                    description = """
                            ### 설명 : 토큰이 위변조될 경우 반환될 예외 DTO 입니다.
                            - 예외 형식 1 : { "exceptionCode": 4001, "message":"FAIL TO AUTHORIZATION" }
                            - 예외 형식 2 : { "exceptionCode": 4002, "message":"TOKEN EXPIRED" }
                            - 예외 형식 3 : { "exceptionCode": 4003, "message":"INVALID SIGNATURE" }
                            - 예외 형식 4 : { "exceptionCode": 4004, "message":"FORGED TOKEN" }
                            - 예외 형식 5 : { "exceptionCode": 4005, "message":"INVALID TOKEN" }
                            - 예외 형식 6 : { "exceptionCode": 4006, "message":"MEMBER NOT FOUND" }
                            """,
                    content = {@Content(schema = @Schema(implementation = ExceptionResponse.class))})
    })
    public void click(@AuthMember Long memberId) {
        clickService.click(memberId);
    }
}
