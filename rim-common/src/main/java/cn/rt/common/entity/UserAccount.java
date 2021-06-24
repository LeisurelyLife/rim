package cn.rt.common.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "user_account")
@Getter
@Setter
public class UserAccount {
    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_account")
    private String userAccount;

    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;
}