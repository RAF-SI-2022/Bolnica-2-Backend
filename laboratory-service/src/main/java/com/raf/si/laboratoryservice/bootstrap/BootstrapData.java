package com.raf.si.laboratoryservice.bootstrap;

import com.raf.si.laboratoryservice.model.*;
import com.raf.si.laboratoryservice.model.enums.labworkorder.OrderStatus;
import com.raf.si.laboratoryservice.model.enums.parameter.ParameterType;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralStatus;
import com.raf.si.laboratoryservice.model.enums.referral.ReferralType;
import com.raf.si.laboratoryservice.model.enums.scheduledlabexam.ExamStatus;
import com.raf.si.laboratoryservice.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Component
public class BootstrapData implements CommandLineRunner {

    private final AnalysisParameterRepository analysisParameterRepository;
    private final AnalysisParameterResultRepository analysisParameterResultRepository;
    private final LabAnalysisRepository labAnalysisRepository;
    private final LabWorkOrderRepository labWorkOrderRepository;
    private final ParameterRepository parameterRepository;
    private final ReferralRepository referralRepository;
    private final ScheduledLabExamRepository scheduledLabExamRepository;

    public BootstrapData(AnalysisParameterRepository analysisParameterRepository,
                         AnalysisParameterResultRepository analysisParameterResultRepository,
                         LabAnalysisRepository labAnalysisRepository,
                         LabWorkOrderRepository labWorkOrderRepository,
                         ParameterRepository parameterRepository,
                         ReferralRepository referralRepository,
                         ScheduledLabExamRepository scheduledLabExamRepository) {

        this.analysisParameterRepository = analysisParameterRepository;
        this.analysisParameterResultRepository = analysisParameterResultRepository;
        this.labAnalysisRepository = labAnalysisRepository;
        this.labWorkOrderRepository = labWorkOrderRepository;
        this.parameterRepository = parameterRepository;
        this.referralRepository = referralRepository;
        this.scheduledLabExamRepository = scheduledLabExamRepository;
    }

    @Override
    public void run(String... args) {
        ScheduledLabExam exam = new ScheduledLabExam();
        exam.setPbo(UUID.fromString("4e5911c8-ce7a-11ed-afa1-0242ac120002"));
        exam.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));
        exam.setExamStatus(ExamStatus.ZAKAZANO);
        exam.setNote("Napomena");
        exam.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        exam.setScheduledDate(new Date());

        scheduledLabExamRepository.save(exam);


        Referral referral = new Referral();
        referral.setType(ReferralType.LABORATORIJA);
        referral.setLbz(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        referral.setPboReferredFrom(UUID.fromString("4e5911c8-ce7a-11ed-afa1-0242ac120002"));
        referral.setPboReferredTo(UUID.fromString("4e5911c8-ce7a-11ed-afa1-0242ac120002"));
        referral.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));
        referral.setStatus(ReferralStatus.NEREALIZOVAN);
        referral.setRequiredAnalysis("Glukoza");
        referral.setComment("Komentar");
        referral.setReferralDiagnosis("Mononukleoza");
        referral.setReferralReason("Provera krvne slike pacijenta, da li je mononukleoza prosla");
        referral.setDeleted(false);


        LabWorkOrder workOrder = new LabWorkOrder();
        workOrder.setLbp(UUID.fromString("c208f04d-9551-404e-8c54-9321f3ae9be8"));
        workOrder.setStatus(OrderStatus.NEOBRADJEN);
        workOrder.setLbzTechnician(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));
        workOrder.setLbzBiochemist(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));

        referral.setLabWorkOrder(workOrder);
        referralRepository.save(referral);
        labWorkOrderRepository.save(workOrder);

        List<LabAnalysis> labAnalysisList = new ArrayList<>();

        LabAnalysis analysis = new LabAnalysis();
        analysis.setName("Glukoza");
        analysis.setAbbreviation("GLU");
        labAnalysisList.add(analysis);

        LabAnalysis analysis2 = new LabAnalysis();
        analysis2.setName("Gvožđe");
        analysis2.setAbbreviation("GVO") ;
        labAnalysisList.add(analysis2);


        LabAnalysis analysis3 = new LabAnalysis();
        analysis3.setName("Lipidni status");
        analysis3.setAbbreviation("LS") ;
        labAnalysisList.add(analysis3);

        LabAnalysis analysis4 = new LabAnalysis();
        analysis4.setName("Enzimi");
        analysis4.setAbbreviation("ENZ") ;
        labAnalysisList.add(analysis4);

        labAnalysisRepository.saveAll(labAnalysisList);

        Parameter parameter = new Parameter();
        parameter.setName("Glukoza");
        parameter.setMeasureUnit("mmol/L");
        parameter.setType(ParameterType.NUMERICKA);
        parameter.setLowerBound(3.90);
        parameter.setUpperBound(6.10);

        parameterRepository.save(parameter);


        AnalysisParameter analysisParameter = new AnalysisParameter();
        analysisParameter.setAnalysis(analysis);
        analysisParameter.setParameter(parameter);

        analysisParameterRepository.save(analysisParameter);


        AnalysisParameterResult result = new AnalysisParameterResult();
        result.setLabWorkOrder(workOrder);
        result.setAnalysisParameter(analysisParameter);
        result.setResult("Sve dobro");
        result.setDateAndTime(new Date());
        result.setLbzBiochemist(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));

        analysisParameterResultRepository.save(result);
    }
}