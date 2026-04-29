# CLAUDE.md — E-Commerce Platform Project Guide

> Bu dosya Claude Code için proje anayasasıdır. Yeni bir oturum açıldığında, herhangi bir kod yazmadan önce bu dosyanın tamamı okunmalıdır. Buradaki kararlar daha önceki bir mimari tasarım sürecinde bilinçli olarak verilmiştir — yeniden tartışılmaz, uygulanır.

---

## 1. Proje Özeti

Spring Boot 3 + React.js ile geliştirilmiş, **Gerçek Mikroservis Mimarisi** kullanan bir e-ticaret platformu.

Kapsam: ürün listeleme, sepet yönetimi, sipariş oluşturma, Iyzico ödeme entegrasyonu, JWT kimlik doğrulama, CI/CD, AWS ECS deployment.

---

## 2. Kesinleşmiş Teknoloji Stack'i

### Backend
| Bileşen | Teknoloji | Versiyon |
|---|---|---|
| Dil | Java | 21 (LTS) |
| Framework | Spring Boot | 3.x |
| Build Tool | Maven | 3.9+ |
| ORM | Spring Data JPA (Hibernate) | Spring Boot ile gelen |
| DB Migration | Flyway | Spring Boot ile gelen |
| Ana Veritabanı | PostgreSQL | 15+ |
| Sepet Depolama | Redis | 7+ |
| Mesajlaşma | RabbitMQ | 3.12+ |
| Service Discovery | Eureka (Spring Cloud Netflix) | Spring Cloud ile gelen |
| Config Yönetimi | Spring Cloud Config Server | Spring Cloud ile gelen |
| API Gateway | Spring Cloud Gateway | Spring Cloud ile gelen |
| Tracing | Micrometer Tracing + Zipkin | Spring Boot ile gelen |
| Güvenlik | Spring Security + JWT (jjwt) | 0.12+ |
| API Docs | SpringDoc OpenAPI (Swagger UI) | 2.x |
| Test | JUnit 5 + Mockito + Testcontainers | Spring Boot ile gelen |
| Containerization | Docker + Jib (Dockerfile'sız) | Jib 3.x |

### Frontend
| Bileşen | Teknoloji |
|---|---|
| Framework | React 18 |
| Build Tool | Vite |
| Styling | Tailwind CSS |
| State Management | Zustand |
| Routing | React Router v6 |
| HTTP Client | Axios (interceptor ile JWT yönetimi) |
| Form Yönetimi | React Hook Form + Zod |

### DevOps
| Bileşen | Teknoloji |
|---|---|
| CI/CD | GitHub Actions |
| Containerization | Docker + Docker Compose (local) |
| Cloud | AWS ECS (Fargate) + RDS (PostgreSQL) |
| Image Build | Jib (Maven plugin) |
| Monitoring/Alert | Slack webhook (deploy bildirimleri) |

---

## 3. Sistem Mimarisi

### 3.1 Servis Topolojisi

```
React SPA (Vite + Tailwind + Zustand)
           │
           ▼ HTTPS
┌─────────────────────┐
│   API Gateway       │  :8080  — Tek giriş noktası
│   Spring Cloud GW   │         JWT doğrulama, routing, CORS, rate limit
└──────────┬──────────┘
           │
    ┌──────┴──────┐
    │   Eureka    │  :8761  — Service Discovery
    │Config Server│  :8888  — Merkezi konfigürasyon
    │   Zipkin    │  :9411  — Distributed tracing
    └──────┬──────┘
           │
    ┌──────┴──────────────────────────────────────────┐
    │                                                 │
  :8081   :8082    :8083    :8084    :8085    :8086   │
User    Product   Cart    Order   Payment  Notification│
 │        │        │        │        │        │       │
user_db prod_db  Redis   order_db pay_db  (no DB)     │
                                                      │
        ┌─────────────────────────────────────────────┘
        │         RabbitMQ :5672 (Event Bus)
        └──────────────────────────────────────────────
```

### 3.2 Servis Portları ve Routing

| Servis | Port | Gateway Route Prefix |
|---|---|---|
| API Gateway | 8080 | — |
| User Service | 8081 | `/api/users/**`, `/api/auth/**` |
| Product Service | 8082 | `/api/products/**`, `/api/categories/**` |
| Cart Service | 8083 | `/api/cart/**` |
| Order Service | 8084 | `/api/orders/**` |
| Payment Service | 8085 | `/api/payments/**` |
| Notification Service | 8086 | (sadece event consumer, dış endpoint yok) |
| Eureka | 8761 | — |
| Config Server | 8888 | — |
| Zipkin | 9411 | — |
| RabbitMQ Management UI | 15672 | — |

---

## 4. Servis Sorumlulukları (Sınır Kuralları)

> **ALTIN KURAL**: Hiçbir servis başka bir servisin veritabanına doğrudan erişemez. Servisler arası veri ihtiyacı ya REST (senkron) ya da Event (asenkron) üzerinden karşılanır.

### User Service
- Kullanıcı kayıt/giriş, JWT üretimi (access + refresh token)
- Kullanıcı profili CRUD
- Rol yönetimi: `USER`, `ADMIN`
- Bilmediği şeyler: Sipariş, sepet, ürün — bunları asla sormaz

### Product Service
- Ürün ve kategori CRUD (admin yetkisi gerekir)
- Ürün listeleme (pagination, filtreleme, sıralama)
- Variant yönetimi (tek boyut: örn. beden veya renk)
- **Stok yönetimi** — rezervasyon, onay, iptal
- Stok inconsistency'yi önlemek için stok mantığı yalnızca burada olur

### Cart Service
- Kullanıcı sepeti CRUD (Redis'te, TTL: 30 gün)
- Sepete ürün eklerken **fiyat snapshot** alınır
- Checkout anında fiyatlar Product Service'ten yeniden doğrulanır
- Sadece login olan kullanıcıların sepeti vardır (guest checkout yok)

### Order Service
- Sipariş oluşturur — **Saga Choreography'nin başlatıcısıdır**
- Sipariş anındaki ürün bilgilerini (isim, fiyat, miktar) kendi tablosuna kopyalar (snapshot)
- Sipariş durumlarını yönetir: `PENDING → STOCK_RESERVED → PAYMENT_REQUESTED → CONFIRMED | CANCELLED`
- `order_status_history` tablosunda her geçiş loglanır

### Payment Service
- Sadece Iyzico ile konuşur
- Order'ı bilmez; `orderReference` (opaque UUID), kullanıcı ID ve tutar yeterlidir
- Iyzico Checkout Form (hosted) kullanılır — PCI-DSS yükü Iyzico'da
- Ödeme sonuçlarını event olarak yayınlar

### Notification Service
- Tüm domain event'lerini dinler
- V1'de gerçek email göndermez; log'a yazar (mock)
- DB'si yoktur
- Yeni bildirim kanalı eklemek bu servise dokunmayı gerektirir, başka servise dokunulmaz

### API Gateway
- Business logic içermez — asla
- JWT imzasını doğrular; servislere `X-User-Id` ve `X-User-Role` header'ları iletir
- Downstream servisler bu header'lara güvenir, tekrar JWT parse etmez

---

## 5. Event Catalog (RabbitMQ)

Exchange tipi: **Topic Exchange** (`ecommerce.events`)

| Event | Routing Key | Producer | Consumer(s) |
|---|---|---|---|
| `UserRegistered` | `user.registered` | User | Notification |
| `OrderCreated` | `order.created` | Order | Product, Notification |
| `StockReserved` | `stock.reserved` | Product | Order |
| `StockReservationFailed` | `stock.reservation.failed` | Product | Order |
| `PaymentRequested` | `payment.requested` | Order | Payment |
| `PaymentCompleted` | `payment.completed` | Payment | Order, Product, Notification |
| `PaymentFailed` | `payment.failed` | Payment | Order, Product, Notification |
| `OrderConfirmed` | `order.confirmed` | Order | Cart, Notification |
| `OrderCancelled` | `order.cancelled` | Order | Product, Notification |
| `StockCommitted` | `stock.committed` | Product | — |
| `StockReleased` | `stock.released` | Product | — |

**Idempotency Kuralı**: Her event consumer, işlemeden önce `processed_events` tablosunu kontrol eder. RabbitMQ "at-least-once delivery" garantisi verir; aynı event iki kez gelebilir. Her consumer bu durumu handle etmek zorundadır.

---

## 6. Saga Choreography — Sipariş Akışı

```
[User] → POST /api/orders (checkout)
   │
   ▼
[Order Service]
  → Order kaydı oluştur (status=PENDING)
  → PUBLISH: OrderCreated {orderId, userId, items[], totalAmount}
   │
   ▼
[Product Service] ← CONSUME: OrderCreated
  → Stok rezervasyonu dene (stock - reserved_stock >= quantity)
  ├─ Başarılı → PUBLISH: StockReserved {orderId}
  └─ Başarısız → PUBLISH: StockReservationFailed {orderId, reason}
   │
   ├─ [StockReserved] ──────────────────────────────────►
   │                                              [Order Service]
   │                                               → status = STOCK_RESERVED
   │                                               → PUBLISH: PaymentRequested
   │                                                     │
   │                                               [Payment Service]
   │                                               → Iyzico checkout başlat
   │                                               ├─ Başarılı → PaymentCompleted
   │                                               └─ Başarısız → PaymentFailed
   │
   └─ [StockReservationFailed] ──────────────────────────►
                                               [Order Service]
                                               → status = CANCELLED
                                               [Notification] log

[PaymentCompleted] → fanout:
  → [Order]   status = CONFIRMED
  → [Product] stok kalıcı düş (StockCommitted)
  → [Cart]    sepeti temizle (OrderConfirmed event tetikler)
  → [Notif]   "Siparişiniz onaylandı" log

[PaymentFailed] → fanout:
  → [Order]   status = CANCELLED
  → [Product] rezervasyonu serbest bırak (StockReleased)
  → [Notif]   "Ödeme başarısız" log
```

---

## 7. Veritabanı Şemaları

### user_db
```sql
users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  role VARCHAR(20) NOT NULL DEFAULT 'USER', -- USER | ADMIN
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP
)

refresh_tokens (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES users(id),
  token_hash VARCHAR(255) NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  revoked BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT now()
)
```

### product_db
```sql
categories (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(100) NOT NULL,
  slug VARCHAR(100) UNIQUE NOT NULL,
  parent_id UUID REFERENCES categories(id)
)

products (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name VARCHAR(255) NOT NULL,
  slug VARCHAR(255) UNIQUE NOT NULL,
  description TEXT,
  category_id UUID REFERENCES categories(id),
  base_price NUMERIC(10,2) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP
)

product_variants (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  product_id UUID NOT NULL REFERENCES products(id),
  sku VARCHAR(100) UNIQUE NOT NULL,
  variant_value VARCHAR(100) NOT NULL, -- örn: "M", "L", "Kırmızı"
  price NUMERIC(10,2) NOT NULL,
  stock INTEGER NOT NULL DEFAULT 0,
  reserved_stock INTEGER NOT NULL DEFAULT 0,
  CONSTRAINT chk_stock CHECK (stock >= 0),
  CONSTRAINT chk_reserved CHECK (reserved_stock >= 0),
  CONSTRAINT chk_available CHECK (stock >= reserved_stock)
)

stock_reservations (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id UUID NOT NULL,
  variant_id UUID NOT NULL REFERENCES product_variants(id),
  quantity INTEGER NOT NULL,
  status VARCHAR(20) NOT NULL, -- PENDING | COMMITTED | RELEASED
  expires_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now()
)

processed_events (
  event_id VARCHAR(255) PRIMARY KEY,
  processed_at TIMESTAMP NOT NULL DEFAULT now()
)
```

### order_db
```sql
orders (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL,
  status VARCHAR(30) NOT NULL,
  -- PENDING | STOCK_RESERVED | PAYMENT_REQUESTED | CONFIRMED | CANCELLED
  total_amount NUMERIC(10,2) NOT NULL,
  shipping_address JSONB NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP
)

order_items (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id UUID NOT NULL REFERENCES orders(id),
  product_id UUID NOT NULL,
  variant_id UUID NOT NULL,
  product_name_snapshot VARCHAR(255) NOT NULL, -- anlık fiyat/isim kaydı
  variant_value_snapshot VARCHAR(100) NOT NULL,
  unit_price_snapshot NUMERIC(10,2) NOT NULL,
  quantity INTEGER NOT NULL
)

order_status_history (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id UUID NOT NULL REFERENCES orders(id),
  from_status VARCHAR(30),
  to_status VARCHAR(30) NOT NULL,
  changed_at TIMESTAMP NOT NULL DEFAULT now()
)

processed_events (
  event_id VARCHAR(255) PRIMARY KEY,
  processed_at TIMESTAMP NOT NULL DEFAULT now()
)
```

### payment_db
```sql
payments (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_reference UUID NOT NULL UNIQUE, -- opaque ref, order_id değil
  user_id UUID NOT NULL,
  amount NUMERIC(10,2) NOT NULL,
  currency VARCHAR(10) NOT NULL DEFAULT 'TRY',
  status VARCHAR(20) NOT NULL, -- PENDING | COMPLETED | FAILED
  iyzico_payment_id VARCHAR(255),
  iyzico_response JSONB,
  created_at TIMESTAMP NOT NULL DEFAULT now(),
  updated_at TIMESTAMP
)

processed_events (
  event_id VARCHAR(255) PRIMARY KEY,
  processed_at TIMESTAMP NOT NULL DEFAULT now()
)
```

### Cart — Redis Key Yapısı
```
Key:   cart:{userId}
Value: JSON string
TTL:   2592000 (30 gün)

JSON Yapısı:
{
  "userId": "uuid",
  "items": [
    {
      "productId": "uuid",
      "variantId": "uuid",
      "productName": "...",
      "variantValue": "M",
      "priceSnapshot": 299.90,
      "quantity": 2,
      "addedAt": "ISO-8601"
    }
  ],
  "updatedAt": "ISO-8601"
}
```

---

## 8. Proje Klasör Yapısı

```
ecommerce-platform/
├── CLAUDE.md                          ← Bu dosya
├── README.md
├── docker-compose.yml                 ← Tüm local stack
├── docker-compose.infra.yml           ← Sadece altyapı (PG, Redis, RabbitMQ, Zipkin)
├── .github/
│   └── workflows/
│       ├── ci.yml                     ← Build + Test
│       └── deploy.yml                 ← ECS deploy + Slack bildirim
│
├── shared/
│   └── common-events/                 ← Servisler arası paylaşılan event sınıfları
│       └── pom.xml
│
├── infrastructure/
│   ├── api-gateway/
│   ├── discovery-server/              ← Eureka
│   └── config-server/
│
├── services/
│   ├── user-service/
│   ├── product-service/
│   ├── cart-service/
│   ├── order-service/
│   ├── payment-service/
│   └── notification-service/
│
└── frontend/                          ← React + Vite
    ├── src/
    │   ├── assets/
    │   ├── components/
    │   │   ├── ui/                    ← Paylaşılan UI bileşenleri
    │   │   └── layout/
    │   ├── features/                  ← Feature-based yapı
    │   │   ├── auth/
    │   │   ├── products/
    │   │   ├── cart/
    │   │   └── orders/
    │   ├── hooks/                     ← Custom hooks
    │   ├── lib/
    │   │   ├── axios.ts               ← Axios instance + interceptors
    │   │   └── utils.ts
    │   ├── stores/                    ← Zustand stores
    │   │   ├── authStore.ts
    │   │   └── cartStore.ts
    │   ├── types/                     ← TypeScript tipleri
    │   └── pages/                     ← Route sayfaları
    ├── package.json
    ├── vite.config.ts
    ├── tailwind.config.js
    └── tsconfig.json
```

---

## 9. Her Spring Boot Servisi için Paket Yapısı

Tüm servisler bu yapıyı takip eder. **Hexagonal Architecture** (Ports & Adapters).

```
{service-name}/
└── src/
    ├── main/
    │   ├── java/com/ecommerce/{service}/
    │   │   ├── {Service}Application.java
    │   │   │
    │   │   ├── domain/                     ← Framework bağımsız iş mantığı
    │   │   │   ├── model/                  ← Domain entity'leri (JPA değil!)
    │   │   │   ├── exception/              ← Domain exception'ları
    │   │   │   └── service/                ← Domain servisleri (pure business logic)
    │   │   │
    │   │   ├── application/                ← Use case orchestration
    │   │   │   ├── usecase/                ← Her kullanım senaryosu için bir sınıf
    │   │   │   ├── port/
    │   │   │   │   ├── in/                 ← Use case interface'leri (incoming port)
    │   │   │   │   └── out/                ← Repository/publisher interface'leri (outgoing port)
    │   │   │   └── dto/                    ← Application layer DTO'ları
    │   │   │
    │   │   ├── infrastructure/             ← Framework, DB, dış sistemler
    │   │   │   ├── persistence/
    │   │   │   │   ├── entity/             ← JPA @Entity sınıfları
    │   │   │   │   ├── repository/         ← Spring Data JPA interface'leri
    │   │   │   │   └── adapter/            ← Outgoing port implementasyonları
    │   │   │   ├── messaging/
    │   │   │   │   ├── publisher/          ← RabbitMQ event publisher
    │   │   │   │   └── consumer/           ← RabbitMQ event consumer
    │   │   │   ├── client/                 ← Feign clients (senkron servis çağrıları)
    │   │   │   └── config/                 ← Spring configuration sınıfları
    │   │   │
    │   │   └── interfaces/                 ← Dışa açılan yüzey
    │   │       └── rest/
    │   │           ├── controller/         ← @RestController sınıfları
    │   │           ├── dto/                ← Request/Response DTO'ları
    │   │           └── mapper/             ← DTO ↔ Domain mapper (MapStruct)
    │   │
    │   └── resources/
    │       ├── application.yml
    │       ├── application-local.yml
    │       └── db/migration/               ← Flyway SQL dosyaları (V1__, V2__ ...)
    │
    └── test/
        └── java/com/ecommerce/{service}/
            ├── unit/                       ← Domain ve use case testleri (mock ile)
            ├── integration/                ← Testcontainers ile gerçek DB testi
            └── architecture/               ← ArchUnit ile katman kuralı testleri
```

**Bağımlılık Kuralı (kesinlikle uyulacak):**
```
interfaces → application → domain
infrastructure → application (port interface'lerini implement eder)
domain hiçbir şeye bağımlı değildir
```

---

## 10. Kodlama Kuralları

### Genel
- Her public method'un Javadoc'u olmak zorunda değil, ama karmaşık iş mantığı içeren methodlar için kısa bir açıklama eklenmelidir.
- Magic string/number kullanılmaz; sabitler `enum` veya `static final` ile tanımlanır.
- `var` kullanımı teşvik edilir (Java 21).
- Record kullanımı teşvik edilir: DTO'lar, event payload'ları için `record` tercih edilir.

### Controller Kuralları
- Controller'lar ince (thin) olmalıdır: validation ve use case çağrısından ibaret.
- Business logic controller'da olmaz.
- Her controller endpoint'i `@Operation` ile Swagger dokümantasyonu içerir.
- Response tipi her zaman spesifik bir DTO'dur, entity asla döndürülmez.

### Service / Use Case Kuralları
- Bir use case sınıfı tek bir işi yapar (SRP).
- Use case'ler interface üzerinden çağrılır (`port/in/`).
- Domain service'ler Spring bean değildir (mümkün olduğunda); pure Java sınıflarıdır.

### Exception Handling
- Her servis `GlobalExceptionHandler` (@RestControllerAdvice) içerir.
- Domain exception'lar `DomainException` base class'ından türer.
- Response formatı standart: `{ "timestamp", "status", "error", "message", "path" }`
- 4xx hataları log'a INFO, 5xx hataları ERROR olarak düşülür.

### Test Kuralları
- Her use case için unit test yazılır (mock repository ile).
- Her REST endpoint için integration test yazılır (Testcontainers + gerçek DB).
- Test coverage: minimum %70 (line coverage).
- Test ismi formatı: `methodName_givenContext_expectedBehavior()`.

### Loglama Kuralları
- `@Slf4j` ile Lombok logger kullanılır.
- Log seviyesi: DEBUG (geliştirme), INFO (production).
- Her servis başında ve bitişinde `traceId` log'a eklenir (MDC ile).
- Hassas veri (şifre, kart no) asla log'a yazılmaz.

---

## 11. JWT ve Güvenlik Kuralları

- **Access Token**: 15 dakika ömür, stateless, sadece imza doğrulanır.
- **Refresh Token**: 7 gün ömür, `refresh_tokens` tablosunda hash'i saklanır, revoke edilebilir.
- **Gateway Sorumluluğu**: JWT imzasını doğrular → `X-User-Id` ve `X-User-Role` header ekler → downstream'e iletir.
- **Downstream Servis Sorumluluğu**: JWT parse etmez. `X-User-Id` header'ına güvenir.
- **Public Endpoint'ler** (JWT gerektirmez): `POST /api/auth/register`, `POST /api/auth/login`, `GET /api/products/**`
- **USER rolü gerektirenler**: `/api/cart/**`, `POST /api/orders`, `GET /api/orders/{id}`
- **ADMIN rolü gerektirenler**: `POST/PUT/DELETE /api/products/**`, `GET /api/orders` (tüm siparişler)

---

## 12. RabbitMQ Konfigürasyonu

```
Exchange: ecommerce.events (topic)
Exchange: ecommerce.events.dlx (dead-letter exchange)

Queue isimlendirme: {service}.{event}
Örnek: product.order-created, order.stock-reserved

Her queue'nun DLQ (Dead Letter Queue) karşılığı vardır:
Örnek: product.order-created.dlq

TTL: Mesaj 24 saat işlenmezse DLQ'ya düşer.
```

**Idempotency implementasyonu** (her consumer için):
```java
// EventIdempotencyService (her serviste aynı pattern)
@Transactional
public boolean isAlreadyProcessed(String eventId) {
    // processed_events tablosunu kontrol et
}

public void markAsProcessed(String eventId) {
    // processed_events tablosuna kaydet
}
```

---

## 13. API Sözleşmeleri (Standart Formatlar)

### Başarılı Yanıt
```json
{
  "data": { ... },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

### Hata Yanıtı
```json
{
  "timestamp": "2024-01-01T10:00:00Z",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Ürün bulunamadı",
  "path": "/api/products/123"
}
```

### Pagination Yanıtı
```json
{
  "data": {
    "content": [ ... ],
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8,
    "last": false
  }
}
```

---

## 14. Docker ve Local Geliştirme

### docker-compose.infra.yml
Sadece altyapı servisleri:
- `postgres-user` (:5433)
- `postgres-product` (:5434)
- `postgres-order` (:5435)
- `postgres-payment` (:5436)
- `redis` (:6379)
- `rabbitmq` (:5672, management :15672)
- `zipkin` (:9411)

### Servis Başlatma Sırası (local)
```
1. docker-compose -f docker-compose.infra.yml up -d
2. config-server başlat (port 8888)
3. discovery-server başlat (port 8761) — config-server'a bağımlı
4. api-gateway başlat (port 8080) — eureka'ya bağımlı
5. Business servisler (herhangi sırada)
```

### Spring Profile Stratejisi
- `local`: Yerel geliştirme, hardcoded URL'ler
- `docker`: Docker Compose ortamı, servis isimleriyle URL
- `prod`: AWS ortamı, environment variable'lardan

---

## 15. CI/CD Pipeline (GitHub Actions)

### ci.yml (her push, PR'da tetiklenir)
```
1. Java 21 setup
2. Maven build + test (mvn verify)
3. Test raporu yayınla
4. Docker image build (Jib ile, push olmadan)
```

### deploy.yml (main branch'e merge sonrası)
```
1. Maven build + test
2. Jib ile Docker image build + ECR'a push
3. ECS service update (rolling deploy)
4. Health check bekle
5. Slack bildirimi gönder (başarı/hata)
```

---

## 16. AWS Deployment Topolojisi

```
Internet → ALB (Application Load Balancer)
            → ECS Fargate Cluster
               → API Gateway Task
               → User Service Task
               → Product Service Task
               → Cart Service Task (ElastiCache Redis)
               → Order Service Task
               → Payment Service Task
               → Notification Service Task

RDS:  Her servis için ayrı PostgreSQL instance (veya ayrı DB, tek instance)
ElastiCache: Redis (Cart Service için)
MSK veya Amazon MQ: RabbitMQ yerine geçebilir
ECR: Docker image repository
```

---

## 17. Implementation Sırası

Bu sırayı koru. Dependencies var:

```
1. shared/common-events             (Maven modülü)
2. infrastructure/config-server     (diğerleri buna bağımlı)
3. infrastructure/discovery-server  (Eureka)
4. infrastructure/api-gateway       (routing kuralları boş başlar, servis eklendikçe dolar)
5. services/user-service            (JWT olmadan diğerleri test edilemez)
6. services/product-service         (Cart ve Order buna bağımlı)
7. services/cart-service            (Order'dan önce gelmeli)
8. services/order-service           (Saga'nın kalbi, en karmaşık)
9. services/payment-service         (Iyzico mock ile başla, sonra gerçek)
10. services/notification-service   (En basit, event consumer)
11. frontend/                       (Backend stabil olunca paralel başlar)
12. CI/CD pipeline                  (Tüm servisler hazır olunca)
13. AWS deployment                  (En son)
```

---

## 18. Önemli Kararlar ve Gerekçeleri (Değiştirilemez)

| Karar | Gerekçe |
|---|---|
| Her servis kendi PostgreSQL DB'si | Saf mikroservis prensibi, servis bağımsızlığı |
| Cart için Redis | Sepet ephemeral data, TTL desteği, yüksek okuma/yazma performansı |
| Choreography Saga (RabbitMQ) | Servisler birbirini bilmez, decoupled, event-driven |
| Fiyat snapshot (sepet ve sipariş) | Ürün fiyatı değişse bile geçmiş siparişler etkilenmez |
| Gateway'de JWT doğrulama | Tekrar eden kod önlenir, servisler sadece header'a güvenir |
| Iyzico Checkout Form | PCI-DSS yükü Iyzico'da, kart verisi sisteme girmez |
| Guest checkout YOK (v1) | Scope sadeligi, auth flow daha temiz |
| Admin UI: role-based routing (aynı frontend) | Tek codebase, JWT role claim ile guard |
| Notification Service v1: sadece log | Scope kontrolü, gerçek email sonradan eklenebilir |
| Idempotency (processed_events) | RabbitMQ at-least-once delivery, duplicate processing önlenir |

---

## 19. Yasaklar (Anti-Pattern Listesi)

- ❌ Bir servis başka servisin DB'sine direkt bağlanmaz
- ❌ Controller'da business logic olmaz
- ❌ Entity doğrudan API response'u olarak döndürülmez
- ❌ Servisler `@Autowired` field injection kullanmaz (constructor injection zorunlu)
- ❌ `System.out.println` kullanılmaz (her zaman `log.info/debug/error`)
- ❌ Unchecked exception'lar yukarıya fırlatılmaz (GlobalExceptionHandler yakalar)
- ❌ `@Transactional` her yere eklenmez; sadece gerekli yerlerde, doğru scope'ta
- ❌ Hardcoded URL, şifre, secret key kaynak kodunda olmaz (environment variable / config server)
- ❌ Test olmadan PR merge edilmez
- ❌ `TODO` comment bırakılmaz; ya implement edilir ya issue açılır

---

*Son güncelleme: Mimari tasarım oturumu sonrası*
*Versiyon: 1.0*
