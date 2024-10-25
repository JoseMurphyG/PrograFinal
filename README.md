<<<<<<< HEAD
# PrograFinal

# Chatbot Error Logging

Este proyecto contiene un sistema de registro de errores para capturar y almacenar excepciones ocurridas durante la ejecución del chatbot. Utiliza una entidad llamada `ErrorLog` para almacenar detalles sobre los errores y un servicio `ErrorLogService` para gestionar el registro y recuperación de errores.

## Descripción del Sistema de Registro de Errores

El sistema está diseñado para seguir el principio de responsabilidad única, separando la lógica de manejo de errores de la lógica principal del chatbot. Cada error que ocurra en la ejecución del chatbot se registra en la base de datos con detalles como el mensaje de error, el tipo de error, el rastreo de pila (stack trace), el `chatId` del chat que lo generó, y el `username` del usuario.

### Entidades

#### ErrorLog

La entidad `ErrorLog` contiene los siguientes campos:

- `id`: Identificador único del registro de error (auto-generado).
- `errorMessage`: Mensaje de error capturado.
- `stackTrace`: Detalles del rastreo de pila del error.
- `errorType`: Tipo de error (nombre de la clase de la excepción).
- `createdAt`: Fecha y hora de la creación del registro de error.
- `chatId`: ID del chat de Telegram donde ocurrió el error.
- `username`: Nombre de usuario de Telegram que generó el error.
- `chatMessage`: Relación con la entidad `ChatMessage` para rastrear qué mensaje desencadenó el error.

La entidad `ErrorLog` está mapeada a la tabla `error_logs` en la base de datos.

```java
@Entity
@Table(name = "error_logs")
@Data
@NoArgsConstructor
public class ErrorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String errorMessage;

    @Column(length = 4000)
    private String stackTrace;

    @Column(nullable = false)
    private String errorType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private Long chatId;

    private String username;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "chat_message_id")
    private ChatMessage chatMessage;
}
=======
# Telegram
Bot Telegram con Chatgpt
>>>>>>> d976445 (Cruds de las tabas client y request)
