package com.fallt.qafordevs.repository;

import com.fallt.qafordevs.entity.DeveloperEntity;
import com.fallt.qafordevs.util.DataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DeveloperRepositoryTest {

    @Autowired
    private DeveloperRepository developerRepository;

    @BeforeEach
    public void setUp() {
        developerRepository.deleteAll();
    }

    @Test
    @DisplayName("Test save developer functionality")
    void givenDeveloperObject_whenSave_thenDeveloperIsCreated() {
        //given
        DeveloperEntity developerToSave = DataUtils.getJohnDoeTransient();
        //when
        DeveloperEntity savedDeveloper = developerRepository.save(developerToSave);
        //then
        assertThat(savedDeveloper).isNotNull();
        assertThat(savedDeveloper.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test update developer functionality")
    void givenDeveloperToUpdate_whenSave_thenEmailIsChanged() {
        //given
        String updatedEmail = "updated@gmail.com";
        DeveloperEntity developerToCreate = DataUtils.getJohnDoeTransient();
        developerRepository.save(developerToCreate);
        //when
        Optional<DeveloperEntity> optionalDeveloper = developerRepository.findById(developerToCreate.getId());
        if (optionalDeveloper.isEmpty()) {
            throw new RuntimeException("Incorrect ID");
        }
        DeveloperEntity developerToUpdate = optionalDeveloper.get();
        developerToUpdate.setEmail(updatedEmail);
        DeveloperEntity updatedDeveloper = developerRepository.save(developerToUpdate);
        //then
        assertThat(updatedDeveloper).isNotNull();
        assertThat(updatedDeveloper.getEmail()).isEqualTo(updatedEmail);
    }

    @Test
    @DisplayName("Test get developer by id functionality")
    void givenDeveloperCreated_whenGetById_thenDeveloperReturn() {
        //given
        DeveloperEntity developerToSave = DataUtils.getJohnDoeTransient();
        developerRepository.save(developerToSave);
        //when
        Optional<DeveloperEntity> optionalDeveloper = developerRepository.findById(developerToSave.getId());
        if (optionalDeveloper.isEmpty()) {
            throw new RuntimeException("Incorrect ID");
        }
        DeveloperEntity obtainedDeveloper = optionalDeveloper.get();
        //then
        assertThat(obtainedDeveloper).isNotNull();
        assertThat(obtainedDeveloper.getEmail()).isEqualTo("john.doe@gmail.com");
    }

    @Test
    @DisplayName("Test developer not found functionality")
    void givenDeveloperIsNotCreated_whenGetById_thenOptionalIsEmpty() {
        //given
        //when
        Optional<DeveloperEntity> optionalDeveloper = developerRepository.findById(1);
        //then
        assertThat(optionalDeveloper).isEmpty();
    }

    @Test
    @DisplayName("Test get all developers functionality")
    void givenThreeDevelopersAreStored_whenFindAll_thenAllDevelopersAreReturned() {
        //given
        DeveloperEntity developer1 = DataUtils.getJohnDoeTransient();
        DeveloperEntity developer2 = DataUtils.getMikeSmithTransient();
        DeveloperEntity developer3 = DataUtils.getFrankJonesTransient();
        developerRepository.saveAll(List.of(developer1, developer2, developer3));
        //when
        List<DeveloperEntity> obtainedDevelopers = developerRepository.findAll();
        //then
        assertThat(CollectionUtils.isEmpty(obtainedDevelopers)).isFalse();
        assertThat(obtainedDevelopers).hasSize(3);
    }

    @Test
    @DisplayName("Test get developer by email functionality")
    void givenDeveloperSaved_whenGetByEmail_thenDeveloperIsReturned() {
        //given
        DeveloperEntity developer = DataUtils.getJohnDoeTransient();
        developerRepository.save(developer);
        //when
        String email = "john.doe@gmail.com";
        Optional<DeveloperEntity> optionalDeveloper = developerRepository.findByEmail(email);
        if (optionalDeveloper.isEmpty()) {
            throw new RuntimeException(MessageFormat.format("Developer with email {0} does not exist", email));
        }
        DeveloperEntity obtainedDeveloper = optionalDeveloper.get();
        //then
        assertThat(obtainedDeveloper.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Get all active developers by speciality")
    void givenThreeDevelopersAndTwoAreActive_whenFindAllActiveBySpeciality_thenReturnedOnlyTwoDevelopers() {
        //given
        DeveloperEntity developer1 = DataUtils.getJohnDoeTransient();
        DeveloperEntity developer2 = DataUtils.getMikeSmithTransient();
        DeveloperEntity developer3 = DataUtils.getFrankJonesTransient();
        developerRepository.saveAll(List.of(developer1, developer2, developer3));
        //when
        List<DeveloperEntity> activeDevelopers = developerRepository.findAllActiveBySpeciality("Java");
        //then
        assertThat(activeDevelopers).hasSize(2);
    }

    @Test
    @DisplayName("Test delete developer by id functionality")
    void givenDeveloperIsSaved_whenDeletedById_thenDeveloperIsRemovedFromDB() {
        //given
        DeveloperEntity developer = DataUtils.getJohnDoeTransient();
        developerRepository.save(developer);
        //when
        developerRepository.deleteById(developer.getId());
        //then
        Optional<DeveloperEntity> optionalDeveloper = developerRepository.findById(developer.getId());
        assertThat(optionalDeveloper).isEmpty();
    }

}
