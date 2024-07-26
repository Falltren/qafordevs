package com.fallt.qafordevs.service;

import com.fallt.qafordevs.entity.DeveloperEntity;
import com.fallt.qafordevs.entity.Status;
import com.fallt.qafordevs.exeption.DeveloperNotFoundException;
import com.fallt.qafordevs.exeption.DeveloperWithDuplicateEmailException;
import com.fallt.qafordevs.repository.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeveloperServiceImpl implements DeveloperService {

    private final DeveloperRepository developerRepository;

    @Override
    public DeveloperEntity saveDeveloper(DeveloperEntity developer) {
        Optional<DeveloperEntity> duplicateCandidate = developerRepository.findByEmail(developer.getEmail());
        if (duplicateCandidate.isPresent()) {
            throw new DeveloperWithDuplicateEmailException(MessageFormat.format("Email {0} already use", developer.getEmail()));
        }
        return developerRepository.save(developer);
    }

    @Override
    public DeveloperEntity updateDeveloper(DeveloperEntity developer) {
        boolean isExist = developerRepository.existsById(developer.getId());
        if (!isExist) {
            throw new DeveloperNotFoundException(MessageFormat.format("Developer with ID: {0} not found", developer.getId()));
        }
        return developerRepository.save(developer);
    }

    @Override
    public DeveloperEntity getDeveloperById(Integer id) {
        return developerRepository.findById(id).orElseThrow(
                () -> new DeveloperNotFoundException(MessageFormat.format("Developer with ID: {0} not found", id)));
    }

    @Override
    public DeveloperEntity getDeveloperByEmail(String email) {
        Optional<DeveloperEntity> obtainedDeveloper = developerRepository.findByEmail(email);
        if (obtainedDeveloper.isEmpty()) {
            throw new DeveloperNotFoundException(MessageFormat.format("Developer with email: {0} not found", email));
        }
        return obtainedDeveloper.get();
    }

    @Override
    public List<DeveloperEntity> getAllDevelopers() {
        return developerRepository.findAll().stream()
                .filter(d -> d.getStatus().equals(Status.ACTIVE))
                .toList();
    }

    @Override
    public List<DeveloperEntity> getAllActiveBySpeciality(String speciality) {
        return developerRepository.findAllActiveBySpeciality(speciality);
    }

    @Override
    public void softDeleteById(Integer id) {
        DeveloperEntity obtainedDeveloper = getDeveloperById(id);
        obtainedDeveloper.setStatus(Status.DELETED);
        developerRepository.save(obtainedDeveloper);
    }

    @Override
    public void hardDeleteById(Integer id) {
        DeveloperEntity obtainedDeveloper = getDeveloperById(id);
        developerRepository.deleteById(obtainedDeveloper.getId());
    }
}
