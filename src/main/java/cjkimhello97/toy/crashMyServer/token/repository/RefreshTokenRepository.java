package cjkimhello97.toy.crashMyServer.token.repository;

import cjkimhello97.toy.crashMyServer.token.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

}
