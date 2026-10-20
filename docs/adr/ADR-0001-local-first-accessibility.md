# ADR-0001 — Arquitectura local-first con mapa accesible y bóveda multiperfil

Fecha: 2026-10-20
Estado: PROPUESTA — requiere aprobación arquitectónica

## Contexto
Voice Access puede fallar ante selectores de fecha, desplegables y elementos web mal etiquetados. El usuario necesita invocar ayuda por voz sin perder Chrome, reutilizar información autorizada de varias personas y guardar opcionalmente nuevos campos.

## Decisión propuesta
Adoptar Android AccessibilityService como mecanismo de inspección estructurada, mapa alternativo accesible en overlay temporal, motor determinista con IA local opcional bajo demanda, ejecutor con validación y verificación, bóveda local cifrada multiperfil y política de guardado diferido. Mantener envío final manual.

## Consecuencias
La aplicación debe tratar contenido web como no confiable y no asumir acceso DOM completo en Chrome Android. AccessibilityService requiere permiso sensible y su coexistencia con Voice Access debe probarse en dispositivo real. El cifrado y la importación de datos reales quedan sujetos a diseño y consentimiento. Los modelos de AI Edge Gallery no se reutilizan automáticamente.

## Alternativas pendientes de evaluación
Activación: acceso directo de accesibilidad, notificación o lanzamiento de actividad.
DOM: navegador con extensiones compatibles o integración especializada.
Visión: capturas puntuales autorizadas como último recurso.
Inferencia: LiteRT-LM u otro runtime local intercambiable.

## Reglas bloqueantes
Sin contraseñas, OTP ni tarjetas; sin PII en GitHub; sin guardado automático; sin inferencia permanente; sin envío automático; sin mezclar perfiles; parada independiente del modelo; sin implementación hasta cerrar pruebas de factibilidad y aprobación.

## Evidencia requerida
Pruebas en Samsung Tab A9 con Voice Access y Chrome; compilación y pruebas CI; auditoría de permisos; seguridad de bóveda y restauración; test de no mezcla de perfiles; estados verificables.
