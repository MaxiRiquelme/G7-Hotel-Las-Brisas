# Documentación del Sistema Hotel Las Brisas

## Índice
1. [Descripción General](#descripción-general)
2. [Arquitectura del Sistema](#arquitectura-del-sistema)
3. [Modelo de Datos](#modelo-de-datos)
4. [Capa de Controladores](#capa-de-controladores)
5. [Capa de Vista](#capa-de-vista)
6. [Relaciones entre Clases](#relaciones-entre-clases)
7. [Flujos de Operación](#flujos-de-operación)

---

## Descripción General

**Hotel Las Brisas** es un sistema de gestión hotelera desarrollado en Java con interfaz gráfica Swing. El sistema gestiona dos módulos principales:
- **Módulo de Reservas**: Gestión de habitaciones, huéspedes y reservas
- **Módulo de Cafetería**: Gestión de productos, inventario y ventas

El sistema utiliza persistencia de datos mediante serialización de objetos en archivos binarios.

---

## Arquitectura del Sistema

El proyecto sigue el patrón de arquitectura **Modelo-Vista-Controlador (MVC)**:

```
┌─────────────────┐
│     VISTA       │  → Interfaces gráficas (Swing)
│  (vista/)       │
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│  CONTROLADOR    │  → Lógica de negocio
│ (controlador/)  │
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│     MODELO      │  → Entidades de datos
│   (modelo/)     │
└─────────────────┘
```

### Componentes Principales:
- **Main.java**: Punto de entrada de la aplicación
- **Paquete modelo**: Entidades de datos del sistema
- **Paquete controlador**: Lógica de negocio y gestión de datos
- **Paquete vista**: Interfaz gráfica de usuario

---

## Modelo de Datos

### 1. **Habitacion**
**Propósito**: Representa una habitación del hotel con sus características y estado.

**Atributos**:
- `numero`: String - Identificador único de la habitación
- `tipo`: String - Tipo de habitación ("Single", "Matrimonial", "Suite")
- `precio`: Double - Precio por noche
- `estado`: String - Estado actual ("DISPONIBLE", "OCUPADA")
- `capacidad`: Integer - Capacidad de personas (calculada según tipo)
- `fechaDesocupacion`: Date - Fecha programada de desocupación (null si está disponible)

**Métodos Principales**:
- `isDisponible()`: Verifica si la habitación está disponible
- `setDisponible(boolean)`: Cambia el estado de disponibilidad
- `verificarEstado()`: Imprime el estado actual

**Relaciones**:
- Es utilizada por `Reserva` (relación de asociación)
- Es gestionada por `ControladorHotel`

---

### 2. **Huesped**
**Propósito**: Representa a un cliente/huésped del hotel.

**Atributos**:
- `rut`: String - Identificador único del huésped
- `nombre`: String - Nombre del huésped
- `apellido`: String - Apellido del huésped
- `telefono`: String - Teléfono de contacto
- `numeroDocumento`: Integer - Número de documento
- `numeroHabitacion`: String - Habitación asignada (null si no es huésped activo)

**Métodos Principales**:
- `esHuesped()`: Verifica si está actualmente hospedado (tiene habitación asignada)

**Relaciones**:
- Es referenciado por `Reserva` (composición)
- Es referenciado por `Venta` (asociación)
- Es gestionado por `ControladorHotel` y `ControladorCafeteria`

---

### 3. **Reserva**
**Propósito**: Representa una reserva de habitación realizada por un huésped.

**Atributos**:
- `idReserva`: String - Identificador único de la reserva
- `huesped`: Huesped - Huésped que realiza la reserva
- `habitacion`: Habitacion - Habitación reservada
- `fechaEntrada`: Date - Fecha de check-in
- `fechaSalida`: Date - Fecha de check-out
- `diasEstadia`: int - Cantidad de días de la estadía
- `estado`: String - Estado de la reserva ("ACTIVA", "PENDIENTE", "FINALIZADA", "CANCELADA")
- `totalPagado`: Double - Monto total pagado
- `metodoPago`: String - Método de pago utilizado

**Métodos Principales**:
- `calcularFechaSalida(Date, int)`: Calcula la fecha de salida basada en días de estadía
- `setDiasEstadia(int)`: Actualiza los días y recalcula fecha de salida

**Relaciones**:
- **Composición** con `Huesped` (una reserva pertenece a un huésped)
- **Asociación** con `Habitacion` (una reserva utiliza una habitación)
- Es gestionada por `ControladorHotel`

**Estados de Reserva**:
- `PENDIENTE`: Reserva futura que aún no ha comenzado
- `ACTIVA`: Reserva en curso (huésped hospedado)
- `FINALIZADA`: Reserva completada (check-out realizado)
- `CANCELADA`: Reserva cancelada

---

### 4. **Producto**
**Propósito**: Representa un producto disponible en la cafetería del hotel.

**Atributos**:
- `idProducto`: String - Identificador único del producto
- `nombre`: String - Nombre del producto
- `precio`: int - Precio unitario
- `stock`: int - Cantidad disponible en inventario
- `categoria`: String - Categoría del producto

**Métodos Principales**:
- `disminuirStock(int)`: Reduce el stock en la cantidad especificada
- `agregarStock(int)`: Incrementa el stock
- `calcularPrecio(int)`: Calcula el precio total para una cantidad
- `actualizarStock()`: Método para actualización general de stock

**Relaciones**:
- Es utilizado por `DetalleVenta` (composición)
- Es gestionado por `ControladorCafeteria`

---

### 5. **DetalleVenta**
**Propósito**: Representa un ítem individual dentro de una venta (producto + cantidad).

**Atributos**:
- `producto`: Producto - Producto vendido
- `cantidad`: int - Cantidad vendida
- `subtotal`: int - Subtotal calculado (precio × cantidad)

**Métodos Principales**:
- Getters para acceder a los atributos

**Relaciones**:
- **Composición** con `Producto` (contiene un producto específico)
- Es contenido en una lista dentro de `Venta`
- Es utilizado por `ControladorCafeteria` para gestionar el carrito

---

### 6. **Venta**
**Propósito**: Representa una transacción de venta en la cafetería.

**Atributos**:
- `id`: long - Identificador único de la venta
- `fecha`: String - Fecha y hora de la venta (formato "dd/MM/yyyy HH:mm:ss")
- `huesped`: Huesped - Cliente que realiza la compra
- `detalles`: List<DetalleVenta> - Lista de productos vendidos
- `metodoPago`: String - Método de pago ("EFECTIVO", "TARJETA", "CARGO_HABITACION")
- `total`: int - Monto total de la venta
- `vuelto`: int - Vuelto entregado (solo para efectivo)

**Métodos Principales**:
- Getters para acceder a los atributos
- `setVuelto(int)`: Establece el vuelto

**Relaciones**:
- **Composición** con `DetalleVenta` (una venta contiene múltiples detalles)
- **Asociación** con `Huesped` (una venta es realizada por un huésped/cliente)
- Es gestionada por `ControladorCafeteria`

---

## Capa de Controladores

### 1. **GestorDatos** (Singleton)
**Propósito**: Gestor centralizado de persistencia de datos. Implementa el patrón Singleton para garantizar una única instancia.

**Responsabilidades**:
- Cargar datos desde archivo binario al inicio
- Guardar datos en archivo binario
- Proporcionar acceso centralizado a las colecciones de datos
- Gestionar listeners para notificaciones de actualización

**Atributos**:
- `instancia`: GestorDatos (static) - Instancia única del singleton
- `huespedes`: List<Huesped> - Colección de huéspedes
- `habitaciones`: List<Habitacion> - Colección de habitaciones
- `reservas`: List<Reserva> - Colección de reservas
- `productos`: List<Producto> - Colección de productos
- `ventas`: List<Venta> - Colección de ventas
- `listeners`: List<ActualizacionListener> - Listeners registrados

**Métodos Principales**:
- `obtenerInstancia()`: Retorna la instancia única (patrón Singleton)
- `cargarDatos()`: Deserializa datos desde "datos_sistema.bin"
- `guardarDatos()`: Serializa datos al archivo binario
- `inicializarDatos()`: Crea datos iniciales si no existe archivo
- `agregarHuesped(Huesped)`: Agrega un huésped y guarda
- `agregarReserva(Reserva)`: Agrega una reserva y guarda
- `agregarVenta(Venta)`: Agrega una venta y guarda
- `agregarListener(ActualizacionListener)`: Registra un listener
- `notificarActualizacion(String)`: Notifica a los listeners

**Relaciones**:
- Es utilizado por `ControladorHotel` y `ControladorCafeteria`
- Gestiona todas las entidades del modelo
- Implementa el patrón Observer mediante `ActualizacionListener`

**Inicialización de Datos**:
- Habitaciones: 15 Single (101-115), 15 Matrimonial (201-215), 7 Suite (301-307)
- Productos: 10 productos de cafetería predefinidos

---

### 2. **ControladorHotel**
**Propósito**: Gestiona toda la lógica de negocio relacionada con reservas y habitaciones.

**Atributos**:
- `gestor`: GestorDatos - Referencia al gestor de datos
- `habitaciones`: List<Habitacion> - Lista de habitaciones
- `huespedes`: List<Huesped> - Lista de huéspedes
- `reservas`: List<Reserva> - Lista de reservas

**Métodos Principales**:

#### Búsqueda
- `buscarHabitacionesDisponibles(String tipo)`: Filtra habitaciones disponibles por tipo
- `buscarHabitacion(String numero)`: Busca una habitación por número
- `buscarCliente(String rut)`: Busca un huésped por RUT
- `buscarReservaActiva(String numHabitacion)`: Busca la reserva activa de una habitación

#### Gestión de Reservas
- `realizarReserva(...)`: Crea una nueva reserva (2 sobrecargas)
  - Versión 1: Reserva inmediata (fecha entrada = hoy)
  - Versión 2: Reserva con fecha de entrada personalizada
  - **Validaciones**:
    - Habitación debe existir
    - Días de estadía > 0
    - Fecha de entrada no puede ser pasada
    - No debe haber solapamiento con otras reservas
  - **Proceso**:
    - Busca o crea huésped
    - Calcula fechas y total
    - Crea reserva con estado ACTIVA o PENDIENTE
    - Si es para hoy, ocupa la habitación inmediatamente

- `extenderEstadia(String numHabitacion, int diasAdicionales)`: Extiende una reserva activa
  - **Validaciones**:
    - Debe existir reserva activa
    - Días adicionales > 0
    - La extensión no debe solaparse con otras reservas
  - **Retorna**: Monto adicional a pagar

- `cancelarReserva(String numHabitacion)`: Cancela una reserva por habitación
- `cancelarReservaPorId(String idReserva)`: Cancela una reserva por ID

#### Gestión de Habitaciones
- `liberarHabitacion(String numHabitacion)`: Libera una habitación (check-out)
  - Marca habitación como disponible
  - Desvincula huésped de la habitación
  - Marca reserva como FINALIZADA

- `activarReservasPendientes()`: Activa reservas pendientes cuya fecha de entrada es hoy
  - Se ejecuta al iniciar la aplicación

- `obtenerRangoOcupacion(String numeroHabitacion)`: Obtiene los rangos de fechas ocupadas

#### Métodos Auxiliares
- `esHoy(Date)`: Verifica si una fecha es hoy
- `esHoyOFutura(Date)`: Verifica si una fecha es hoy o futura
- `verificarDisponibilidadFechas(...)`: Verifica disponibilidad para un rango de fechas
- `calcularDiasMaximosExtension(...)`: Calcula días máximos de extensión sin conflictos

**Relaciones**:
- Utiliza `GestorDatos` para persistencia
- Gestiona entidades: `Habitacion`, `Huesped`, `Reserva`
- Es utilizado por `PanelReserva` y `VistaPrincipal`

---

### 3. **ControladorCafeteria**
**Propósito**: Gestiona toda la lógica de negocio relacionada con productos, inventario y ventas.

**Atributos**:
- `gestor`: GestorDatos - Referencia al gestor de datos
- `productos`: List<Producto> - Lista de productos
- `huespedes`: List<Huesped> - Lista de huéspedes
- `carritoActual`: List<DetalleVenta> - Carrito de compras temporal

**Métodos Principales**:

#### Gestión de Productos
- `obtenerProductos()`: Retorna lista completa de productos
- `buscarProductos(String consulta)`: Busca productos por nombre (case-insensitive)
- `buscarProductoPorId(String id)`: Busca un producto por ID
- `agregarProductoNuevo(...)`: Agrega un nuevo producto al inventario
  - **Validaciones**: ID único, precio > 0, stock >= 0

#### Gestión de Inventario
- `aumentarStock(String id, int cantidad)`: Incrementa stock de un producto
- `disminuirStock(String id, int cantidad)`: Reduce stock de un producto
  - **Validaciones**: Cantidad > 0, stock suficiente
- `eliminarProducto(String id)`: Elimina un producto del inventario

#### Gestión de Carrito
- `agregarAlCarrito(Producto, int cantidad)`: Agrega producto al carrito
  - **Validación**: Stock suficiente
- `vaciarCarrito()`: Limpia el carrito
- `getCarrito()`: Obtiene items del carrito
- `calcularTotalCarrito()`: Suma los subtotales del carrito

#### Procesamiento de Ventas
- `finalizarVenta(...)`: Procesa el pago y completa la venta
  - **Parámetros**: RUT, nombre, habitación, método pago, monto efectivo, número tarjeta
  - **Validaciones**:
    - Carrito no vacío
    - Dinero suficiente (efectivo)
    - Número de tarjeta (tarjeta)
    - Cliente sea huésped (cargo a habitación)
  - **Proceso**:
    - Busca o crea huésped
    - Valida según método de pago
    - Reduce stock de productos
    - Crea y guarda venta
    - Vacía carrito

#### Otros
- `buscarCliente(String rut)`: Busca un huésped por RUT
- `agregarListener(ActualizacionListener)`: Registra listener de actualización

**Relaciones**:
- Utiliza `GestorDatos` para persistencia
- Gestiona entidades: `Producto`, `DetalleVenta`, `Venta`, `Huesped`
- Es utilizado por `PanelVenta` y `PanelInventario`

---

### 4. **ActualizacionListener** (Interfaz)
**Propósito**: Define el contrato para objetos que desean ser notificados de cambios en los datos.

**Métodos**:
- `onActualizacion(String tipo)`: Método callback invocado cuando hay una actualización

**Implementado por**:
- `PanelInventario`: Actualiza tabla de productos
- `PanelVenta`: Actualiza lista de productos

**Patrón**: Observer

---

## Capa de Vista

### 1. **VistaPrincipal**
**Propósito**: Ventana principal de la aplicación que gestiona la navegación entre módulos.

**Atributos**:
- `ctrlHotel`: ControladorHotel - Controlador del módulo hotel
- `ctrlCafeteria`: ControladorCafeteria - Controlador del módulo cafetería
- `panelContenido`: JPanel - Panel contenedor con CardLayout
- `cardLayout`: CardLayout - Gestor de layout para cambio de vistas

**Componentes**:
- Panel de Menú Principal con botones:
  - Reserva de Habitación → `PanelReserva`
  - Cafetería → `PanelVenta`
  - Inventario Cafetería → `PanelInventario`
  - Salir del Sistema

**Métodos**:
- `inicializarUI()`: Configura la interfaz gráfica
- `crearPanelMenu()`: Crea el menú principal
- `crearBotonMenu(String, Color)`: Crea botones estilizados
- `mostrarVista(String)`: Cambia entre vistas usando CardLayout

**Constructor**:
- Recibe ambos controladores
- Activa reservas pendientes al iniciar
- Inicializa la UI

**Relaciones**:
- Contiene `PanelReserva`, `PanelVenta`, `PanelInventario`
- Utiliza `ControladorHotel` y `ControladorCafeteria`

---

### 2. **PanelReserva**
**Propósito**: Panel para gestión de reservas de habitaciones.

**Atributos**:
- `controlador`: ControladorHotel
- `mainFrame`: VistaPrincipal - Para navegación
- `cmbTipoHabitacion`: JComboBox - Filtro de tipo
- `modeloTabla`: DefaultTableModel - Modelo de la tabla
- `tablaHabitaciones`: JTable - Tabla de habitaciones
- `formatoFecha`: SimpleDateFormat

**Funcionalidades**:

#### Visualización
- Tabla con columnas: Número, Tipo, Precio/Noche, Estado, Rango de Ocupación
- Filtro por tipo de habitación (TODAS, Single, Matrimonial, Suite)
- Botón "Verificar Disponibilidad" para refrescar

#### Proceso de Reserva (`iniciarProcesoReserva()`)
1. **Selección de habitación** desde la tabla
2. **Detección de estado**:
   - Si está OCUPADA: Ofrece reserva futura
   - Si está DISPONIBLE: Permite inmediata o agendada
3. **Captura de datos del huésped**:
   - RUT, teléfono (obligatorios)
   - Busca huésped existente o solicita nombre/apellido
4. **Selección de fecha de entrada**:
   - Inmediata (hoy) o agendada
   - Si agendada: Selector de fecha personalizado
5. **Periodo de estadía**: Cantidad de días
6. **Método de pago**: Efectivo, Tarjeta, Transferencia
7. **Confirmación**: Muestra resumen completo
8. **Generación**: Crea reserva y muestra voucher

#### Gestión de Habitaciones (`mostrarGestionHabitaciones()`)
- **Desocupar Habitación**: Check-out de habitación ocupada
- **Extender Estadía**: Agregar días a reserva activa
- **Cancelar Reserva**: Cancelar reserva activa o pendiente

**Métodos Auxiliares**:
- `seleccionarFechaEntrada()`: Diálogo con spinners para selección de fecha
- `mostrarVoucher(Reserva)`: Muestra comprobante de reserva
- `desocuparHabitacion()`: Proceso de check-out
- `extenderEstadia()`: Proceso de extensión
- `cancelarReserva()`: Proceso de cancelación

**Relaciones**:
- Utiliza `ControladorHotel` para todas las operaciones
- Es contenido en `VistaPrincipal`
- Interactúa con entidades: `Habitacion`, `Huesped`, `Reserva`

---

### 3. **PanelVenta**
**Propósito**: Panel para realizar ventas en la cafetería del hotel.

**Atributos**:
- `controlador`: ControladorCafeteria
- `mainFrame`: VistaPrincipal
- `txtBuscar`: JTextField - Campo de búsqueda
- `modeloTablaProd`: DefaultTableModel - Tabla de productos
- `modeloTablaCarrito`: DefaultTableModel - Tabla del carrito
- `lblTotal`: JLabel - Etiqueta del total
- `tablaProductos`: JTable

**Implementa**: `ActualizacionListener`

**Funcionalidades**:

#### Búsqueda de Productos
- Campo de texto con búsqueda en tiempo real
- Tabla con: ID, Producto, Precio, Stock
- Doble clic en producto para agregar al carrito

#### Gestión de Carrito
- Tabla con: Item, Cantidad, Subtotal
- Etiqueta de total dinámico
- Se actualiza automáticamente al agregar productos

#### Proceso de Venta (`iniciarProcesoPago()`)
1. **Identificación del cliente**:
   - Solicita RUT
   - Busca cliente existente o crea nuevo
   - Identifica si es huésped (tiene habitación)
2. **Selección de método de pago**:
   - Efectivo: Solicita monto recibido, calcula vuelto
   - Tarjeta: Solicita número (simulado)
   - Cargo a Habitación: Valida que sea huésped
3. **Finalización**:
   - Procesa venta mediante controlador
   - Muestra ticket/recibo
   - Vacía carrito
   - Retorna al menú

**Métodos**:
- `onActualizacion(String)`: Actualiza tabla cuando cambian productos
- `buscarProductos()`: Filtra y muestra productos
- `agregarAlCarrito(String)`: Agrega producto al carrito
- `actualizarCarritoUI()`: Refresca visualización del carrito
- `iniciarProcesoPago()`: Proceso completo de pago
- `mostrarRecibo(Venta)`: Genera y muestra ticket

**Relaciones**:
- Utiliza `ControladorCafeteria` para todas las operaciones
- Implementa `ActualizacionListener` para actualizaciones automáticas
- Es contenido en `VistaPrincipal`

---

### 4. **PanelInventario**
**Propósito**: Panel para gestión de inventario de productos de cafetería.

**Atributos**:
- `controlador`: ControladorCafeteria
- `mainFrame`: VistaPrincipal
- `modelo`: DefaultTableModel
- `tabla`: JTable

**Implementa**: `ActualizacionListener`

**Funcionalidades**:

#### Visualización
- Tabla con: ID, Nombre, Precio, Stock
- Actualización automática al recibir notificaciones

#### Operaciones
- **Nuevo Producto**: Diálogo para agregar producto
  - Campos: ID, Nombre, Precio, Stock
- **Aumentar Stock**: Incrementar inventario de un producto
- **Reducir Stock**: Decrementar inventario de un producto
- **Eliminar Producto**: Eliminar producto del sistema

**Métodos**:
- `onActualizacion(String)`: Actualiza tabla cuando notifican cambios
- `actualizarTabla()`: Refresca datos de la tabla
- `refrescarTabla()`: Recarga productos desde controlador
- `agregarProducto()`: Diálogo y lógica para nuevo producto
- `aumentarStock()`: Diálogo y lógica para incrementar stock
- `reducirStock()`: Diálogo y lógica para reducir stock
- `eliminarProducto()`: Diálogo de confirmación y eliminación

**Relaciones**:
- Utiliza `ControladorCafeteria` para todas las operaciones
- Implementa `ActualizacionListener` para actualizaciones automáticas
- Es contenido en `VistaPrincipal`

---

## Relaciones entre Clases

### Diagrama de Relaciones Principales

```
Main
 │
 └──> VistaPrincipal
       │
       ├──> ControladorHotel ──────┐
       │     │                     │
       │     └──> GestorDatos <────┼─────┐
       │           (Singleton)     │     │
       │                           │     │
       ├──> ControladorCafeteria ──┘     │
       │                                 │
       ├──> PanelReserva                 │
       │                                 │
       ├──> PanelVenta                   │
       │     (ActualizacionListener)     │
       │                                 │
       └──> PanelInventario              │
             (ActualizacionListener)     │
                                         │
            ┌────────────────────────────┘
            │
            ├──> List<Habitacion>
            ├──> List<Huesped>
            ├──> List<Reserva>
            ├──> List<Producto>
            └──> List<Venta>
```

### Relaciones de Composición y Agregación

#### Composición (Parte-Todo fuerte)
- `Reserva` **compone** `Huesped`: Una reserva siempre tiene un huésped específico
- `Venta` **compone** `List<DetalleVenta>`: Una venta contiene detalles
- `DetalleVenta` **compone** `Producto`: Un detalle contiene un producto

#### Agregación (Parte-Todo débil)
- `Reserva` **agrega** `Habitacion`: Una reserva usa una habitación
- `GestorDatos` **agrega** todas las listas de entidades

#### Asociación
- `Huesped` ↔ `Habitacion`: Un huésped puede tener una habitación asignada
- `ControladorHotel` → `GestorDatos`: El controlador usa el gestor
- `ControladorCafeteria` → `GestorDatos`: El controlador usa el gestor

#### Implementación de Interfaces
- `PanelVenta` **implementa** `ActualizacionListener`
- `PanelInventario` **implementa** `ActualizacionListener`

#### Dependencias
- `Main` → `VistaPrincipal`: Main crea la vista
- `Main` → `ControladorHotel`: Main crea el controlador
- `Main` → `ControladorCafeteria`: Main crea el controlador
- `VistaPrincipal` → `PanelReserva`: Vista contiene el panel
- `VistaPrincipal` → `PanelVenta`: Vista contiene el panel
- `VistaPrincipal` → `PanelInventario`: Vista contiene el panel

---

## Flujos de Operación

### Flujo 1: Inicio de Aplicación

```
1. Main.main()
   │
   ├─> UIManager.setLookAndFeel() [Configuración de UI]
   │
   └─> SwingUtilities.invokeLater() [EDT]
       │
       ├─> GestorDatos.obtenerInstancia()
       │   │
       │   ├─> cargarDatos()
       │   │   ├─> ¿Existe datos_sistema.bin?
       │   │   ├─── Sí: Deserializar datos
       │   │   └─── No: inicializarDatos()
       │   │
       │   └─> Retorna instancia única
       │
       ├─> new ControladorHotel()
       │   └─> Obtiene referencias de GestorDatos
       │
       ├─> new ControladorCafeteria()
       │   └─> Obtiene referencias de GestorDatos
       │
       └─> new VistaPrincipal(ctrlHotel, ctrlCafeteria)
           │
           ├─> ctrlHotel.activarReservasPendientes()
           │   └─> Reservas PENDIENTES con fecha=hoy → ACTIVA
           │
           └─> vista.setVisible(true)
```

### Flujo 2: Realizar Reserva Inmediata

```
1. Usuario selecciona habitación en PanelReserva
   │
2. PanelReserva.iniciarProcesoReserva()
   │
   ├─> Verifica estado de habitación
   │   ├─── OCUPADA: Ofrece reserva futura
   │   └─── DISPONIBLE: Continúa
   │
   ├─> Solicita datos del huésped (RUT, teléfono)
   │
   ├─> ControladorHotel.buscarCliente(rut)
   │   ├─── Existe: Usa datos existentes
   │   └─── No existe: Solicita nombre/apellido
   │
   ├─> Selección de tipo de entrada
   │   ├─── Inmediata: fecha = hoy
   │   └─── Agendada: seleccionarFechaEntrada()
   │
   ├─> Solicita días de estadía
   │
   ├─> Calcula total (precioNoche × días)
   │
   ├─> Selecciona método de pago
   │
   ├─> Muestra resumen y solicita confirmación
   │
   └─> ControladorHotel.realizarReserva(...)
       │
       ├─> Validaciones:
       │   ├─ Habitación existe
       │   ├─ Días > 0
       │   ├─ Fecha no es pasada
       │   └─ No hay solapamiento de fechas
       │
       ├─> Busca o crea Huesped
       │
       ├─> Calcula fechas (entrada, salida)
       │
       ├─> Crea Reserva
       │   ├─── esHoy(fechaEntrada)?
       │   │    ├─ Sí: estado = "ACTIVA"
       │   │    │      habitacion.setDisponible(false)
       │   │    │      huesped.setNumeroHabitacion()
       │   │    └─ No: estado = "PENDIENTE"
       │   │
       │   └─> gestor.agregarReserva(reserva)
       │       └─> gestor.guardarDatos()
       │
       └─> PanelReserva.mostrarVoucher(reserva)
```

### Flujo 3: Extender Estadía

```
1. Usuario selecciona "Extender Estadía"
   │
2. PanelReserva.extenderEstadia()
   │
   ├─> Solicita número de habitación
   │
   ├─> Solicita días adicionales
   │
   └─> ControladorHotel.extenderEstadia(numHab, diasAdic)
       │
       ├─> buscarReservaActiva(numHab)
       │   └─ No existe → Exception
       │
       ├─> Validaciones:
       │   └─ diasAdicionales > 0
       │
       ├─> Calcula nueva fecha de salida
       │
       ├─> Verifica conflictos con otras reservas
       │   │
       │   └─ Para cada reserva de la misma habitación:
       │       ├─ ¿Se solapa nueva fecha?
       │       ├─── Sí: Exception con detalles
       │       └─── No: Continúa
       │
       ├─> Sin conflictos:
       │   ├─ reserva.setDiasEstadia(nuevos)
       │   ├─ Actualiza totalPagado
       │   ├─ Actualiza fechaDesocupacion de habitación
       │   └─ gestor.guardarDatos()
       │
       └─> Retorna monto adicional
```

### Flujo 4: Activación Automática de Reservas Pendientes

```
[Al iniciar la aplicación]

VistaPrincipal(constructor)
 │
 └─> ControladorHotel.activarReservasPendientes()
     │
     ├─> Obtiene fecha actual (hoy)
     │
     ├─> Para cada reserva en lista:
     │   │
     │   └─ ¿estado == "PENDIENTE" Y esHoy(fechaEntrada)?
     │       │
     │       ├─── Sí:
     │       │    ├─ reserva.setEstado("ACTIVA")
     │       │    ├─ habitacion.setDisponible(false)
     │       │    ├─ habitacion.setFechaDesocupacion(fechaSalida)
     │       │    ├─ huesped.setNumeroHabitacion(numero)
     │       │    └─ gestor.guardarDatos()
     │       │
     │       └─── No: Continúa con siguiente
     │
     └─> Fin del proceso
```

### Flujo 5: Realizar Venta en Cafetería

```
1. Usuario busca y agrega productos al carrito
   │
   ├─> PanelVenta.agregarAlCarrito(id)
   │   │
   │   └─> ControladorCafeteria.agregarAlCarrito(producto, 1)
   │       ├─ Valida stock suficiente
   │       ├─ Crea DetalleVenta
   │       └─ Agrega a carritoActual
   │
2. Usuario hace clic en "Procesar Pago"
   │
3. PanelVenta.iniciarProcesoPago()
   │
   ├─> Solicita RUT del cliente
   │
   ├─> ControladorCafeteria.buscarCliente(rut)
   │   ├─── Existe: Muestra datos
   │   └─── No existe: Solicita nombre y habitación
   │
   ├─> Muestra opciones de pago
   │   ├─ Efectivo
   │   ├─ Tarjeta
   │   └─ Cargo a Habitación
   │
   ├─> Según método de pago:
   │   ├─ EFECTIVO: Solicita monto recibido
   │   ├─ TARJETA: Solicita número de tarjeta
   │   └─ CARGO_HABITACION: (validado en controlador)
   │
   └─> ControladorCafeteria.finalizarVenta(...)
       │
       ├─> Validaciones:
       │   ├─ Carrito no vacío
       │   ├─ Dinero suficiente (efectivo)
       │   ├─ Número tarjeta (tarjeta)
       │   └─ Es huésped (cargo habitación)
       │
       ├─> Busca o crea Huesped
       │
       ├─> Calcula total del carrito
       │
       ├─> Calcula vuelto (si efectivo)
       │
       ├─> Reduce stock de productos:
       │   └─ Para cada DetalleVenta:
       │       └─ producto.disminuirStock(cantidad)
       │
       ├─> Crea Venta
       │
       ├─> gestor.agregarVenta(venta)
       │   └─> gestor.guardarDatos()
       │       └─> notificarActualizacion("PRODUCTOS")
       │           └─> PanelInventario.onActualizacion()
       │               └─> actualizarTabla()
       │
       ├─> vaciarCarrito()
       │
       └─> Retorna venta
           │
           └─> PanelVenta.mostrarRecibo(venta)
```

### Flujo 6: Gestión de Inventario

```
1. Usuario selecciona "Inventario Cafetería"
   │
2. VistaPrincipal.mostrarVista("INVENTARIO")
   │
   └─> PanelInventario se hace visible
       │
       ├─> Constructor ya registró listener:
       │   └─ controlador.agregarListener(this)
       │       └─ gestor.agregarListener(this)
       │
       └─> actualizarTabla()
           └─ Muestra todos los productos

3. Usuario modifica inventario:
   │
   ├─> Aumentar Stock:
   │   ├─ Solicita ID del producto
   │   ├─ Solicita cantidad
   │   └─> ControladorCafeteria.aumentarStock(id, cant)
   │       ├─ producto.agregarStock(cant)
   │       └─ gestor.guardarDatos()
   │           └─ notificarActualizacion("PRODUCTOS")
   │               └─ PanelInventario.onActualizacion()
   │                   └─ actualizarTabla()
   │
   ├─> Reducir Stock: (similar a aumentar)
   │
   ├─> Nuevo Producto:
   │   └─> ControladorCafeteria.agregarProductoNuevo(...)
   │       ├─ Valida ID único
   │       ├─ Crea Producto
   │       ├─ Agrega a lista
   │       └─ gestor.guardarDatos()
   │           └─ notificarActualizacion("PRODUCTOS")
   │
   └─> Eliminar Producto:
       └─> ControladorCafeteria.eliminarProducto(id)
           ├─ Busca producto
           ├─ Remueve de lista
           └─ gestor.guardarDatos()
               └─ notificarActualizacion("PRODUCTOS")
```

### Flujo 7: Persistencia de Datos

```
[Guardar Datos]

Cualquier operación que modifica datos
 │
 └─> gestor.guardarDatos()
     │
     ├─> Crea ObjectOutputStream
     │
     ├─> Serializa en orden:
     │   ├─ writeObject(huespedes)
     │   ├─ writeObject(habitaciones)
     │   ├─ writeObject(reservas)
     │   ├─ writeObject(productos)
     │   └─ writeObject(ventas)
     │
     ├─> Cierra stream
     │
     └─> notificarActualizacion("PRODUCTOS")
         │
         └─> Para cada listener registrado:
             └─ listener.onActualizacion("PRODUCTOS")

[Cargar Datos]

GestorDatos.obtenerInstancia() [primera vez]
 │
 └─> GestorDatos()
     │
     └─> cargarDatos()
         │
         ├─> ¿Existe "datos_sistema.bin"?
         │   │
         │   ├─── Sí:
         │   │    ├─ Crea ObjectInputStream
         │   │    ├─ Deserializa en orden:
         │   │    │  ├─ huespedes = readObject()
         │   │    │  ├─ habitaciones = readObject()
         │   │    │  ├─ reservas = readObject()
         │   │    │  ├─ productos = readObject()
         │   │    │  └─ ventas = readObject()
         │   │    └─ Cierra stream
         │   │
         │   └─── No:
         │        └─ inicializarDatos()
         │           ├─ Crea listas vacías
         │           ├─ Agrega 37 habitaciones
         │           ├─ Agrega 10 productos
         │           └─ guardarDatos()
         │
         └─> Datos listos para usar
```

---

## Características Especiales del Sistema

### 1. Patrón Singleton en GestorDatos
- Garantiza una única instancia del gestor de datos
- Evita inconsistencias en la persistencia
- Permite acceso global controlado

### 2. Patrón Observer con ActualizacionListener
- Permite actualización automática de vistas
- Desacopla modelo de vista
- Notificaciones específicas por tipo de actualización

### 3. Gestión de Reservas Futuras
- Reservas PENDIENTES para fechas futuras
- Activación automática al iniciar la aplicación
- Prevención de solapamiento de fechas

### 4. Validaciones de Negocio
- Stock insuficiente (cafetería)
- Solapamiento de reservas
- Fechas inválidas (pasadas)
- Métodos de pago según tipo de cliente

### 5. Cálculos Automáticos
- Fechas de salida basadas en días de estadía
- Subtotales y totales en ventas
- Vuelto en pagos en efectivo
- Capacidad de habitaciones según tipo

### 6. Persistencia Robusta
- Serialización automática al modificar datos
- Inicialización con datos por defecto
- Manejo de errores al cargar/guardar

---

## Conclusión

El sistema **Hotel Las Brisas** es una aplicación completa de gestión hotelera que integra:

- **Gestión de Reservas**: Control completo de habitaciones, huéspedes y reservas con soporte para reservas futuras
- **Gestión de Cafetería**: Sistema de punto de venta con carrito, múltiples métodos de pago e inventario
- **Persistencia**: Almacenamiento automático y confiable de datos
- **Interfaz Intuitiva**: Navegación simple entre módulos con retroalimentación clara al usuario

La arquitectura MVC proporciona:
- **Separación de responsabilidades**
- **Facilidad de mantenimiento**
- **Escalabilidad**
- **Reutilización de código**

Los patrones de diseño implementados (Singleton, Observer) demuestran buenas prácticas de programación orientada a objetos.

