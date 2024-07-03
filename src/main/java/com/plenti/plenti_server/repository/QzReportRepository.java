package com.plenti.plenti_server.repository;

import com.plenti.plenti_server.entity.Member;
import com.plenti.plenti_server.entity.QzReport;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QzReportRepository extends JpaRepository<QzReport, Long> {
  List<QzReport> findByQzReportUser(Member qzReportUser);
}
