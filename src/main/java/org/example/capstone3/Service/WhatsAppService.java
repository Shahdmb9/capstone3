package org.example.capstone3.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class WhatsAppService {
    @Value("${twilio.account.sid}")
    private String accountSid ;
    @Value("${twilio.auth.token}")
    private String authToken ;
    @Value("${twilio.whatsapp.from}")
    private String from ;
    public void sendReportByWhatsApp(String toPhone , String name ){
        Twilio.init(accountSid,authToken);
        String formattedPhone = toPhone;
        if (toPhone.startsWith("05")) {
            formattedPhone = "+966" + toPhone.substring(1); // 05XX → +9665XX
        } else if (toPhone.startsWith("5")) {
            formattedPhone = "+966" + toPhone; // 5XX → +9665XX
        }
        Message.creator(
                new PhoneNumber("WhatsApp:" + formattedPhone),
                new PhoneNumber(from),
                name  +" your Children Performance Report has been generated! \nCheck your mail"
        ).create();
    }
    public void whatsAppMessage(String toPhone , String body ){
        Twilio.init(accountSid,authToken);
        String formattedPhone = toPhone;
        if (toPhone.startsWith("05")) {
            formattedPhone = "+966" + toPhone.substring(1); // 05XX → +9665XX
        } else if (toPhone.startsWith("5")) {
            formattedPhone = "+966" + toPhone; // 5XX → +9665XX
        }
        Message.creator(
                new PhoneNumber("WhatsApp:" + formattedPhone),
                new PhoneNumber(from),
                body
        ).create();
    }
}
