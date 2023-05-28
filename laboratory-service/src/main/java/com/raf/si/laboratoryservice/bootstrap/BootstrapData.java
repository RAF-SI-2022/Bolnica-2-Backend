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

        LabAnalysis analysis = new LabAnalysis();
        analysis.setName("Glukoza");
        analysis.setAbbreviation("GLU");
        labAnalysisRepository.save(analysis);
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


        LabAnalysis analysis2 = new LabAnalysis();
        analysis2.setName("Gvožđe");
        analysis2.setAbbreviation("GVO") ;
        labAnalysisRepository.save(analysis2);
        Parameter parameter2 = new Parameter();
        parameter2.setName("Gvožđe");
        parameter2.setMeasureUnit("mg");
        parameter2.setType(ParameterType.NUMERICKA);
        parameter2.setLowerBound(0.50);
        parameter2.setUpperBound(10.90);
        parameterRepository.save(parameter2);
        AnalysisParameter analysisParameter2 = new AnalysisParameter();
        analysisParameter2.setAnalysis(analysis2);
        analysisParameter2.setParameter(parameter2);
        analysisParameterRepository.save(analysisParameter2);


        LabAnalysis analysis3 = new LabAnalysis();
        analysis3.setName("Lipidni status");
        analysis3.setAbbreviation("LS") ;
        labAnalysisRepository.save(analysis3);
        Parameter parameter3 = new Parameter();
        parameter3.setName("Lipidni status");
        parameter3.setMeasureUnit("/");
        parameter3.setType(ParameterType.TEKSTUALNA);
        parameter3.setLowerBound(0.00);
        parameter3.setUpperBound(0.00);
        parameterRepository.save(parameter3);
        AnalysisParameter analysisParameter3 = new AnalysisParameter();
        analysisParameter3.setAnalysis(analysis3);
        analysisParameter3.setParameter(parameter3);
        analysisParameterRepository.save(analysisParameter3);


        LabAnalysis analysis4 = new LabAnalysis();
        analysis4.setName("Enzimi");
        analysis4.setAbbreviation("ENZ") ;
        labAnalysisRepository.save(analysis4);
        Parameter parameter4 = new Parameter();
        parameter4.setName("Enzimi");
        parameter4.setMeasureUnit("/");
        parameter4.setType(ParameterType.TEKSTUALNA);
        parameter4.setLowerBound(0.00);
        parameter4.setUpperBound(0.00);
        parameterRepository.save(parameter4);
        AnalysisParameter analysisParameter4 = new AnalysisParameter();
        analysisParameter4.setAnalysis(analysis4);
        analysisParameter4.setParameter(parameter4);
        analysisParameterRepository.save(analysisParameter4);

        AnalysisParameterResult result = new AnalysisParameterResult();
        result.setLabWorkOrder(workOrder);
        result.setAnalysisParameter(analysisParameter);
        result.setResult("Sve dobro");
        result.setDateAndTime(new Date());
        result.setLbzBiochemist(UUID.fromString("5a2e71bb-e4ee-43dd-a3ad-28e043f8b435"));

        analysisParameterResultRepository.save(result);
    }
}