package com.payline.payment.template.test.bean;

import com.payline.payment.template.bean.TemplatePaymentRequest;
import com.payline.payment.template.test.Utils;
import com.payline.payment.template.utils.BadFieldException;
import com.payline.payment.template.utils.InvalidRequestException;
import com.payline.payment.template.utils.TemplateCardConstants;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.Environment;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Currency;

public class PaySafeRequestTest {

    @Test
    public void createPaySafeRequestFromContractParam() throws InvalidRequestException {
        ContractParametersCheckRequest checkRequest = Utils.createContractParametersCheckRequest(null, null, null, Utils.AUTHORISATION_VAL);
        TemplatePaymentRequest request = new TemplatePaymentRequest(checkRequest);
        Assert.assertNotNull(request);
    }

    @Test
    public void createPaySafeRequestFromPaymentRequest() throws InvalidRequestException {
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().build();
        TemplatePaymentRequest request = new TemplatePaymentRequest(paymentRequest);
        Assert.assertNotNull(request);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithoutAmount() throws InvalidRequestException {
        Amount amount = new Amount(null, Currency.getInstance("EUR"));
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withAmount(amount).build();
        new TemplatePaymentRequest(paymentRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithoutCurrency() throws InvalidRequestException {
        Amount amount = new Amount(BigInteger.ONE, null);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withAmount(amount).build();
        new TemplatePaymentRequest(paymentRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithNullSuccessUrl() throws InvalidRequestException {
        Environment environment = new Environment(Utils.NOTIFICATION_URL, null, Utils.FAILURE_URL, true);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withEnvironment(environment).build();
        new TemplatePaymentRequest(paymentRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithNullFailureUrl() throws InvalidRequestException {
        Environment environment = new Environment(Utils.NOTIFICATION_URL, Utils.SUCCESS_URL, null, true);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withEnvironment(environment).build();
        new TemplatePaymentRequest(paymentRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithoutAuthorisationKey() throws InvalidRequestException {
        ContractConfiguration configuration = Utils.createContractConfiguration(null, null, null, null);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withContractConfiguration(configuration).build();
        new TemplatePaymentRequest(paymentRequest);
    }

    @Test
    public void createPaySafeRequestWithWrongCountryCode() throws InvalidRequestException {
        ContractConfiguration configuration = Utils.createContractConfiguration(null, null, "foo", Utils.AUTHORISATION_VAL);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withContractConfiguration(configuration).build();
        try {
            new TemplatePaymentRequest(paymentRequest);
        }catch (BadFieldException e){
            Assert.assertEquals( TemplateCardConstants.COUNTRYRESTRICTION_KEY, e.getField());
        }
    }

    @Test
    public void createPaySafeRequestWithBadMinAge() throws InvalidRequestException {
        ContractConfiguration configuration = Utils.createContractConfiguration(null, "a", null, Utils.AUTHORISATION_VAL);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withContractConfiguration(configuration).build();
        try {
            new TemplatePaymentRequest(paymentRequest);
        } catch (BadFieldException e) {
            Assert.assertEquals(  TemplateCardConstants.MINAGE_KEY, e.getField());
        }
    }

    @Test
    public void createPaySafeRequestWithOutOfRangeMinAge() throws InvalidRequestException {
        ContractConfiguration configuration = Utils.createContractConfiguration(null, "10000", null, Utils.AUTHORISATION_VAL);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withContractConfiguration(configuration).build();
        try {
            new TemplatePaymentRequest(paymentRequest);
        } catch (BadFieldException e) {
            Assert.assertEquals(  TemplateCardConstants.MINAGE_KEY, e.getField());
        }
    }

    @Test
    public void createAmount() {
        Assert.assertEquals("0.00", TemplatePaymentRequest.createAmount(0));
        Assert.assertEquals("0.01", TemplatePaymentRequest.createAmount(1));
        Assert.assertEquals("1.00", TemplatePaymentRequest.createAmount(100));
        Assert.assertEquals("10.00", TemplatePaymentRequest.createAmount(1000));
        Assert.assertEquals("100.00", TemplatePaymentRequest.createAmount(10000));
    }

    @Test
    public void encode() {
        String s = "hello world";
        String s2 = "aGVsbG8gd29ybGQ=";
        Assert.assertEquals(s2, TemplatePaymentRequest.encodeToBase64(s));
        Assert.assertEquals("", TemplatePaymentRequest.encodeToBase64(""));
        Assert.assertEquals("", TemplatePaymentRequest.encodeToBase64(null));
    }
}
