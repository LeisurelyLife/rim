package cn.rt.common.entity;

import javax.persistence.*;

@Table(name = "useraccount")
public class Useraccount {
    @Column(name = "UserAccount")
    private String useraccount;

    @Column(name = "UserId")
    private String userid;

    @Column(name = "Password")
    private String password;

    /**
     * @return UserAccount
     */
    public String getUseraccount() {
        return useraccount;
    }

    /**
     * @param useraccount
     */
    public void setUseraccount(String useraccount) {
        this.useraccount = useraccount;
    }

    /**
     * @return UserId
     */
    public String getUserid() {
        return userid;
    }

    /**
     * @param userid
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * @return Password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}