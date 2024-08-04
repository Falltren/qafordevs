package com.fallt.qafordevs.it;

import com.fallt.qafordevs.dto.DeveloperDto;
import com.fallt.qafordevs.entity.DeveloperEntity;
import com.fallt.qafordevs.entity.Status;
import com.fallt.qafordevs.repository.DeveloperRepository;
import com.fallt.qafordevs.util.DataUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItDeveloperControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeveloperRepository developerRepository;

    @BeforeEach
    public void setUp() {
        developerRepository.deleteAll();
    }

    @Test
    @DisplayName("Test create developer functionality")
    void givenDeveloperDto_whenCreateDeveloper_thenSuccessResponse() throws Exception {
        //given
        DeveloperDto dto = DataUtils.getJohnDoeDtoTransient();
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$.firstName", CoreMatchers.is("John")))
                .andExpect(jsonPath("$.lastName", CoreMatchers.is("Doe")))
                .andExpect(jsonPath("$.status", CoreMatchers.is("ACTIVE")));
    }

    @Test
    @DisplayName("Test create developer with duplicate email functionality")
    void givenDeveloperDtoWithDuplicateEmail_whenCreateDeveloper_thenErrorResponse() throws Exception {
        //given
        String duplicateEmail = "duplicate@gmail.com";
        DeveloperEntity developer = DataUtils.getJohnDoeTransient();
        developer.setEmail(duplicateEmail);
        developerRepository.save(developer);
        DeveloperDto dto = DataUtils.getJohnDoeDtoTransient();
        dto.setEmail(duplicateEmail);
        String message = "Email duplicate@gmail.com already use";
        //when
        ResultActions result = mockMvc.perform(post("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", CoreMatchers.is(400)))
                .andExpect(jsonPath("$.message", CoreMatchers.is(message)));
    }

    @Test
    @DisplayName("Test update developer functionality")
    void givenDeveloperDto_whenUpdateDeveloper_thenSuccessResponse() throws Exception {
        String updatedEmail = "updated@gmail.com";
        DeveloperDto dto = DataUtils.getJohnDoeDtoPersisted();
        DeveloperEntity entity = DataUtils.getJohnDoePersisted();
        developerRepository.save(entity);
        dto.setEmail(updatedEmail);
        //when
        ResultActions result = mockMvc.perform(put("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$.firstName", CoreMatchers.is("John")))
                .andExpect(jsonPath("$.lastName", CoreMatchers.is("Doe")))
                .andExpect(jsonPath("$.email", CoreMatchers.is(updatedEmail)))
                .andExpect(jsonPath("$.status", CoreMatchers.is("ACTIVE")));
    }

    @Test
    @DisplayName("Test update developer with incorrect id functionality")
    void givenDeveloperDtoWithIncorrectId_whenUpdateDeveloper_thenErrorResponse() throws Exception {
        //given
        DeveloperDto dto = DataUtils.getJohnDoeDtoPersisted();
        //when
        ResultActions result = mockMvc.perform(put("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        result.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", CoreMatchers.is(404)))
                .andExpect(jsonPath("$.message", CoreMatchers.is("Developer with ID: " + dto.getId() + " not found")));
    }

    @Test
    @DisplayName("Test get developer by id functionality")
    void givenId_whenGetById_thenSuccessResponse() throws Exception {
        //given
        DeveloperEntity entity = DataUtils.getJohnDoeTransient();
        developerRepository.save(entity);
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/developers/" + entity.getId())
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", CoreMatchers.is(1)))
                .andExpect(jsonPath("$.firstName", CoreMatchers.is("John")))
                .andExpect(jsonPath("$.lastName", CoreMatchers.is("Doe")))
                .andExpect(jsonPath("$.status", CoreMatchers.is("ACTIVE")));
    }

    @Test
    @DisplayName("Test get developer by incorrect id functionality")
    void givenIncorrectId_whenGetById_thenErrorResponse() throws Exception {
        //given
        String id = "1";
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/developers/" + id)
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", CoreMatchers.is(404)))
                .andExpect(jsonPath("$.message", CoreMatchers.is("Developer with ID: " + id + " not found")));
    }

    @Test
    @DisplayName("Test soft delete developer functionality")
    void givenId_whenSoftDelete_thenSuccessResponse() throws Exception {
        //given
        DeveloperEntity developer = DataUtils.getJohnDoeTransient();
        developerRepository.save(developer);
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/developers/" + developer.getId())
                .contentType(MediaType.APPLICATION_JSON));
        //then
        DeveloperEntity obtainedEntity = developerRepository.findById(developer.getId()).orElseThrow();
        assertThat(obtainedEntity.getStatus()).isEqualTo(Status.DELETED);
        result
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("Test soft delete developer by incorrect id functionality")
    void givenIncorrectId_whenSoftDelete_thenErrorResponse() throws Exception {
        //given
        String id = "1";
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/developers/" + id)
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", CoreMatchers.is(404)))
                .andExpect(jsonPath("$.message", CoreMatchers.is("Developer with ID: " + id + " not found")));
    }

    @Test
    @DisplayName("Test hard delete developer functionality")
    void givenId_whenHardDelete_thenSuccessResponse() throws Exception {
        //given
        DeveloperEntity developer = DataUtils.getJohnDoeTransient();
        developerRepository.save(developer);
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/developers/" + developer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .param("isHard", "true"));
        //then
        DeveloperEntity obtainedDeveloper = developerRepository.findById(developer.getId()).orElse(null);
        assertThat(obtainedDeveloper).isNull();
        result
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test hard delete developer by incorrect id functionality")
    void givenIncorrectId_whenHardDelete_thenErrorResponse() throws Exception {
        //given
        String id = "1";
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/developers/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .param("isHard", "true"));
        //then
        result
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", CoreMatchers.is(404)))
                .andExpect(jsonPath("$.message", CoreMatchers.is("Developer with ID: " + id + " not found")));
    }
}
