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

    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf() throws JRException, IOException {

        // Panggil service untuk generate PDF
        byte[] pdfBytes = pdfService.generatePdf();

        // Set response header untuk mendownload file
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "orders_report.pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
