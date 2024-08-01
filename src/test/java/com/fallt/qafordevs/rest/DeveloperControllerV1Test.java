package com.fallt.qafordevs.rest;

import com.fallt.qafordevs.dto.DeveloperDto;
import com.fallt.qafordevs.entity.DeveloperEntity;
import com.fallt.qafordevs.exeption.DeveloperNotFoundException;
import com.fallt.qafordevs.exeption.DeveloperWithDuplicateEmailException;
import com.fallt.qafordevs.service.DeveloperService;
import com.fallt.qafordevs.util.DataUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class DeveloperControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeveloperService developerService;

    @Test
    @DisplayName("Test create developer functionality")
    void givenDeveloperDto_whenCreateDeveloper_thenSuccessResponse() throws Exception {
        //given
        DeveloperDto dto = DataUtils.getJohnDoeDtoTransient();
        DeveloperEntity entity = DataUtils.getJohnDoePersisted();
        BDDMockito.given(developerService.saveDeveloper(any(DeveloperEntity.class)))
                .willReturn(entity);
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
        DeveloperDto dto = DataUtils.getJohnDoeDtoTransient();
        String message = "Developer with defined email already exists";
        BDDMockito.given(developerService.saveDeveloper(any(DeveloperEntity.class)))
                .willThrow(new DeveloperWithDuplicateEmailException(message));
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
        DeveloperDto dto = DataUtils.getJohnDoeDtoPersisted();
        DeveloperEntity entity = DataUtils.getJohnDoePersisted();
        BDDMockito.given(developerService.updateDeveloper(any(DeveloperEntity.class)))
                .willReturn(entity);
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
                .andExpect(jsonPath("$.status", CoreMatchers.is("ACTIVE")));
    }

    @Test
    @DisplayName("Test update developer with incorrect id functionality")
    void givenDeveloperDtoWithIncorrectId_whenUpdateDeveloper_thenErrorResponse() throws Exception {
        DeveloperDto dto = DataUtils.getJohnDoeDtoPersisted();
        String message = "Developer not found";
        BDDMockito.given(developerService.updateDeveloper(any(DeveloperEntity.class)))
                .willThrow(new DeveloperNotFoundException(message));
        //when
        ResultActions result = mockMvc.perform(put("/api/v1/developers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));
        //then
        result.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", CoreMatchers.is(404)))
                .andExpect(jsonPath("$.message", CoreMatchers.is(message)));
    }

    @Test
    @DisplayName("Test get developer by id functionality")
    void givenId_whenGetById_thenSuccessResponse() throws Exception {
        //given
        BDDMockito.given(developerService.getDeveloperById(anyInt()))
                .willReturn(DataUtils.getJohnDoePersisted());
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/developers/1")
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
        String message = "Developer not found";
        BDDMockito.given(developerService.getDeveloperById(anyInt()))
                .willThrow(new DeveloperNotFoundException(message));
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/developers/1")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", CoreMatchers.is(404)))
                .andExpect(jsonPath("$.message", CoreMatchers.is(message)));
    }

    @Test
    @DisplayName("Test soft delete developer functionality")
    void givenId_whenSoftDelete_thenSuccessResponse() throws Exception {
        //given
        BDDMockito.doNothing().when(developerService).softDeleteById(anyInt());
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/developers/1")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        verify(developerService, times(1)).softDeleteById(anyInt());
        result
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test soft delete developer by incorrect id functionality")
    void givenIncorrectId_whenSoftDelete_thenErrorResponse() throws Exception {
        //given
        String message = "Developer not found";
        BDDMockito.doThrow(new DeveloperNotFoundException(message))
                .when(developerService).softDeleteById(anyInt());
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/developers/1")
                .contentType(MediaType.APPLICATION_JSON));
        //then
        result
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", CoreMatchers.is(404)))
                .andExpect(jsonPath("$.message", CoreMatchers.is(message)));
    }

    @Test
    @DisplayName("Test hard delete developer functionality")
    void givenId_whenHardDelete_thenSuccessResponse() throws Exception {
        //given
        BDDMockito.doNothing().when(developerService).hardDeleteById(anyInt());
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/developers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .param("isHard", "true"));
        //then
        verify(developerService, times(1)).hardDeleteById(anyInt());
        result
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test hard delete developer by incorrect id functionality")
    void givenIncorrectId_whenHardDelete_thenErrorResponse() throws Exception {
        //given
        String message = "Developer not found";
        BDDMockito.doThrow(new DeveloperNotFoundException(message))
                .when(developerService).hardDeleteById(anyInt());
        //when
        ResultActions result = mockMvc.perform(delete("/api/v1/developers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .param("isHard", "true"));
        //then
        verify(developerService, times(1)).hardDeleteById(anyInt());
        result
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", CoreMatchers.is(404)))
                .andExpect(jsonPath("$.message", CoreMatchers.is(message)));
    }
}
