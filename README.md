# El Vago

Proyecto independiente de accesibilidad digital para Android. Objetivo: ayudar a personas que usan Voice Access a identificar y, en futuras versiones, resolver controles de formularios inaccesibles.

## MVP 0.1 (experimental)
- Android AccessibilityService de **solo lectura**, activado voluntariamente.
- Diagnóstico local agregado: tipos y cantidades de controles, sin copiar texto, contraseñas, valores ni contenido de pantallas.
- Sin Internet, IA, captura de imágenes, automatización de clics, envío de formularios ni almacenamiento persistente.
- El servicio puede observar ventanas de otras aplicaciones cuando se habilita. Usar solo en un dispositivo de prueba y desactivarlo al terminar.

## Compilación
GitHub Actions > Android CI en cada PR. APK debug solo si el job termina correctamente. No instalar un APK que no haya superado CI.

## Política de seguridad
Fail closed. Ninguna acción sensible se ejecuta sin aprobación explícita. No incluir datos personales en issues, artefactos o logs. No registrar contenido de campos. No mezclar código ni recursos con Porota Trading.

## Próximos hitos
1. Validar compilación, lint y permisos mínimos.
2. Añadir formulario ficticio y pruebas automatizadas.
3. Detectar estados de controles con consentimiento y sin retener contenido.
4. Planificador local intercambiable (Gemma/Qwen), validador estricto, parada y verificación.
5. Ejecución limitada y autorizada, con envío final siempre manual.
