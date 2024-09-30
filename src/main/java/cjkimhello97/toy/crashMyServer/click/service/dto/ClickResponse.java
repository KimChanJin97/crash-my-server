package cjkimhello97.toy.crashMyServer.click.service.dto;

import java.util.Map;
import lombok.Builder;

@Builder
public record ClickResponse(
        String count,
        Map<String, String> clickRank
) {

}
