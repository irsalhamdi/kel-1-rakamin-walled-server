package com.odp.walled.service;

import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;

@Service
public class HtmlToPdfService {

    public byte[] generatePdfFromHtml(String htmlContent) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            renderer.finishPDF();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Gagal membuat PDF dari HTML", e);
        }
    }

    public String buildHtml(
            String date,
            String amount,
            String recipientName,
            String recipientAccount,
            String senderName,
            String senderAccount,
            String transactionId,
            String notes,
            String total) {
        return String.format(
                """
                        <html xmlns="http://www.w3.org/1999/xhtml">
                        <head>
                            <meta charset="UTF-8"/>
                            <style>
                                body { font-family: Arial, sans-serif; max-width: 500px; margin: auto; }
                                .container { border-radius: 10px; overflow: hidden; }
                                .section { background-color: #E0ECFF; padding: 15px; margin-bottom: 10px; }
                                .info { background-color: #ffffff; padding: 15px; border: 1px solid #eee; }
                                .row { margin-bottom: 12px; }
                                .label { font-weight: bold; }
                                hr { margin: 15px 0; }
                                .right { float: right; }
                            </style>
                        </head>
                        <body>
                            <div class="container">
                                <div class="row">
                                    <img src="https://github.com/irsalhamdi/kel-1-rakamin-walled-server/blob/main/walled.png?raw=true"
                                         width="100" alt="logo" />
                                </div>
                                <h2>Transaction Successful!</h2>
                                <p style="color: #888; font-size: 14px;">%s</p>
                                <div class="section">
                                    <div class="row">
                                        <span class="label">Amount</span>
                                        <span class="right">Rp %s</span>
                                    </div>
                                </div>
                                <div class="info">
                                    <div class="row">
                                        <span class="label">Recipient</span>
                                        <span class="right">%s (%s)</span>
                                    </div>
                                    <div class="row">
                                        <span class="label">Sender</span>
                                        <span class="right">%s (%s)</span>
                                    </div>
                                    <div class="row">
                                        <span class="label">Transaction ID</span>
                                        <span class="right">%s</span>
                                    </div>
                                    <div class="row">
                                        <span class="label">Notes</span>
                                        <span class="right">%s</span>
                                    </div>
                                    <div class="row">
                                        <span class="label">Admin Fee</span>
                                        <span class="right">Rp 0</span>
                                    </div>
                                    <hr/>
                                    <div class="row">
                                        <span class="label">Total</span>
                                        <span class="right">Rp %s</span>
                                    </div>
                                </div>
                            </div>
                        </body>
                        </html>
                        """,
                date, amount, recipientName, recipientAccount,
                senderName, senderAccount, transactionId, notes, total);
    }
}
