package com.spartaclub.orderplatform.domain.store.presentation.dto.request;

import com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@Getter
@Setter
@NoArgsConstructor
public class StoreSearchRequestDto {

    private Long ownerId;
    private StoreStatus status;

    private int page = 0;
    private int size = 10;
    private Sort.Direction sortDirection = Direction.DESC;

    public void validatePageSize() {
        if (size != 10 && size != 30 && size != 50) {
            this.size = 10;
        }
    }
}
