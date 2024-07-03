package com.plenti.plenti_server.repository;

import com.plenti.plenti_server.entity.CmtReport;
import com.plenti.plenti_server.entity.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CmtReportRepository extends JpaRepository<CmtReport, Long> {
  List<CmtReport> findByCmtReportUser(Member cmtReportUser);
}
