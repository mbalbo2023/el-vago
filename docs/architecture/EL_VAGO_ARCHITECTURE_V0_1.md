# El Vago — Arquitectura maestra candidata v0.1
Fecha: 2026-10-20 | Estado: PROPUESTA EN REVISIÓN | No autoriza implementación ni permisos.

## Objetivo y límites
Agente asistivo Android, especialmente Samsung Tab A9 y Chrome, que complemente Voice Access para completar formularios, reutilizar datos autorizados y rescatar controles inaccesibles. Local-first, sin APIs pagas. No pertenece a Porota Trading. iOS queda como estudio separado de alcance limitado, sin prometer control general entre aplicaciones.

## Estado real y alcance
PR #1: prototipo Kotlin experimental de lectura de controles y formulario ficticio, aislado y no fusionado. Las pruebas de CI no equivalen a validación en tablet. No existe bóveda, integración de modelos, activación por voz contextual ni ejecutor autorizado. Congelar funcionalidades hasta aprobar arquitectura y pruebas de factibilidad. GitHub público: NUNCA incluir datos personales, valores reales, exportaciones, capturas privadas, claves o modelos sujetos a restricciones.

## Requisitos funcionales
1. Invocación desde Chrome mediante Voice Access sin perder el estado del formulario; mecanismo exacto pendiente de prueba en Tab A9.
2. Mapa alternativo temporal y accesible: controles con nombres inequívocos, números propios, navegación secuencial, foco y cierre sin interferir con Voice Access.
3. Descubrimiento escalonado: árbol AccessibilityService; integración DOM solo si navegador compatible; visión puntual autorizada; gesto como último recurso, con verificación.
4. Autocompletado por persona, sección y rol (titular, hija, responsable, etc.). Selección explícita de perfil; evitar mezclas involuntarias.
5. Varios valores por campo y perfil: múltiples teléfonos, documentos, correos, domicilios, etiquetas principal/alternativo y preferencias opcionales por sitio.
6. Bóveda cifrada local; consultar, modificar, borrar, importar/exportar cifrado con controles accesibles. Contraseñas, OTP y tarjetas fuera de alcance.
7. Campo nuevo: proponer guardado DESPUÉS de completar; nunca persistir sin consentimiento explícito. La decisión «más adelante» deja PENDING_SAVE_DECISION solo en sesión; al finalizar descarta el valor, sin guardarlo.
8. Precarga Initial Profile Import desde datos compartidos anteriormente en ChatGPT: inventario candidato, revisión de vigencia, deduplicación, identificación de titular, autorización por valor y transferencia segura directamente al dispositivo. ChatGPT y la app NO comparten automáticamente memoria ni almacenamiento. No precargar datos reales en código ni GitHub.
9. Envío final siempre manual. Operaciones de compra, transferencias y otras acciones con efectos externos excluidas del modo automático.
10. Parada independiente de la IA, reversión solo cuando sea segura y comprobable, y estado NO_VERIFICADO ante resultado ambiguo.

## Componentes propuestos
- Activation Adapter: accesos de accesibilidad / controles por voz / notificación como alternativas a validar; sin micrófono propio escuchando continuamente.
- Context Manager: paquete, ventana, instantánea de estructura y versión; invalida contexto ante navegación o cambios.
- Discovery Engine: árbol accesible con permisos acotados; jamás confundir acceso al árbol con acceso al DOM de Chrome.
- Accessible Map UI: accessibility overlay temporal; navegación por voz, números y nombres, botón Detener.
- Vault: personas, roles, definiciones semánticas de campo, valores múltiples, etiquetas, permisos, origen, caducidad y preferencias de sitio.
- Local AI Adapter: runtime y modelo intercambiables, carga bajo demanda, presupuesto de RAM/latencia, salida estrictamente validada. No asumir que modelos descargados en AI Edge Gallery pueden reutilizarse directamente.
- Policy Engine: autorización por campo/acción, prohibiciones, listas de exclusión, datos mínimos.
- Executor: acciones AccessibilityNodeInfo cuando existan; gestos acotados solo con consentimiento y revalidación.
- Verifier: lectura posterior y comparación del valor, incluyendo estado de página; no equiparar clic exitoso con tarea completada.
- Audit: solo métricas técnicas agregadas, sin valores ni textos personales.

## Modelo de datos lógico (no esquema final)
Person(person_id, display_label, relationship, authorization_status)
FieldDefinition(field_type_id, canonical_name, aliases, sensitivity)
FieldValue(value_id, person_id, field_type_id, encrypted_value, label, is_preferred, source, verified_at, retention_policy)
SharedValueLink(value_id, person_id, authorized_role)
SitePreference(site_scope, person_id, field_type_id, preferred_value_id, consent_scope)
ConsentGrant(grant_id, person_id, value_id, site_scope, operation, expires_at)
SessionContext(session_id, package_id, window_token, state, expires_at) — solo RAM
PendingSaveDecision(session_id, field_type_id, person_id, ephemeral_value) — solo RAM
AuditEvent(event_type, technical_status, timestamp) — sin valores sensibles

