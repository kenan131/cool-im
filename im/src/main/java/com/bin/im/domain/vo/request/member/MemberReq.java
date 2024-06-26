package com.bin.im.domain.vo.request.member;

import com.bin.im.domain.vo.request.common.CursorPageBaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-07-17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberReq extends CursorPageBaseReq {
    @ApiModelProperty("房间号")
    private Long roomId = 1L;
}
