package com.spartaclub.orderplatform.domain.user.application.mapper;

import com.spartaclub.orderplatform.domain.user.domain.entity.Address;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressCreateRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressCreateResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressDeleteResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressListPageResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressListResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressUpdateResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 주소 매퍼 인터페이스 Address 엔티티와 DTO 간의 변환을 담당
 *
 * @author 전우선
 * @date 2025-10-12(일)
 */
@Mapper(componentModel = "spring")
public interface AddressMapper {

    /**
     * 주소 생성 요청 DTO를 Address 엔티티로 변환 user는 서비스에서 별도 설정
     */
    @Mapping(target = "addressId", ignore = true)
    @Mapping(target = "user", ignore = true)
    Address toEntityFromCreateRequest(AddressCreateRequestDto requestDto);

    /**
     * Address 엔티티를 주소 생성 응답 DTO로 변환 fullAddress는 별도 계산 후 설정
     */
    @Mapping(target = "message", constant = "주소가 성공적으로 등록되었습니다.")
    @Mapping(target = "fullAddress", expression = "java(address.getRoadNameAddress() + \" \" + address.getDetailedAddress())")
    AddressCreateResponseDto toCreateResponse(Address address);

    /**
     * Address 엔티티를 주소 목록 응답 DTO로 변환 fullAddress는 별도 계산 후 설정
     */
    @Mapping(target = "fullAddress", expression = "java(address.getRoadNameAddress() + \" \" + address.getDetailedAddress())")
    AddressListResponseDto toListResponse(Address address);

    /**
     * 기본 주소 정보를 DefaultAddressInfo DTO로 변환
     */
    default AddressListPageResponseDto.DefaultAddressInfo toDefaultAddressInfo(
        Address defaultAddress) {
        if (defaultAddress == null) {
            return null;
        }
        return AddressListPageResponseDto.DefaultAddressInfo.builder()
            .addressId(defaultAddress.getAddressId())
            .addressName(defaultAddress.getAddressName())
            .build();
    }

    /**
     * 주소 목록 페이지 응답 DTO를 생성
     */
    default AddressListPageResponseDto toPageResponse(
        java.util.List<AddressListResponseDto> addressList,
        long totalCount,
        Address defaultAddress) {
        return AddressListPageResponseDto.builder()
            .addresses(addressList)
            .totalCount(totalCount)
            .defaultAddress(toDefaultAddressInfo(defaultAddress))
            .build();
    }

    /**
     * Address 엔티티를 주소 수정 응답 DTO로 변환
     */
    @Mapping(target = "message", constant = "주소가 성공적으로 수정되었습니다.")
    @Mapping(target = "fullAddress", expression = "java(address.getRoadNameAddress() + \" \" + address.getDetailedAddress())")
    AddressUpdateResponseDto toUpdateResponse(Address address);

    /**
     * Address 엔티티를 주소 삭제 응답 DTO로 변환
     */
    @Mapping(target = "message", constant = "주소가 성공적으로 삭제되었습니다.")
    @Mapping(target = "deletedAddressId", source = "addressId")
    @Mapping(target = "deletedAddressName", source = "addressName")
    AddressDeleteResponseDto toDeleteResponse(Address address);
}