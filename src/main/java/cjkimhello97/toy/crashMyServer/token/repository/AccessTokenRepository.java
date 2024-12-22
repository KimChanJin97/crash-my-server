package cjkimhello97.toy.crashMyServer.token.repository;

import cjkimhello97.toy.crashMyServer.token.domain.AccessToken;
import org.springframework.data.repository.CrudRepository;

public interface AccessTokenRepository extends CrudRepository<AccessToken, Long> {

}
