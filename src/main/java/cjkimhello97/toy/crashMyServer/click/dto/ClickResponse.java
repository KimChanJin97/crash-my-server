package cjkimhello97.toy.crashMyServer.click.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.Builder;

@Builder
@Schema(title = "[ HTTP ] ClickResponse : 클릭(본인 클릭 횟수 조회 + 클릭 랭크 조회) 응답 DTO")
public record ClickResponse(
        String count,
        Map<String, String> clickRank
) {

}
