package com.alessandro.napoletano.springbootoauth2demov2.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Entity
@Getter
@Setter
public class VerificationToken {
    //VerificationToken will last one day
    private static final int EXPIRATION = 60*24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String emailToken;

    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    private Date expiryDate;

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public VerificationToken(){
        super();
    }

    public VerificationToken(String emailToken, User user) {
        super();

        this.emailToken = emailToken;
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }
}
