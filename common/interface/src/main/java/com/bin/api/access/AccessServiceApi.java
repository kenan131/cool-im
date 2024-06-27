package com.bin.api.access;

import com.bin.model.user.dto.PushMessageDTO;

import java.util.List;

/**
 * @author: bin.jiang
 * @date: 2024/6/24 21:06
 **/

public interface AccessServiceApi {
    public boolean pushMessage(List<PushMessageDTO> data);
}
