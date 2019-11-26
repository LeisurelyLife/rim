package cn.rt.common.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "useraccount")
public class Useraccount {
    @Id
    @Column(name = "userId")
    private String userid;

    @Column(name = "userAccount")
    private String useraccount;

    private String password;

    @Column(name = "userName")
    private String username;

    /**
     * @return userId
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
     * @return userAccount
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
     * @return password
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

    /**
     * @return userName
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }
}