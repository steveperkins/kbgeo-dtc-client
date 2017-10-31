package com.kbs.biz.mail.sparkpost;

public class EmailException extends Exception {

    public EmailException(Exception ex) {
        super(ex);
    }
}
