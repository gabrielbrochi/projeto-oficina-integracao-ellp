package com.ellp.oficinas.service;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JREmptyDataSource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

@Component
public class JasperReportAdapter implements JasperReportPort {

    @Override
    public byte[] gerar(Map<String, Object> parametros) throws Exception {
        InputStream template = getClass().getResourceAsStream("/certificado.jrxml");
        if (template == null) {
            throw new IllegalStateException("Template certificado.jrxml não encontrado em src/main/resources");
        }
        JasperReport jasperReport = JasperCompileManager.compileReport(template);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, new JREmptyDataSource());
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
