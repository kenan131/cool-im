package com.bin.model.user.vo.request.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: bin.jiang
 * @date: 2024/6/27 15:18
 **/
@Data
public class LoginReqDto implements Serializable {
    String userName;
    String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return password;
    }

    public void setPassWord(String passWord) {
        this.password = passWord;
    }
}
