package com.gap.readliness.controllers;

import com.gap.readliness.services.PdfService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @GetMapping("/downloadpdf")
    public ResponseEntity<byte[]> downloadPdf() throws JRException, IOException {

        // Call service to generate PDF
        byte[] pdfBytes = pdfService.generatePdf();

        // Set response header to download file
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "orders_report.pdf");
        headers.setContentLength(pdfBytes.length); // Specify the content length

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
