package com.lyc.learn.secKill.vo;

import com.lyc.learn.common.dto.SecKillProductVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecKillResultVo {
    private int code;
    private String msg;
    private SecKillProductVo productInfo;
}
