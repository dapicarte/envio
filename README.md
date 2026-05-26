# Microservicio de Envío

## server.port=8095

## Endpoints

### POST `/api/v1/envios`
Crea un nuevo envío asociado a un pedido.

**JSON de entrada:**
```json
{
    "idPedido": 1
}
```

**JSON de entrada con estado personalizado:**
```json
{
    "idPedido": 1,
    "estadoEnvio": "EN_CAMINO"
}
```

**Respuestas posibles:**
| Situación | Status | Mensaje |
|---|---|---|
| Envío creado | 200 OK | Objeto Envio |
| Pedido no encontrado | 404 NOT_FOUND | `Pedido no encontrado` |
| Error general | 409 CONFLICT | `Error al crear el envio` |

---

### GET `/api/v1/envios`
Lista todos los envíos registrados.

---

### GET `/api/v1/envios/{id}`
Obtiene un envío por su id.

---

### PUT `/api/v1/envios/{id}/estado?estadoEnvio={estado}`
Actualiza el estado de un envío.

**Estados disponibles:**
| Estado | Descripción |
|---|---|
| `PREPARANDO` | Envío en preparación |
| `EN_CAMINO` | Envío en camino |
| `ENTREGADO` | Envío entregado — registra fechaEntrega automáticamente |

**Ejemplos:**
```
PUT /api/v1/envios/1/estado?estadoEnvio=EN_CAMINO
PUT /api/v1/envios/1/estado?estadoEnvio=ENTREGADO
```

---

### DELETE `/api/v1/envios/{id}`
Elimina un envío por su id.

---

## Dependencias
| MS | Puerto | Para qué |
|---|---|---|
| MS Pedidos | 8081 | Obtener dirección de envío del pedido |