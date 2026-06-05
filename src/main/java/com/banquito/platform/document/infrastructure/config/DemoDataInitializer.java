package com.banquito.platform.document.infrastructure.config;

import com.banquito.platform.document.domain.enums.EstadoTipoDocumentoEnum;
import com.banquito.platform.document.domain.model.TipoDocumentoCatalogo;
import com.banquito.platform.document.domain.repository.TipoDocumentoCatalogoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoDataInitializer implements CommandLineRunner {
    private final TipoDocumentoCatalogoRepository repository;
    private final boolean enabled;

    public DemoDataInitializer(TipoDocumentoCatalogoRepository repository,
                               @Value("${banquito.demo.enabled:true}") boolean enabled) {
        this.repository = repository;
        this.enabled = enabled;
    }

    @Override
    public void run(String... args) {
        if (!enabled) return;
        seed("PAYMENT_INPUT_FILE", "Archivo de entrada de pagos masivos", "Archivo cargado por portal o SFTP en Switch", "SWITCH");
        seed("PAYMENT_NOVELTY_REPORT", "Reporte de novedades por lote", "Reporte de errores o rechazos de pago masivo", "SWITCH");
        seed("CORPORATE_SETTLEMENT_RECEIPT", "Comprobante de liquidación corporativa", "Comprobante consolidado de lote empresarial", "SWITCH");
        seed("ACCOUNT_TRANSACTION_RECEIPT", "Comprobante de transacción de cuenta", "Comprobante de débito, crédito, retiro, depósito o transferencia", "CORE_ACCOUNT");
        seed("EOD_TRIAL_BALANCE_CSV", "Balance de comprobación CSV", "Reporte contable generado en cierre EOD", "CORE_ACCOUNTING");
        seed("EOD_EVIDENCE", "Evidencia de cierre EOD", "Evidencia documental de proceso de cierre diario", "CORE_ACCOUNTING");
        seed("SECURITY_AUDIT_EVIDENCE", "Evidencia de auditoría de seguridad", "Evidencia relacionada a eventos IAM", "IDENTITY");
        seed("NOTIFICATION_EVIDENCE", "Evidencia de notificación enviada", "Evidencia de correo, SMS o push emitido", "NOTIFICATION");
    }

    private void seed(String code, String name, String description, String ownerService) {
        repository.findByCode(code).ifPresentOrElse(existing -> {
            boolean changed = false;
            if (existing.getName() == null || existing.getName().isBlank()) {
                existing.setName(name);
                changed = true;
            }
            if (existing.getDescription() == null || existing.getDescription().isBlank()) {
                existing.setDescription(description);
                changed = true;
            }
            if (existing.getOwnerService() == null || existing.getOwnerService().isBlank()) {
                existing.setOwnerService(ownerService);
                changed = true;
            }
            if (existing.getStatus() == null || existing.getStatus() != EstadoTipoDocumentoEnum.ACTIVO) {
                existing.setStatus(EstadoTipoDocumentoEnum.ACTIVO);
                changed = true;
            }
            if (changed) {
                repository.save(existing);
            }
        }, () -> repository.save(TipoDocumentoCatalogo.crear(code, name, description, ownerService)));
    }
}
