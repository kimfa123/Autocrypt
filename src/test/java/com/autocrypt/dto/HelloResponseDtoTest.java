package com.autocrypt.dto;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloResponseDtoTest {

    @Test
    public void 롬복_기능_테스트() {
        //given
        String name = "jihoon";
        String nickname = "babo";

        //when
        HelloResponseDto helloResponseDto = new HelloResponseDto(name, nickname); //필드가 포함된 생성자를 생성

        //then
        assertThat(helloResponseDto.getName()).isEqualTo(name); //get 메소드를 선언하지 않아도 getName()을 사용할 수 있습니다.
        assertThat(helloResponseDto.getNickname()).isEqualTo(nickname);
    }
}
