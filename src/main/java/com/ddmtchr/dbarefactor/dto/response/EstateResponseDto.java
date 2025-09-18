package com.ddmtchr.dbarefactor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstateResponseDto {
    private Long id;

    private String name;

    private String description;

    private Long price;

    private Long ownerId;
}
