package com.spartaclub.orderplatform.domain.user.presentation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartaclub.orderplatform.domain.user.application.service.AddressService;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressCreateRequestDto;
import com.spartaclub.orderplatform.global.auth.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * AddressController 간단한 테스트 - 인증이 필요없는 기본적인 API 검증만 수행
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AddressControllerSimpleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AddressService addressService;

    @MockBean
    private JwtUtil jwtUtil;

    private AddressCreateRequestDto createRequestDto;

    @BeforeEach
    void setUp() {
        createRequestDto = new AddressCreateRequestDto();
        createRequestDto.setAddressName("집");
        createRequestDto.setName("홍길동");
        createRequestDto.setPhoneNumber("01012345678");
        createRequestDto.setPostCode("12345");
        createRequestDto.setRoadNameAddress("서울시 강남구");
        createRequestDto.setDetailedAddress("456호");
        createRequestDto.setDefaultAddress(true);
    }

    @Test
    @DisplayName("주소 등록 API - 인증 없이 요청시 401 반환")
    void createAddress_unauthorized() throws Exception {
        // when & then
        mockMvc.perform(post("/v1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequestDto)))
            .andDo(print())
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("AddressController가 올바르게 로드되는지 확인")
    void contextLoads() {
        // AddressController가 Spring 컨텍스트에서 제대로 로드되는지만 확인
    }
}