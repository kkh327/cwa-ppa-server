package app.coronawarn.datadonation.services.ppac.ios.controller.validation;

import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildBase64String;
import static app.coronawarn.datadonation.services.ppac.ios.testdata.TestData.buildPPADataRequestIosPayload;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.coronawarn.datadonation.common.protocols.internal.ppdd.PPADataRequestIOS;
import app.coronawarn.datadonation.services.ppac.config.PpacConfiguration;
import app.coronawarn.datadonation.services.ppac.ios.client.IosDeviceApiClient;
import java.util.UUID;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PpaDataRequestIosPayloadValidatorTest {

  @Autowired
  private PpaDataRequestIosPayloadValidator underTest;

  @Autowired
  private PpacConfiguration configuration;

  @MockBean
  private ConstraintValidatorContext context;

  @MockBean
  private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

  @MockBean
  private IosDeviceApiClient iosDeviceApiClient;

  @BeforeEach
  public void setup() {
    context = mock(ConstraintValidatorContext.class);
    violationBuilder = mock(
        ConstraintValidatorContext.ConstraintViolationBuilder.class);
    when(context.buildConstraintViolationWithTemplate(any())).thenReturn(violationBuilder);
  }

  @Test
  public void testValidatePayloadValidationShouldBeSuccessful() {
    String base64String = buildBase64String(configuration.getIos().getMinDeviceTokenLength() + 1);

    PPADataRequestIOS payload = buildPPADataRequestIosPayload(UUID.randomUUID().toString(), base64String, true);

    assertThat(underTest.isValid(payload, context)).isTrue();
  }

  @Test
  public void testValidatePayloadValidationShouldFailInvalidDeviceTokenWrongMinLength() {
    String deviceToken = buildBase64String(configuration.getIos().getMinDeviceTokenLength() - 4);
    PPADataRequestIOS payload = buildPPADataRequestIosPayload(UUID.randomUUID().toString(), deviceToken, true);

    assertThat(underTest.isValid(payload, context)).isFalse();
  }

  @Test
  public void testValidatePayloadValidationShouldFailInvalidDeviceTokenWrongMaxLength() {
    String deviceToken = buildBase64String(configuration.getIos().getMaxDeviceTokenLength() + 1);
    PPADataRequestIOS payload = buildPPADataRequestIosPayload(UUID.randomUUID().toString(), deviceToken, true);

    assertThat(underTest.isValid(payload, context)).isFalse();
  }

  @Test
  public void testValidatePayloadValidationShouldFailInvalidDeviceTokenNoBase64() {
    final PPADataRequestIOS payload = buildPPADataRequestIosPayload(UUID.randomUUID().toString(), "notbase64", true);

    assertThat(underTest.isValid(payload, context)).isFalse();
  }

  @Test
  public void testValidatePayloadValidationShouldFailInvalidApiToken() {
    String base64String = buildBase64String(configuration.getIos().getMinDeviceTokenLength() + 1);
    PPADataRequestIOS payload = buildPPADataRequestIosPayload("apiToken_invalid", base64String, true);

    assertThat(underTest.isValid(payload, context)).isFalse();
  }


}
