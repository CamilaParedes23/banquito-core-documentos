# Pruebas manuales - Document Service

Obtener token desde Identity:

```powershell
$loginBody = @{
  username = "admin.core"
  password = "password"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8081/api/v1/auth/login" `
  -ContentType "application/json" `
  -Body $loginBody

$token = $loginResponse.accessToken
```

Listar tipos documentales:

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8086/api/v1/documents/types" `
  -Headers @{ Authorization = "Bearer $token" }
```

Registrar documento:

```powershell
$body = @{
  businessContext = "CORE_ACCOUNT"
  documentType = "ACCOUNT_TRANSACTION_RECEIPT"
  businessReferenceUuid = "a9ef935f-2073-4195-9838-fe9ee9df24a5"
  fileName = "comprobante-deposito-demo.txt"
  mimeType = "text/plain"
  storagePath = "/receipts/account/comprobante-deposito-demo.txt"
  hashSha256 = "demo-hash"
  textPayload = "Comprobante de deposito de prueba"
  createdBy = "admin.core"
  correlationId = "33333333-3333-3333-3333-333333333333"
  metadata = @{ accountNumber = "0010515383395"; amount = 100.00 }
} | ConvertTo-Json -Depth 5

$document = Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8086/api/v1/documents" `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $token" } `
  -Body $body

$document
```

Consultar metadata:

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8086/api/v1/documents/$($document.documentUuid)" `
  -Headers @{ Authorization = "Bearer $token" }
```

Descargar payload:

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8086/api/v1/documents/$($document.documentUuid)/download" `
  -Headers @{ Authorization = "Bearer $token" }
```
