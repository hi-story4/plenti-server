package com.plenti.plenti_server.repository;

import com.plenti.plenti_server.entity.Member;
import com.plenti.plenti_server.entity.Scrap;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
  List<Scrap> findAllByScrapUser(Member scrapUser);
  Optional<Scrap> findById(Long scrapId);
}
