package cjkimhello97.toy.crashMyServer.kafka.dto;

import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaClickRankRequest implements Serializable {

    private Map<String, String> clickRank;
}
