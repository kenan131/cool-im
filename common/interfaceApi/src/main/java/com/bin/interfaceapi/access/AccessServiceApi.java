package com.bin.interfaceapi.access;

import com.bin.interfaceapi.access.dto.PushMessageDTO;

import java.util.List;

/**
 * @author: bin.jiang
 * @date: 2024/6/24 21:06
 **/

public interface AccessServiceApi {
    public boolean pushMessage(List<PushMessageDTO> data);
}
