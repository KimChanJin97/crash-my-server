package cjkimhello97.toy.crashMyServer.chat.controller.dto;

import cjkimhello97.toy.crashMyServer.member.domain.Member;
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
