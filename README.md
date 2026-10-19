# Plataforma Web de Tutorías UTS (`tutorias-uts`)

Prototipo funcional monolítico para el agendamiento y gestión de tutorías académicas en las **Unidades Tecnológicas de Santander (UTS)**.

---

## 📌 1. Objetivo del Proyecto

Brindar un canal institucional estable, seguro y accesible para consultar disponibilidad de docentes y monitores, reservar citas de tutoría, realizar seguimiento académico preventivo y colaborar en foros temáticos por asignatura, evitando cruces de horarios y mejorando la comunicación académica.

---

## 🛠️ 2. Stack Tecnológico y Restricciones

### Tecnologías Utilizadas
* **Lenguaje:** Java 21 LTS
* **Framework:** Spring Boot 3.3.4
* **Base de Datos:** MongoDB Atlas (base de datos: `tutorias_uts`)
* **Motor de Plantillas:** Thymeleaf (renderizado del lado del servidor)
* **Diseño e Interfaz:** Bootstrap 5.3 (CDN) + Bootstrap Icons + CSS propio con paleta institucional UTS (`#1B6B3A`)
* **Construcción y Dependencias:** Maven Wrapper (`./mvnw`)

### Restricciones Acatadas Estrictamente (Sección 3.3)
* ❌ **Sin Lombok:** Todos los getters, setters y constructores fueron codificados a mano.
* ❌ **Sin Spring Security:** La autenticación institucional y la autorización por roles se maneja mediante `SesionUsuario` (`@SessionScope`) y `AccesoInterceptor` (`HandlerInterceptor`).
* ❌ **Sin APIs REST (`@RestController`):** Arquitectura MVC pura con controladores `@Controller` retornando plantillas Thymeleaf con el patrón **Post/Redirect/Get (PRG)** y mensajes flash (`RedirectAttributes`).
* ❌ **Sin JPA / SQL ni Frameworks JS pesados:** Conexión con Spring Data MongoDB y JavaScript mínimo (únicamente los bundles modales de Bootstrap).
* ❌ **Sin `HttpSession` ni `HttpServletRequest` en controladores/servicios:** Sesión gestionada de forma limpia mediante componentes desacoplados.

---

## 🗄️ 3. Configuración y Conexión a MongoDB Atlas

El sistema utiliza la base de datos `tutorias_uts` en MongoDB Atlas.

### Variable de Entorno `MONGODB_URI`
De acuerdo con la sección 10.2 de la especificación técnica, cualquier caracter especial en la clave (como `@`) debe ir codificado en porcentaje (`@` se codifica como `%40`):

```text
mongodb+srv://USUARIO:CLAVE_CODIFICADA@HOST/tutorias_uts?retryWrites=true&w=majority
```

#### En Windows (PowerShell):
```powershell
$env:MONGODB_URI = "mongodb+srv://mmiguelr1256:MarcoPolo69%40@main.drwjqup.mongodb.net/tutorias_uts?retryWrites=true&w=majority"
```

#### En Linux / macOS (Bash):
```bash
export MONGODB_URI="mongodb+srv://mmiguelr1256:MarcoPolo69%40@main.drwjqup.mongodb.net/tutorias_uts?retryWrites=true&w=majority"
```

*(El archivo `application.properties` ya cuenta con el valor por defecto configurado para ejecución inmediata).*

---

## 👥 4. Roles y Cuentas de Prueba Pre-cargadas (`DataLoader`)

El componente `DataLoader` inicializa automáticamente la base de datos si la colección de usuarios está vacía, con las siguientes cuentas institucionales (todas con dominio `@correo.uts.edu.co`, ingreso sin contraseña):

