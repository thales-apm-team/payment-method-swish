package com.payline.payment.template.services;

import com.payline.payment.template.utils.LocalizationImpl;
import com.payline.payment.template.utils.LocalizationService;
import com.payline.pmapi.bean.paymentform.bean.PaymentFormLogo;
import com.payline.pmapi.bean.paymentform.bean.form.NoFieldForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import com.payline.pmapi.bean.paymentform.response.logo.PaymentFormLogoResponse;
import com.payline.pmapi.bean.paymentform.response.logo.impl.PaymentFormLogoResponseFile;
import com.payline.pmapi.service.PaymentFormConfigurationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class PaymentFormConfigurationServiceImpl implements PaymentFormConfigurationService {
    private static final String LOGO_CONTENT_TYPE = "image/png";
    private static final int LOGO_HEIGHT = 25;
    private static final int LOGO_WIDTH = 141;
    private static final boolean DISPLAY_PAYMENT_BUTTON = true;

    private static final Logger LOGGER = LogManager.getLogger(PaymentFormConfigurationServiceImpl.class);

    private LocalizationService localization;

    public PaymentFormConfigurationServiceImpl() {
        localization = LocalizationImpl.getInstance();
    }

    /**
     * Build a new PaymentFormConfigurationResponse
     *
     * @param paymentFormConfigurationRequest
     * @return
     */
    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {
        NoFieldForm noFieldForm = NoFieldForm.NoFieldFormBuilder.aNoFieldForm()
                .withDisplayButton(DISPLAY_PAYMENT_BUTTON)
                .withButtonText(localization.getSafeLocalizedString("form.button.template.text", paymentFormConfigurationRequest.getLocale()))
                .withDescription(localization.getSafeLocalizedString("form.button.template.description", paymentFormConfigurationRequest.getLocale()))
                .build();

        return PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder.aPaymentFormConfigurationResponseSpecific()
                .withPaymentForm(noFieldForm)
                .build();
    }

    @Override
    public PaymentFormLogoResponse getPaymentFormLogo(PaymentFormLogoRequest paymentFormLogoRequest) {
        return PaymentFormLogoResponseFile.PaymentFormLogoResponseFileBuilder.aPaymentFormLogoResponseFile()
                .withHeight(LOGO_HEIGHT)
                .withWidth(LOGO_WIDTH)
                .withTitle(localization.getSafeLocalizedString("project.name", paymentFormLogoRequest.getLocale()))
                .withAlt(localization.getSafeLocalizedString("project.name", paymentFormLogoRequest.getLocale()))
                .build();
    }

    @Override
    public PaymentFormLogo getLogo(String paymentMethodIdentifier, Locale locale) {
        try {
            // Read logo file
            InputStream input = PaymentFormConfigurationServiceImpl.class.getClassLoader().getResourceAsStream("template.png");
            BufferedImage logo = ImageIO.read(input);

            // Recover byte array from image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(logo, "png", baos);

            return PaymentFormLogo.PaymentFormLogoBuilder.aPaymentFormLogo()
                    .withFile(baos.toByteArray())
                    .withContentType(LOGO_CONTENT_TYPE)
                    .build();

        } catch (IOException e) {
            LOGGER.error("unable to load the logo: {}", e.getMessage(), e);
            throw new RuntimeException("Unable to load logo");
        }
    }
}
