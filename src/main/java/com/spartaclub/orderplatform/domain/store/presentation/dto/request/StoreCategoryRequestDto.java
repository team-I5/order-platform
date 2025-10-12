package com.spartaclub.orderplatform.domain.store.presentation.dto.request;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreCategoryRequestDto {

    private List<UUID> categoryIds;
}
