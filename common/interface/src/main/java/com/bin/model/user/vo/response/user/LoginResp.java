package com.bin.model.user.vo.response.user;

import com.bin.model.common.exception.WSBaseResp;

import java.io.Serializable;

/**
 * @author: bin.jiang
 * @date: 2024/6/27 15:26
 **/
public class LoginResp implements Serializable {
    private Long uid;
    private boolean type;
    private String errorMsg;

    private WSBaseResp wsBaseResp;

    public boolean getType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public WSBaseResp getWsBaseResp() {
        return wsBaseResp;
    }

    public void setWsBaseResp(WSBaseResp wsBaseResp) {
        this.wsBaseResp = wsBaseResp;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    static public LoginResp buildFailResp(String errorMsg){
        LoginResp resp = new LoginResp();
        resp.setType(false);
        resp.setErrorMsg(errorMsg);
        return resp;
    }
    static public LoginResp buildSuccessResp(Long uid,WSBaseResp wsBaseResp){
        LoginResp resp = new LoginResp();
        resp.setType(true);
        resp.setWsBaseResp(wsBaseResp);
        resp.setUid(uid);
        return resp;
    }

    @Override
    public String toString() {
        return "LoginResp{" +
                "type=" + type +
                ", errorMsg='" + errorMsg + '\'' +
                ", wsBaseResp=" + wsBaseResp +
                '}';
    }
}