| Rol | Correo Institucional | Nombre de Prueba | Panel de Aterrizaje Inicial |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin@correo.uts.edu.co` | Administrador UTS | `/admin/asignaturas` |
| **DOCENTE** | `docente1@correo.uts.edu.co` | Prof. Roberto Gómez | `/docente/solicitudes` |
| **MONITOR** | `monitor1@correo.uts.edu.co` | Mario Monitor | `/docente/solicitudes` |
| **COORDINADOR** | `coordinador@correo.uts.edu.co` | Dra. Patricia Silva | `/reportes/asignaturas` |
| **DIRECTOR** | `director@correo.uts.edu.co` | Dr. Alfonso Valbuena (Sistemas) | `/reportes/asignaturas` |
| **BIENESTAR** | `bienestar@correo.uts.edu.co` | Lic. Claudia Bienestar | `/reportes/participacion` |
| **ESTUDIANTE** | `estudiante1@correo.uts.edu.co` | Andrés Felipe Pérez | `/estudiante/tutorias` |
| **ESTUDIANTE** | `estudiante2@correo.uts.edu.co` | Beatriz Elena Morales | `/estudiante/tutorias` |
| **ESTUDIANTE** | `estudiante3@correo.uts.edu.co` | Camilo Torres (Industrial) | `/estudiante/tutorias` |

---

## 🧪 5. Ejecución de Pruebas Automatizadas

El proyecto cuenta con una suite completa de pruebas unitarias con **JUnit 5 y Mockito** sobre los servicios de negocio (sin requerir conexión a MongoDB) y una prueba de contexto general de Spring Boot:

```bash
# En Windows:
.\mvnw test

# En Linux / macOS:
./mvnw test
```

### Reglas probadas unitariamente (Sección 12.1):
1. ✅ Solicitar una franja ya `RESERVADA` lanza `ReglaNegocioException`.
2. ✅ Rechazar una cita deja la cita en `RECHAZADA` y la franja en `LIBRE`.
3. ✅ Cancelar una cita deja la cita en `CANCELADA` y la franja en `LIBRE`.
4. ✅ Crear una franja con fecha pasada falla.
5. ✅ Docente sin la asignatura asignada no puede publicar franjas.
6. ✅ Hora fin anterior o igual a hora inicio falla.
7. ✅ Estudiante con otra cita activa a la misma fecha y hora no puede reservar.
8. ✅ Validaciones de longitud en foros (título 3-120 chars, contenido 5-1000 chars, respuestas 2-500 chars).
9. ✅ El Director de programa solo visualiza asignaturas y docentes de su programa académico.
10. ✅ Identificación de estudiantes en riesgo sin tutorías en asignaturas críticas para Bienestar (sin exponer datos privados).

---

## 🚀 6. Cómo Ejecutar la Aplicación

Para iniciar la aplicación en el servidor embebido Tomcat (puerto `8080`):

```bash
# En Windows:
.\mvnw spring-boot:run

# En Linux / macOS:
./mvnw spring-boot:run
```

Luego, abre tu navegador web e ingresa a:
👉 **`http://localhost:8080/`**

---

## 📋 7. Guía de Verificación Funcional Manual (Sección 12.2)

1. **Intento de ingreso con correo fuera del dominio:**
   * Ingresa a `/login` e intenta ingresar con `usuario@gmail.com`.
   * *Resultado esperado:* Error de validación impidiendo el acceso, solo se acepta dominio `@correo.uts.edu.co`.
2. **Control de acceso por rol (Interceptor):**
   * Inicia sesión con `estudiante1@correo.uts.edu.co` e intenta acceder manualmente a `/docente/solicitudes` o `/admin/asignaturas`.
   * *Resultado esperado:* Redirección inmediata a `/acceso-denegado` (código 403).
3. **Flujo completo de tutoría:**
   * Inicia sesión con `docente1@correo.uts.edu.co`, ve a `/docente/disponibilidad` y publica una franja para una de sus materias asignadas en una fecha futura.
   * Cierra sesión e ingresa con `estudiante1@correo.uts.edu.co`, ve a `/estudiante/tutorias`, busca la franja y solicita la tutoría indicando el tema. La franja pasará a `RESERVADA`.
   * Cierra sesión e ingresa con `docente1@correo.uts.edu.co`, ve a `/docente/solicitudes` y presiona **Aceptar**. La cita pasa a `ACEPTADA`.
4. **Filtro de programa para el Director:**
   * Inicia sesión con `director@correo.uts.edu.co` (Director de Ingeniería de Sistemas).
   * Ingresa a `/reportes/asignaturas`.
   * *Resultado esperado:* Solo aparecen asignaturas y docentes de Ingeniería de Sistemas, excluyendo las de Ingeniería Industrial.
5. **Confidencialidad de Bienestar:**
   * Inicia sesión con `bienestar@correo.uts.edu.co` e ingresa a `/reportes/participacion`.
   * *Resultado esperado:* Se observa el listado de estudiantes en riesgo pero **no** se exponen motivos, temas ni comentarios pedagógicos de las citas.
