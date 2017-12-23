package es.udc.fi.dc.fd.ranking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {

	public Page<Ranking> findByRaceIdOrderByPosition(Long raceId, Pageable page);

}
