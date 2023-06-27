package com.raf.si.patientservice.repository.filtering.specification;

import com.raf.si.patientservice.model.*;
import com.raf.si.patientservice.repository.filtering.filter.CovidCertificateFilter;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CovidCertificateSpecification implements Specification<CovidCertificate> {

    private final CovidCertificateFilter filter;

    public CovidCertificateSpecification(CovidCertificateFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<CovidCertificate> root, @NonNull CriteriaQuery<?> query,
                                 @NonNull CriteriaBuilder criteriaBuilder) {
        Join<VaccinationCovid, CovidCertificate> vaccinationJoin = root.join("vaccinationCovid", JoinType.LEFT);
        Join<ScheduledVaccinationCovid, VaccinationCovid> scheduledVaccinationCovidJoin = vaccinationJoin.join("scheduledVaccinationCovid", JoinType.LEFT);
        Join<Patient, ScheduledVaccinationCovid> patientVaccineJoin = scheduledVaccinationCovidJoin.join("patient", JoinType.LEFT);

        Join<Testing, CovidCertificate> testingJoin = root.join("testing", JoinType.LEFT);
        Join<Patient, Testing> patientTestingJoin = testingJoin.join("patient", JoinType.LEFT);

        Path<UUID> lbpVaccinePatient = patientVaccineJoin.get("lbp");
        Path<UUID> lbpTestingPatient = patientTestingJoin.get("lbp");
        Path<LocalDateTime> dateApply = root.get("dateApply");
        Path<LocalDateTime> dateEnd = root.get("endDate");

        final List<Predicate> predicates = new ArrayList<>();

        if (filter.getApply() != null)
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(dateApply, filter.getApply()));
        if (filter.getEnd() != null)
            predicates.add(criteriaBuilder.lessThanOrEqualTo(dateEnd, filter.getEnd()));

        predicates.add(criteriaBuilder.or(criteriaBuilder.equal(lbpVaccinePatient, filter.getLbp()),
                criteriaBuilder.equal(lbpTestingPatient, filter.getLbp())));

        query.orderBy(criteriaBuilder.desc(dateEnd));

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
