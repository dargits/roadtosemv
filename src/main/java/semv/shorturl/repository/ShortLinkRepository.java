package semv.shorturl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import semv.shorturl.entity.ShortLink;

@Repository
public interface ShortLinkRepository extends JpaRepository<ShortLink, Long> {

}
