package data_jpa_tutorial.spring_data_jpa_practice.Service;

import data_jpa_tutorial.spring_data_jpa_practice.Entity.Applicant;
import data_jpa_tutorial.spring_data_jpa_practice.Repository.ApplicantRepository;
import data_jpa_tutorial.spring_data_jpa_practice.Repository.ApplicationPagingAndSortingRepository;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicantService {

    private ApplicantRepository applicantRepository;

    private ApplicationPagingAndSortingRepository applicationPagingAndSortingRepository;

    private Counter counter;
    private Timer timer;
    private DistributionSummary summary;

    public ApplicantService(ApplicantRepository applicantRepository, ApplicationPagingAndSortingRepository applicationPagingAndSortingRepository, MeterRegistry registry){
        this.applicantRepository = applicantRepository;
        this.applicationPagingAndSortingRepository = applicationPagingAndSortingRepository;
        this.counter = registry.counter("applicant.counter");
        this.timer = registry.timer("applicant.timer");
        this.summary = registry.summary("applicant.summary");
    }

    public Optional<Applicant> getApplicant(Long id){
        counter.increment();
        return applicantRepository.findById(id);
    }

    public List<Applicant> getAllApplicants(){
        return timer.record(() -> applicantRepository.findAll());
    }

    @Timed
    public Applicant addApplicant(Applicant applicant){
        return applicantRepository.save(applicant);
    }

    public void deleteApplicant(Long id){
        applicantRepository.deleteById(id);
    }

    public Iterable<Applicant> getApplicantWithPagination(int page, int size){
        return applicationPagingAndSortingRepository.findAll(PageRequest.of(page, size));
    }

    public List<Applicant> findByStatus(String status){
        List<Applicant> applicants = applicantRepository.findByStatus(status);
        summary.record(applicants.size());
        return applicants;
    }

    public List<Applicant> findByPartialName(String name){
        return applicantRepository.findApplicantsByPartialName(name);
    }

    public Applicant findByPhone(String phone){
        return applicantRepository.findByPhone(phone);
    }
}
