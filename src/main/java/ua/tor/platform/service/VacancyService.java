package ua.tor.platform.service;

import org.springframework.stereotype.Service;
import ua.tor.platform.persistent.Vacancy;
import ua.tor.platform.repository.IVacancyRepository;

import java.util.Collection;

@Service
public class VacancyService {

    private final IVacancyRepository vacancyRepository;

    public VacancyService(IVacancyRepository vacancyRepository) {
        this.vacancyRepository = vacancyRepository;
    }

    public void saveAll(Collection<Vacancy> vacancies) {
        vacancyRepository.saveAll(vacancies);
    }
}
