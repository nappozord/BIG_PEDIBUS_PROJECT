package com.alessandro.napoletano.springbootoauth2demov2.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.sql.Timestamp;
import java.util.Calendar;

@Entity
@Getter
@Setter
public class PasswordResetToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    private String password;

    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    private Date expiryDate;

    private java.util.Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new java.util.Date(cal.getTime().getTime());
    }

    public PasswordResetToken(){
        super();
    }

    public PasswordResetToken(String token, User user, String password) {
        super();

        this.token = token;
        this.user = user;
        this.password = password;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }
}
