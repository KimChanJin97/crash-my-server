package cjkimhello97.toy.crashMyServer.redis.repository;

import cjkimhello97.toy.crashMyServer.redis.domain.AccessToken;
import org.springframework.data.repository.CrudRepository;

public interface AccessTokenRepository extends CrudRepository<AccessToken, String> {

}
