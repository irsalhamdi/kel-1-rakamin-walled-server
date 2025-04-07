package com.odp.walled.service;

import com.odp.walled.model.Transaction;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendTransactionSuccessEmail(String toEmail, Transaction transaction) {
        String html = """

                <div style="font-family: Arial, sans-serif; max-width: 500px; margin: auto; border-radius: 10px; overflow: hidden;">
                    <div style="display: flex; align-items: center; margin-bottom: 10px;">
                        <img src="https://github.com/irsalhamdi/kel-1-rakamin-walled-server/blob/main/walled.png?raw=true" width="100" style="margin-right: 10px;" />
                    </div>

                    <h2 style="margin-top: 0; color: #000;">Transaction Successful!</h2>
                    <p style="color: #888; font-size: 14px; margin-top: -10px;">30 Jun 2022 - 20:10</p>
                                    <div style="background-color: #E0ECFF; padding: 15px;">
                                        <div style="display: flex; justify-content: space-between;">
                                            <strong>Amount</strong>
                                            <strong>Rp %s</strong>
                                        </div>
                                    </div>

                                    <div style="background-color: #ffffff; padding: 15px; border: 1px solid #eee;">
                                        <div style="margin-bottom: 12px; display: flex; justify-content: space-between;">
                                            <strong>Recipient</strong>
                                            <div style="text-align: right;">
                                                <div>%s</div>
                                                <div style="font-size: 12px; color: #666;">%s</div>
                                            </div>
                                        </div>

                                        <div style="margin-bottom: 12px; display: flex; justify-content: space-between;">
                                            <strong>Sender</strong>
                                            <div style="text-align: right;">
                                                <div>%s</div>
                                                <div style="font-size: 12px; color: #666;">%s</div>
                                            </div>
                                        </div>

                                        <div style="margin-bottom: 12px; display: flex; justify-content: space-between;">
                                            <strong>Transaction Id</strong>
                                            <span>%s</span>
                                        </div>

                                        <div style="margin-bottom: 12px; display: flex; justify-content: space-between;">
                                            <strong>Notes</strong>
                                            <span>%s</span>
                                        </div>

                                        <div style="margin-bottom: 12px; display: flex; justify-content: space-between;">
                                            <strong>Admin Fee</strong>
                                            <span>Rp0</span>
                                        </div>

                                        <hr style="margin: 15px 0;">

                                        <div style="display: flex; justify-content: space-between;">
                                            <strong>Total</strong>
                                            <strong>Rp %s</strong>
                                        </div>
                                    </div>
                                </div>
                                """
                .formatted(
                        transaction.getAmount().toPlainString(),
                        transaction.getRecipientWallet() != null ? transaction.getWallet().getUser().getFullname()
                                : "-",
                        transaction.getRecipientWallet() != null ? transaction.getRecipientWallet().getAccountNumber()
                                : "-",
                        transaction.getWallet().getUser().getFullname(),
                        transaction.getWallet().getAccountNumber(),
                        transaction.getId() != null ? transaction.getId() : "-",
                        transaction.getDescription(),
                        transaction.getAmount().toPlainString());

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("no-reply@walled.com");
            helper.setTo(toEmail);
            helper.setSubject("Transaction Notification");
            helper.setText(html, true);

            mailSender.send(mimeMessage);
            System.out.println("Transaction email sent to " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }
}
