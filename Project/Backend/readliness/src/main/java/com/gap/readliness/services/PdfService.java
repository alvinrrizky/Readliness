package com.gap.readliness.services;

import com.gap.readliness.dto.DataTemplatePdf;
import com.gap.readliness.model.Customer;
import com.gap.readliness.model.Item;
import com.gap.readliness.model.Order;
import com.gap.readliness.repository.CustomerRepository;
import com.gap.readliness.repository.ItemRepository;
import com.gap.readliness.repository.OrderRepository;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PdfService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    public byte[] generatePdf() throws JRException, IOException {
        List<Order> dataList = orderRepository.findAll(Sort.by(Sort.Direction.ASC, "orderDate"));
        List<DataTemplatePdf> dataTemplatePdfList = new ArrayList<>();

        for (Order order : dataList) {
            Customer dataCustomer = customerRepository.findByCustomerId(order.getCustomerId());
            Item dataItem = itemRepository.findByItemsId(order.getItemsId());

            DataTemplatePdf templatePdf = DataTemplatePdf.builder()
                    .orderCode(order.getOrderCode())
                    .orderDate(order.getOrderDate())
                    .totalPrice(order.getTotalPrice())
                    .quantity(order.getQuantity())
                    .customerName(dataCustomer.getCustomerName())
                    .itemsName(dataItem.getItemsName())
                    .build();

            dataTemplatePdfList.add(templatePdf);
        }

        InputStream reportStream = new ClassPathResource("orders_report.jrxml").getInputStream();

        // Compile file .jrxml menjadi JasperReport
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataTemplatePdfList);

        Map<String, Object> parameters = new HashMap<>();

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
        exporter.exportReport();

        return byteArrayOutputStream.toByteArray();
    }
}
