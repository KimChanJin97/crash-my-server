package cjkimhello97.toy.crashMyServer.chat.dto;

import cjkimhello97.toy.crashMyServer.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "[ HTTP ] SimpleMemberResponse : 멤버 정보 조회 응답 DTO")
public class SimpleMemberResponse {

    private Long memberId;
    private String name;

    public static SimpleMemberResponse from(Member member) {
        return SimpleMemberResponse.builder()
                .memberId(member.getMemberId())
                .name(member.getNickname())
                .build();
    }
}

