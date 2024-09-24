package cjkimhello97.toy.crashMyServer.click.domain;

import static jakarta.persistence.FetchType.LAZY;

import cjkimhello97.toy.crashMyServer.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@EqualsAndHashCode(of = "clickId", callSuper = false)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Click implements Comparable<Click> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "click_id", insertable = false, updatable = false)
    private Long clickId;

    @Column(name = "count")
    private Double count;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Click addCount() {
        this.count++;
        return this;
    }

    @Override
    public int compareTo(Click other) {
        return this.count.compareTo(other.getCount());
    }
}
