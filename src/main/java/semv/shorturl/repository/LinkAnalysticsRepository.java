package semv.shorturl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import semv.shorturl.entity.LinkAnalytics;

@Repository
public interface LinkAnalysticsRepository extends JpaRepository<LinkAnalytics, Long> {
    public Long countByLinkId(Long linkId);

    public List<LinkAnalytics> findTop20ByLinkIdOrderByClickedAtDesc(Long linkId);
}
