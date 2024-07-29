package com.fallt.qafordevs.service;

import com.fallt.qafordevs.entity.DeveloperEntity;
import com.fallt.qafordevs.exeption.DeveloperNotFoundException;
import com.fallt.qafordevs.exeption.DeveloperWithDuplicateEmailException;
import com.fallt.qafordevs.repository.DeveloperRepository;
import com.fallt.qafordevs.util.DataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeveloperServiceImplTest {

    @Mock
    private DeveloperRepository developerRepository;
    @InjectMocks
    private DeveloperServiceImpl serviceUnderTest;

    @Test
    @DisplayName("Test save developer functionality")
    void givenDeveloperToSave_whenSaveDeveloper_thenRepositoryIsCalled() {
        //given
        DeveloperEntity developerToSave = DataUtils.getJohnDoeTransient();
        BDDMockito.given(developerRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());
        BDDMockito.given(developerRepository.save(any(DeveloperEntity.class)))
                .willReturn(DataUtils.getJohnDoePersisted());
        //when
        DeveloperEntity savedDeveloper = serviceUnderTest.saveDeveloper(developerToSave);
        //then
        assertThat(savedDeveloper).isNotNull();
    }

    @Test
    @DisplayName("Test save developer with duplicate email functionality")
    void givenDeveloperToSaveWithDuplicateEmail_whenSaveDeveloper_thenExceptionIsThrow() {
        //given
        DeveloperEntity developerToSave = DataUtils.getJohnDoeTransient();
        BDDMockito.given(developerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(DataUtils.getJohnDoePersisted()));
        //when
        assertThrows(DeveloperWithDuplicateEmailException.class, () -> serviceUnderTest.saveDeveloper(developerToSave));
        //then
        verify(developerRepository, never()).save(any(DeveloperEntity.class));
    }

    @Test
    @DisplayName("Test update developer functionality")
    void givenDeveloperToUpdate_whenUpdateDeveloper_thenRepositoryIsCalled() {
        //given
        DeveloperEntity developerToUpdate = DataUtils.getJohnDoePersisted();
        BDDMockito.given(developerRepository.existsById(anyInt()))
                .willReturn(true);
        BDDMockito.given(developerRepository.save(any(DeveloperEntity.class)))
                .willReturn(developerToUpdate);
        //when
        DeveloperEntity updatedDeveloper = serviceUnderTest.updateDeveloper(developerToUpdate);
        //then
        assertThat(updatedDeveloper).isNotNull();
        verify(developerRepository, times(1)).save(any(DeveloperEntity.class));
    }

    @Test
    @DisplayName("Test update developer with incorrect id functionality")
    void givenDeveloperToUpdateWithIncorrectId_whenUpdateDeveloper_thenExceptionIsThrown() {
        //given
        DeveloperEntity developerToUpdate = DataUtils.getJohnDoePersisted();
        BDDMockito.given(developerRepository.existsById(anyInt()))
                .willReturn(false);
        //when
        assertThrows(DeveloperNotFoundException.class, () -> serviceUnderTest.updateDeveloper(developerToUpdate));
        //then
        verify(developerRepository, never()).save(any(DeveloperEntity.class));
    }

    @Test
    @DisplayName("Test get developer by id functionality")
    void givenId_whenGetById_thenDeveloperIsReturned() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt()))
                .willReturn(Optional.of(DataUtils.getJohnDoePersisted()));
        //when
        DeveloperEntity obtainedDeveloper = serviceUnderTest.getDeveloperById(1);
        //then
        assertThat(obtainedDeveloper).isNotNull();
    }

    @Test
    @DisplayName("Test get developer by incorrect id functionality")
    void givenIncorrectId_whenGetById_thenExceptionIsThrown() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt()))
                .willThrow(DeveloperNotFoundException.class);
        //when
        assertThrows(DeveloperNotFoundException.class, () -> serviceUnderTest.getDeveloperById(1));
        //then
    }

    @Test
    @DisplayName("Test get developer by email functionality")
    void givenEmail_whenGetDeveloperByEmail_thenDeveloperIsReturned() {
        //given
        String email = "john.doe@gmail.com";
        BDDMockito.given(developerRepository.findByEmail(anyString()))
                .willReturn(Optional.of(DataUtils.getJohnDoePersisted()));
        //when
        DeveloperEntity obtainedDeveloper = serviceUnderTest.getDeveloperByEmail(email);
        //then
        assertThat(obtainedDeveloper).isNotNull();
    }

    @Test
    @DisplayName("Test get developer by incorrect email functionality")
    void givenIncorrectEmail_whenGetDeveloperByEmail_thenExceptionIsThrown() {
        String email = "john.doe@gmail.com";
        BDDMockito.given(developerRepository.findByEmail(anyString()))
                .willThrow(DeveloperNotFoundException.class);
        //when
        assertThrows(DeveloperNotFoundException.class, () -> serviceUnderTest.getDeveloperByEmail(email));
    }

    @Test
    @DisplayName("Test get all developers functionality")
    void givenThreeDevelopers_whenGetAll_thenOnlyActiveAreReturned() {
        //given
        DeveloperEntity developer1 = DataUtils.getJohnDoePersisted();
        DeveloperEntity developer2 = DataUtils.getMikeSmithPersisted();
        DeveloperEntity developer3 = DataUtils.getFrankJonesPersisted();
        List<DeveloperEntity> developers = List.of(developer1, developer2, developer3);
        BDDMockito.given(developerRepository.findAll())
                .willReturn(developers);
        //when
        List<DeveloperEntity> obtainedDevelopers = serviceUnderTest.getAllDevelopers();
        //then
        assertThat(obtainedDevelopers).hasSize(2);
    }

    @Test
    @DisplayName("Test get all active by speciality functionality")
    void givenThreeDevelopersAndTwoActive_whenGetAllActiveBySpeciality_thenDevelopersAreReturned() {
        //given
        DeveloperEntity developer1 = DataUtils.getJohnDoePersisted();
        DeveloperEntity developer2 = DataUtils.getMikeSmithPersisted();
        List<DeveloperEntity> developers = List.of(developer1, developer2);
        BDDMockito.given(developerRepository.findAllActiveBySpeciality(anyString()))
                .willReturn(developers);
        //when
        List<DeveloperEntity> obtainedDevelopers = serviceUnderTest.getAllActiveBySpeciality("Java");
        //then
        assertThat(obtainedDevelopers).hasSize(2);
    }

    @Test
    @DisplayName("Test soft delete by id functionality")
    void givenId_whenSoftDeleteById_thenRepositorySaveMethodIsCalled() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt()))
                .willReturn(Optional.of(DataUtils.getJohnDoePersisted()));
        //when
        serviceUnderTest.softDeleteById(1);
        //then
        verify(developerRepository, times(1)).save(any(DeveloperEntity.class));
        verify(developerRepository, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("Test soft delete by incorrect id functionality")
    void givenIncorrectId_whenSoftDeleteById_thenExceptionIsThrown() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt()))
                .willReturn(Optional.empty());
        //when
        assertThrows(DeveloperNotFoundException.class, () -> serviceUnderTest.softDeleteById(1));
        //then
        verify(developerRepository, never()).save(any(DeveloperEntity.class));
    }

    @Test
    @DisplayName("Test hard delete by id functionality")
    void givenId_whenHardDeleteById_thenDeleteRepoMethodIsCalled() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt()))
                .willReturn(Optional.of(DataUtils.getJohnDoePersisted()));
        //when
        serviceUnderTest.hardDeleteById(1);
        //then
        verify(developerRepository, times(1)).deleteById(anyInt());
    }

    @Test
    @DisplayName("Test hard delete by incorrect id functionality")
    void givenIncorrectId_whenHardDeleteById_thenExceptionIsThrown() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt()))
                .willReturn(Optional.empty());
        //when
        assertThrows(DeveloperNotFoundException.class, () -> serviceUnderTest.hardDeleteById(1));
        //then
        verify(developerRepository, never()).deleteById(anyInt());
    }
}
