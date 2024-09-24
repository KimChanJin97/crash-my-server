package cjkimhello97.toy.crashMyServer.common.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Column(updatable = false, nullable = false)
    @CreatedDate
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-ddTHH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-ddTHH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;
}
