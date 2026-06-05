// ============================================================================
// BANCO BANQUITO V2 - DOCUMENT SERVICE
// Modelo documental MongoDB
// Base: banquito_document_db
// Propósito: documentos operativos, reportes, comprobantes, archivos CSV/TXT,
// versiones, payloads y evidencias. No reemplaza las bases relacionales.
// ============================================================================

use('banquito_document_db');

const collections = db.getCollectionNames();

if (!collections.includes('documents')) {
  db.createCollection('documents', {
    validator: {
      $jsonSchema: {
        bsonType: 'object',
        required: ['documentUuid', 'documentType', 'businessContext', 'status', 'createdAt', 'correlationId'],
        properties: {
          documentUuid: { bsonType: 'string' },
          documentType: { bsonType: 'string' },
          businessContext: { bsonType: 'string' },
          businessReferenceUuid: { bsonType: ['string', 'null'] },
          ownerService: { bsonType: ['string', 'null'] },
          fileName: { bsonType: ['string','null'] },
          mimeType: { bsonType: ['string','null'] },
          storagePath: { bsonType: ['string','null'] },
          hashSha256: { bsonType: ['string','null'] },
          sizeBytes: { bsonType: ['long','int','null'] },
          status: { enum: ['CREATED','STORED','SIGNED','ARCHIVED','DELETED','FAILED'] },
          correlationId: { bsonType: 'string' },
          createdBy: { bsonType: ['string','null'] },
          createdAt: { bsonType: 'date' },
          metadata: { bsonType: ['object','null'] }
        }
      }
    }
  });
}

if (!collections.includes('document_versions')) db.createCollection('document_versions');
if (!collections.includes('document_events')) db.createCollection('document_events');
if (!collections.includes('document_payloads')) db.createCollection('document_payloads');
if (!collections.includes('document_type_catalog')) db.createCollection('document_type_catalog');

db.documents.createIndex({ documentUuid: 1 }, { unique: true });
db.documents.createIndex({ businessContext: 1, documentType: 1, createdAt: -1 });
db.documents.createIndex({ businessReferenceUuid: 1, createdAt: -1 });
db.documents.createIndex({ correlationId: 1 });
db.documents.createIndex({ hashSha256: 1 });

db.document_versions.createIndex({ versionUuid: 1 }, { unique: true });
db.document_versions.createIndex({ documentUuid: 1, versionNumber: 1 }, { unique: true });
db.document_events.createIndex({ eventUuid: 1 }, { unique: true });
db.document_events.createIndex({ documentUuid: 1, createdAt: -1 });
db.document_payloads.createIndex({ documentUuid: 1 }, { unique: true });
db.document_type_catalog.createIndex({ code: 1 }, { unique: true });

const types = [
  { code: 'PAYMENT_INPUT_FILE', name: 'Archivo de entrada de pagos masivos', description: 'Archivo cargado por portal o SFTP en Switch', ownerService: 'SWITCH', status: 'ACTIVO' },
  { code: 'PAYMENT_NOVELTY_REPORT', name: 'Reporte de novedades por lote', description: 'Reporte de errores o rechazos de pago masivo', ownerService: 'SWITCH', status: 'ACTIVO' },
  { code: 'CORPORATE_SETTLEMENT_RECEIPT', name: 'Comprobante de liquidación corporativa', description: 'Comprobante consolidado de lote empresarial', ownerService: 'SWITCH', status: 'ACTIVO' },
  { code: 'ACCOUNT_TRANSACTION_RECEIPT', name: 'Comprobante de transacción de cuenta', description: 'Comprobante de débito, crédito, retiro, depósito o transferencia', ownerService: 'CORE_ACCOUNT', status: 'ACTIVO' },
  { code: 'EOD_TRIAL_BALANCE_CSV', name: 'Balance de comprobación CSV', description: 'Reporte contable generado en cierre EOD', ownerService: 'CORE_ACCOUNTING', status: 'ACTIVO' },
  { code: 'EOD_EVIDENCE', name: 'Evidencia de cierre EOD', description: 'Evidencia documental de proceso de cierre diario', ownerService: 'CORE_ACCOUNTING', status: 'ACTIVO' },
  { code: 'SECURITY_AUDIT_EVIDENCE', name: 'Evidencia de auditoría de seguridad', description: 'Evidencia relacionada a eventos IAM', ownerService: 'IDENTITY', status: 'ACTIVO' },
  { code: 'NOTIFICATION_EVIDENCE', name: 'Evidencia de notificación enviada', description: 'Evidencia de correo, SMS o push emitido', ownerService: 'NOTIFICATION', status: 'ACTIVO' }
];

types.forEach(t => {
  db.document_type_catalog.updateOne(
    { code: t.code },
    { $set: t },
    { upsert: true }
  );
});

print('banquito_document_db inicializada/actualizada correctamente.');
