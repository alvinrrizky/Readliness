package com.gap.readliness.controllers;

import com.gap.readliness.services.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf() {

        byte[] pdfBytes = pdfService.generatePdf();

        // Menyiapkan header untuk mendownload file PDF
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "data-list.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
