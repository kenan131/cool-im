package com.bin.user.domain.vo.request.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @author zhongzb create on 2021/05/31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdReqVO {
    @ApiModelProperty("id")
    @NotNull
    private long id;
}
