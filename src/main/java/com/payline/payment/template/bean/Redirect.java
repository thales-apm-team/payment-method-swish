package com.payline.payment.template.bean;

import com.google.gson.annotations.SerializedName;
import com.payline.payment.template.utils.InvalidRequestException;
import com.payline.pmapi.bean.payment.Environment;

public class Redirect {
    @SerializedName("success_url")
    private String successUrl;
    @SerializedName("failure_url")
    private String failureUrl;
    @SerializedName("auth_url")
    private String authUrl;

    Redirect(Environment environment) throws InvalidRequestException {
        if (environment.getRedirectionReturnURL() == null) {
            throw new InvalidRequestException("TemplateRequest must have a success url when created");
        }
        if (environment.getRedirectionCancelURL() == null) {
            throw new InvalidRequestException("TemplateRequest must have a failure url when created");
        }
        this.successUrl = environment.getRedirectionReturnURL();
        this.failureUrl = environment.getRedirectionCancelURL();
    }

    public String getAuthUrl() {
        return authUrl;
    }
}
