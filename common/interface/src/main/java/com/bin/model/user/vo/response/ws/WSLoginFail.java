package com.bin.model.user.vo.response.ws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: bin.jiang
 * @date: 2024/6/27 16:29
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WSLoginFail implements Serializable {
    //登录失败的提示信息。
    private String errorMsg;
}
