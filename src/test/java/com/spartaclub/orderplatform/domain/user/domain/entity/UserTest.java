package com.spartaclub.orderplatform.domain.user.domain.entity;

import com.spartaclub.orderplatform.user.domain.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Entity:User")
public class UserTest {

    @Test
    @DisplayName("JPA Entity의 Setter에 대한 학습 테스트")
    void setterTest() {
        // Given
        User user = new User();

        // Then
        Assertions.assertThat(user.getUserId()).isNotNull();
    }
}
