package semv.shorturl.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import semv.shorturl.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public boolean existsByAccount(String account);

    public Optional<User> findByAccount(String account);
}
