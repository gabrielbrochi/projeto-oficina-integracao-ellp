package com.ellp.oficinas.service;

import java.util.Map;

public interface JasperReportPort {
    byte[] gerar(Map<String, Object> parametros) throws Exception;
}
