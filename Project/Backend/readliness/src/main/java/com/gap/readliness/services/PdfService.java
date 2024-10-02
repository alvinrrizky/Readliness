package com.gap.readliness.services;

import com.gap.readliness.model.Customer;
import com.gap.readliness.model.Item;
import com.gap.readliness.model.Order;
import com.gap.readliness.repository.CustomerRepository;
import com.gap.readliness.repository.ItemRepository;
import com.gap.readliness.repository.OrderRepository;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    public byte[] generatePdf() {
        // Mengambil semua data dan mengurutkannya berdasarkan orderDate secara ASC
        List<Order> dataList = orderRepository.findAll(Sort.by(Sort.Direction.ASC, "orderDate"));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("List Order Available :"));

            // Definisikan tabel dengan jumlah kolom yang diinginkan
            float[] pointColumnWidths = {100F, 100F, 100F, 100F, 100F, 100F};
            Table table = new Table(pointColumnWidths);

            // Tambahkan header untuk tabel
            table.addHeaderCell(new Cell().add(new Paragraph("Order Code")));
            table.addHeaderCell(new Cell().add(new Paragraph("Order Date")));
            table.addHeaderCell(new Cell().add(new Paragraph("Total Price")));
            table.addHeaderCell(new Cell().add(new Paragraph("Jumlah Item")));
            table.addHeaderCell(new Cell().add(new Paragraph("Nama Customer")));
            table.addHeaderCell(new Cell().add(new Paragraph("Nama Item")));

            // Iterasi data dan masukkan ke dalam tabel
            for (Order data : dataList) {
                Customer dataCustomer = customerRepository.findByCustomerId(data.getCustomerId());
                Item dataItem = itemRepository.findByItemsId(data.getItemsId());

                table.addCell(new Cell().add(new Paragraph(data.getOrderCode())));
                table.addCell(new Cell().add(new Paragraph(data.getOrderDate().toString())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(data.getTotalPrice()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(data.getQuantity()))));
                table.addCell(new Cell().add(new Paragraph(dataCustomer.getCustomerName())));
                table.addCell(new Cell().add(new Paragraph(dataItem.getItemsName())));
            }

            // Tambahkan tabel ke dalam dokumen
            document.add(table);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray();
    }
}
