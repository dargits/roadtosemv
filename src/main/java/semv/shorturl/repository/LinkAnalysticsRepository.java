package semv.shorturl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import semv.shorturl.entity.LinkAnalytics;

@Repository
public interface LinkAnalysticsRepository extends JpaRepository<LinkAnalytics, Long> {

}
