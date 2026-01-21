package com.ddmtchr.dbarefactor.dto;

import com.ddmtchr.dbarefactor.dto.producer.CheckDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckMessageBatch implements Serializable {
    List<CheckDto> messages;
}
