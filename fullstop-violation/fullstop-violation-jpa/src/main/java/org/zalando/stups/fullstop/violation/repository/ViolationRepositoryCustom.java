package org.zalando.stups.fullstop.violation.repository;

import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.zalando.stups.fullstop.violation.entity.CountByAccountAndType;
import org.zalando.stups.fullstop.violation.entity.CountByAppVersionAndType;
import org.zalando.stups.fullstop.violation.entity.ViolationEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by gkneitschel.
 */
@Repository
public interface ViolationRepositoryCustom {

    Page<ViolationEntity> queryViolations(List<String> accounts, DateTime from, DateTime to, Long lastViolation, boolean checked,
                                          Integer severity, final Integer priority, Boolean auditRelevant, String type, boolean whitelisted, Pageable pageable);

    boolean violationExists(String accountId, String region, String eventId, String instanceId, String violationType);

    List<CountByAccountAndType> countByAccountAndType(Set<String> accountIds, Optional<DateTime> from,
                                                      Optional<DateTime> to, boolean resolved, boolean whitelisted);

    List<CountByAppVersionAndType> countByAppVersionAndType(String account, Optional<DateTime> from,
                                                      Optional<DateTime> to, boolean resolved, boolean whitelisted);
}
