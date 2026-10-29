package io.kestra.plugin.email;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.Test;

import io.kestra.core.junit.annotations.KestraTest;
import io.kestra.core.runners.RunContextFactory;

import jakarta.inject.Inject;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@KestraTest
class MailServiceTest {
    @Inject
    private RunContextFactory runContextFactory;

    @Test
    void parsesTextPartWithUnknownCharset() throws Exception {
        String raw = "From: a@b.c\r\nTo: test@localhost\r\nSubject: bogus\r\nMIME-Version: 1.0\r\n"
            + "Content-Type: text/plain; charset=x-bogus\r\n\r\nhello\r\n";

        MimeMessage message = new MimeMessage(
            Session.getInstance(new Properties()),
            new ByteArrayInputStream(raw.getBytes(StandardCharsets.US_ASCII))
        );

        MailService.EmailData email = MailService.parseEmailData(
            message,
            runContextFactory.of(Map.of()),
            MailService.DEFAULT_MAX_ATTACHMENT_SIZE
        );

        assertThat(email.getSubject(), is("bogus"));
        assertThat(email.getBody(), is("hello\r\n"));
        assertThat(email.getAttachments().size(), is(0));
    }
}