El diseño de cifrado debe seleccionar biblioteca mantenida y esquema de gestión de claves Android Keystore; considerar bloqueo, restauración, pérdida de claves, copias cifradas, migraciones, retención y exclusión de backups no autorizados. No asumir que Keystore cifra por sí solo una base de datos.

## Estados
IDLE -> INVOKED -> DISCOVERING -> PREPARING -> AUTHORIZED -> EXECUTING -> VERIFYING -> COMPLETED/NO_VERIFICADO/BLOQUEADO. STOP cancela operaciones pendientes desde cualquier estado activo; no espera a IA.
Nuevo dato: NOT_SAVED -> PENDING_SAVE_DECISION (efímero) -> SAVED solo tras consentimiento, o DISCARDED al terminar sesión. El silencio nunca es consentimiento.

## Flujo de rescate de fecha
Invocación -> detección de calendario -> selección de perfil y fecha -> validación de formato/rango -> previsualización y autorización -> estrategia (edición directa, acción accesible, navegación estructurada, adaptador DOM compatible, visión+gesto autorizado) -> revalidación de ventana -> ejecución individual -> verificación de fecha completa y aceptación por el formulario. No enviar.

## Flujo de precarga
1. Identificar fuentes de datos personales ya proporcionadas y disponibles con permiso del usuario, sin volcar datos en repositorio.
2. Extraer candidatos en entorno privado, marcar NO_VERIFICADO hasta confirmación; incluir titular, categoría, origen y fecha.
3. Mostrar inventario con valores ocultos parcialmente; aprobar/rechazar individualmente, incluidas personas distintas.
4. Generar paquete cifrado local y transferirlo al dispositivo por canal explícitamente elegido; no usar GitHub Actions/artifacts.
5. Importación con previsualización, deduplicación, confirmación y borrado seguro de temporales.
**Estado:** solo diseño; ninguna búsqueda de chats ni extracción autorizada en esta etapa.

## Amenazas y mitigaciones
- Datos de terceros: perfil explícito, autorización adecuada y no mezclar identidades.
- Controles falsos / etiquetas duplicadas / prompt injection en página: tratar todo contenido de página como datos no confiables; validar destino y política fuera del modelo.
- UI dinámica: invalidar referencias al cambiar ventana, foco, zoom o disposición.
- Permisos amplios de AccessibilityService: informar alcance real; aunque el código filtre, Android concede capacidad extensa; evitar vigilancia continua y no registrar contenido.
- Overlay/Voice Access: probar coexistencia, foco, orden de lectura y capacidad de detener.
- Capturas: puntuales y autorizadas; respetar FLAG_SECURE y otras restricciones.
- Fallas de modelo: timeout, cancelación, fallback determinista y cero acciones ambiguas.
- Repositorio público: pruebas exclusivamente ficticias; escaneo de secretos y dependencias.

## Criterios de aceptación antes de desarrollo funcional
A. Voice Access invoca panel y opera todos sus controles sin tocar pantalla.
B. Chrome mantiene formulario y estado tras abrir/cerrar panel.
C. Lectura de controles y regreso al contexto, sin inferencia permanente.
D. Parada accesible incluso con IA bloqueada.
E. Bóveda multiperfil evita confusión de titular y soporta múltiples teléfonos/documentos.
F. Consentimiento por valor, aprendizaje diferido efímero y descarte comprobable.
G. Formulario ficticio: fecha exacta verificada; envío final nunca automático.
H. Sin filtración de PII en logs, repositorio, CI o artefactos.
I. Rendimiento y compatibilidad medidos en Samsung Tab A9, sin inventar métricas.

## Preguntas abiertas (bloqueantes)
- ¿Qué mecanismo concreto de invocación permite Voice Access en esta tablet y versión Android?
- ¿Cómo se resuelve el foco y la navegación de accessibility overlay sin interferencia?
- ¿Qué motor local soporta Gemma4-E2B-it con RAM y rendimiento aceptables, y cómo se importan modelos?
- ¿Qué navegador/adaptador permite DOM en Android, si se justifica?
- ¿Cómo se cifran/restauran respaldos y se gestionan múltiples perfiles y permisos?
- ¿Qué UX evita revelar DNI completo al mostrar opciones de selección?
- ¿Qué tipos de datos personales de terceros son apropiados para importar?

## Gobernanza GitHub
PR #1 permanece aislado; este documento va en PR separado basado en main. No fusionar código ni autorizar nuevos permisos antes de ADR, pruebas de factibilidad, seguridad, QA y aprobación explícita. Cambios mediante PR con scope, SHA, CI, RCA y regresión; sin automatizar envío de formularios. Prohibido mezclar con Porota Trading.
