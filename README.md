# E-Commerce Platform

Spring Boot 3 + React.js ile geliştirilmiş, gerçek mikroservis mimarisi kullanan bir e-ticaret platformu. Ürün listeleme, sepet yönetimi, sipariş oluşturma, Iyzico ödeme entegrasyonu, JWT kimlik doğrulama, CI/CD ve AWS ECS deployment içerir.

---

## İçindekiler

1. [Teknoloji Stack'i](#teknoloji-stacki)
2. [Mimari Genel Bakış](#mimari-genel-bakış)
3. [Servisler ve Sorumlulukları](#servisler-ve-sorumlulukları)
4. [Neden Bu Teknolojiler?](#neden-bu-teknolojiler)
5. [Saga Choreography — Sipariş Akışı](#saga-choreography--sipariş-akışı)
6. [Event Sistemi (RabbitMQ)](#event-sistemi-rabbitmq)
7. [Güvenlik Mimarisi](#güvenlik-mimarisi)
8. [Veritabanı Tasarımı](#veritabanı-tasarımı)
9. [Konfigürasyon Yönetimi](#konfigürasyon-yönetimi)
10. [Proje Yapısı](#proje-yapısı)
11. [Yerel Geliştirme](#yerel-geliştirme)
12. [CI/CD Pipeline](#cicd-pipeline)
13. [AWS Deployment](#aws-deployment)

---

## Teknoloji Stack'i

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
| Service Discovery | Eureka (Spring Cloud Netflix) | Spring Cloud |
| Config Yönetimi | Spring Cloud Config Server | Spring Cloud |
| API Gateway | Spring Cloud Gateway | Spring Cloud |
| Tracing | Micrometer Tracing + Zipkin | Spring Boot |
| Güvenlik | Spring Security + JWT (jjwt) | 0.12+ |
| API Docs | SpringDoc OpenAPI (Swagger UI) | 2.x |
| Test | JUnit 5 + Mockito + Testcontainers | Spring Boot |
| Image Build | Jib (Dockerfile'sız) | 3.x |

### Frontend

| Bileşen | Teknoloji |
|---|---|
| Framework | React 18 |
| Build Tool | Vite |
| Styling | Tailwind CSS |
| State Management | Zustand |
| Routing | React Router v6 |
| HTTP Client | Axios (JWT interceptor) |
| Form Yönetimi | React Hook Form + Zod |

### DevOps

| Bileşen | Teknoloji |
|---|---|
| CI/CD | GitHub Actions |
| Containerization | Docker + Docker Compose |
| Cloud | AWS ECS (Fargate) + RDS |
| Monitoring | Slack webhook (deploy bildirimleri) |

---

## Mimari Genel Bakış

Platform, **Hexagonal Architecture (Ports & Adapters)** prensibiyle geliştirilmiş bağımsız mikroservislerden oluşur. Tüm trafik tek bir giriş noktasından (API Gateway) geçer.

```
React SPA (Vite + Tailwind + Zustand)
           │
           ▼ HTTPS
┌─────────────────────┐
│   API Gateway       │  :8080  — Tek giriş noktası
│   Spring Cloud GW   │         JWT doğrulama, routing, CORS
└──────────┬──────────┘
           │
    ┌──────┴──────┐
    │   Eureka    │  :8761  — Service Discovery
    │Config Server│  :8888  — Merkezi konfigürasyon
    │   Zipkin    │  :9411  — Distributed tracing
    └──────┬──────┘
           │
  :8081  :8082  :8083  :8084  :8085  :8086
  User  Product  Cart  Order Payment Notif
   │      │       │      │      │      │
  user_ prod_  Redis  order_ pay_   (DB yok)
  db    db            db     db
           │
      RabbitMQ :5672 (Event Bus)
```

### Servis Portları

| Servis | Port | Gateway Route |
|---|---|---|
| API Gateway | 8080 | — |
| User Service | 8081 | `/api/users/**`, `/api/auth/**` |
| Product Service | 8082 | `/api/products/**`, `/api/categories/**` |
| Cart Service | 8083 | `/api/cart/**` |
| Order Service | 8084 | `/api/orders/**` |
| Payment Service | 8085 | `/api/payments/**` |
| Notification Service | 8086 | (yalnızca event consumer) |
| Eureka | 8761 | — |
| Config Server | 8888 | — |
| Zipkin | 9411 | — |
| RabbitMQ Management | 15672 | — |

---

## Servisler ve Sorumlulukları

### API Gateway (`infrastructure/api-gateway`)

Tüm client isteklerinin tek giriş noktasıdır. Business logic **içermez**.

- JWT imzasını doğrular
- Doğrulanan kullanıcı bilgilerini (`X-User-Id`, `X-User-Role` header) downstream servislere iletir
- CORS, rate limiting ve routing yönetimi
- Downstream servisler JWT parse etmez; yalnızca bu header'lara güvenir

### User Service (`services/user-service`)

Kimlik yönetiminden sorumlu tek servistir.

- Kullanıcı kayıt / giriş
- JWT access token (15 dk) ve refresh token (7 gün) üretimi
- Refresh token hash'i `refresh_tokens` tablosunda saklanır; revoke edilebilir
- Kullanıcı profili CRUD
- Rol yönetimi: `USER`, `ADMIN`
- Sipariş, sepet veya ürün hakkında hiçbir şey bilmez

### Product Service (`services/product-service`)

Katalog ve stok yönetiminin merkezi.

- Ürün ve kategori CRUD (admin yetkisi)
- Sayfalanmış ürün listeleme, filtreleme ve sıralama
- Variant yönetimi (beden, renk gibi tek boyut)
- **Stok rezervasyon, onay ve iptal** — stok mantığı yalnızca bu serviste olur
- `stock_reservations` tablosuyla rezervasyon durumu takibi

### Cart Service (`services/cart-service`)

Kullanıcı sepetini Redis'te yönetir.

- Sepet verisi Redis'te `cart:{userId}` key ile, 30 gün TTL ile saklanır
- Ürün eklenirken **fiyat snapshot** alınır; ürün fiyatı sonradan değişse bile sepetteki fiyat korunur
- Checkout anında fiyatlar Product Service'ten yeniden doğrulanır
- Yalnızca oturum açmış kullanıcılara hizmet verir (guest checkout yok)

### Order Service (`services/order-service`)

Saga Choreography'nin başlatıcısı ve en karmaşık servis.

- Sipariş oluşturur ve durumunu yönetir
- Sipariş anındaki ürün bilgilerini (isim, fiyat, miktar) **snapshot** olarak kendi tablosunda saklar
- Sipariş durumları: `PENDING → STOCK_RESERVED → PAYMENT_REQUESTED → CONFIRMED | CANCELLED`
- Her durum geçişi `order_status_history` tablosunda loglanır
- Event tabanlı saga akışını koordine eder

### Payment Service (`services/payment-service`)

Ödeme işlemlerini Iyzico üzerinden yürütür.

- Yalnızca Iyzico ile konuşur
- Order Service'i tanımaz; `orderReference` (opaque UUID), kullanıcı ID ve tutar yeterlidir
- **Iyzico Checkout Form** kullanılır — kart verisi sisteme girmez, PCI-DSS yükü Iyzico'da
- Ödeme sonuçları event olarak yayınlanır

### Notification Service (`services/notification-service`)

Olay tabanlı bildirim servisi.

- Tüm domain event'lerini dinler
- V1: Gerçek e-posta göndermez, log'a yazar (mock)
- Veritabanı yoktur
- Yeni bildirim kanalı eklemek yalnızca bu servisi etkiler

---

## Neden Bu Teknolojiler?

### Her Servis İçin Ayrı PostgreSQL
Gerçek mikroservis prensibine göre her servis veritabanına bağımsız sahiptir. Bir servisin şeması değiştiğinde diğerleri etkilenmez. Servisler birbirinin veritabanına **asla** doğrudan erişemez.

### Redis (Sepet)
Sepet verisi geçici (ephemeral) niteliktedir. Redis'in TTL desteği, yüksek okuma/yazma performansı ve JSON veri saklama kolaylığı bu kullanım için idealdir. Kullanıcı 30 gün geri dönmezse sepet otomatik silinir.

### RabbitMQ + Choreography Saga
Servisler birbirini bilmez. Sipariş akışı event'ler üzerinden koordine edilir. Bu sayede:
- Bir servis çökerse akış devam edebilir
- Yeni servis eklemek mevcut servisleri değiştirmez
- Distributed transaction yerine eventual consistency benimsenir

**Idempotency**: RabbitMQ "at-least-once delivery" garantisi verir. Aynı event iki kez gelebilir. Her consumer `processed_events` tablosuna bakarak tekrar işlemeyi önler.

### Fiyat Snapshot (Sepet ve Sipariş)
Ürün fiyatı sonradan değişse bile geçmiş sepet ve siparişler etkilenmez. Veri tutarlılığı event zamanındaki değerle garanti altına alınır.

### JWT — Gateway'de Doğrulama
Her servisin JWT parse etmesi tekrar eden kod ve güvenlik açığı riski doğurur. Gateway tek noktada doğrulayıp `X-User-Id` / `X-User-Role` header ekler; downstream servisler bu header'a güvenir.

### Iyzico Checkout Form
Kart verisi hiçbir zaman kendi sistemimize girmez. PCI-DSS uyumluluğu Iyzico'nun sorumluluğundadır.

### Jib (Dockerfile'sız Build)
Maven plugin olarak çalışır, Dockerfile yazmayı gerektirmez. Layer-optimized image üretir, CI sürecinde Docker daemon gerektirmez.

### Hexagonal Architecture
Domain iş mantığı framework'ten bağımsızdır. Spring Boot yerine başka bir framework kullanılsa bile `domain/` ve `application/` katmanları değişmez. Test edilebilirlik artar.

---

## Saga Choreography — Sipariş Akışı

```
[User] → POST /api/orders

[Order Service]
  → Order oluştur (PENDING)
  → PUBLISH: OrderCreated

[Product Service] ← OrderCreated
  → Stok rezervasyonu dene
  ├─ Başarılı → PUBLISH: StockReserved
  └─ Başarısız → PUBLISH: StockReservationFailed

[StockReserved] →
  [Order Service]
    → status = STOCK_RESERVED
    → PUBLISH: PaymentRequested

  [Payment Service] ← PaymentRequested
    → Iyzico checkout başlat
    ├─ Başarılı → PUBLISH: PaymentCompleted
    └─ Başarısız → PUBLISH: PaymentFailed

[PaymentCompleted] → fanout:
  → [Order]   status = CONFIRMED
  → [Product] StockCommitted (stok kalıcı düş)
  → [Cart]    sepeti temizle
  → [Notif]   "Siparişiniz onaylandı" log

[PaymentFailed] → fanout:
  → [Order]   status = CANCELLED
  → [Product] StockReleased (rezervasyonu serbest bırak)
  → [Notif]   "Ödeme başarısız" log

[StockReservationFailed] →
  → [Order]   status = CANCELLED
  → [Notif]   log
```

---

## Event Sistemi (RabbitMQ)

**Exchange**: `ecommerce.events` (Topic Exchange)
**Dead-Letter Exchange**: `ecommerce.events.dlx`

Queue isimlendirme: `{service}.{event}` (örn: `product.order-created`)
Her queue'nun bir DLQ karşılığı vardır. 24 saat işlenmeyen mesaj DLQ'ya düşer.

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

Event sınıfları `shared/common-events` Maven modülünde paylaşılır; her servis bu modüle bağımlıdır.

---

## Güvenlik Mimarisi

### Token Yapısı
- **Access Token**: 15 dakika ömür, stateless, yalnızca imza doğrulanır
- **Refresh Token**: 7 gün ömür, `refresh_tokens` tablosunda hash olarak saklanır, revoke edilebilir

### Public Endpoint'ler (JWT gerektirmez)
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/products/**`

### USER Rolü Gerektirenler
- `/api/cart/**`
- `POST /api/orders`
- `GET /api/orders/{id}`

### ADMIN Rolü Gerektirenler
- `POST/PUT/DELETE /api/products/**`
- `GET /api/orders` (tüm siparişler)

### Akış
1. Client, `Authorization: Bearer <token>` ile istek gönderir
2. API Gateway imzayı doğrular
3. `X-User-Id` ve `X-User-Role` header'larını ekleyerek downstream'e iletir
4. Downstream servisler JWT parse etmez; header'lara güvenir

---

## Veritabanı Tasarımı

### user_db
- `users` — kimlik bilgileri, rol
- `refresh_tokens` — revoke edilebilir refresh token hash'leri

### product_db
- `categories` — hiyerarşik kategori ağacı
- `products` — ürün kataloğu
- `product_variants` — SKU, stok, rezerve stok
- `stock_reservations` — PENDING / COMMITTED / RELEASED durumu
- `processed_events` — idempotency kontrolü

### order_db
- `orders` — sipariş durumu, toplam tutar, teslimat adresi (JSONB)
- `order_items` — anlık ürün/fiyat snapshot'ı
- `order_status_history` — tüm durum geçişleri
- `processed_events` — idempotency kontrolü

### payment_db
- `payments` — orderReference, Iyzico yanıtı (JSONB), durum
- `processed_events` — idempotency kontrolü

### Cart — Redis
```
Key:   cart:{userId}
Value: JSON (items[], priceSnapshot, updatedAt)
TTL:   30 gün (2592000 saniye)
```

---

## Konfigürasyon Yönetimi

### Spring Cloud Config Server (`infrastructure/config-server`)

Tüm servisler konfigürasyonlarını merkezi Config Server'dan alır. Config dosyaları `infrastructure/config-server/src/main/resources/configs/` altında, servis başına YAML dosyaları halinde tutulur.

```
configs/
├── user-service.yml
├── user-service-docker.yml
├── product-service.yml
├── order-service.yml
├── payment-service.yml
├── payment-service-docker.yml
└── ...
```

### Spring Profile Stratejisi

| Profil | Kullanım | URL Formatı |
|---|---|---|
| `local` | Yerel geliştirme | `localhost` |
| `docker` | Docker Compose | Servis ismi (DNS) |
| `prod` | AWS ortamı | Environment variable |

### Servis Başlatma Sırası (local)

```bash
# 1. Altyapı servisleri
docker-compose -f docker-compose.infra.yml up -d

# 2. Spring uygulamaları (sıra önemlidir)
# Config Server    → port 8888
# Discovery Server → port 8761 (Config Server'a bağımlı)
# API Gateway      → port 8080 (Eureka'ya bağımlı)
# Business servisler (herhangi sırada)
```

---

## Proje Yapısı

```
ecommerce-platform/
├── CLAUDE.md                          ← Proje anayasası
├── README.md                          ← Bu dosya
├── docker-compose.yml                 ← Tüm local stack
├── docker-compose.infra.yml           ← Sadece altyapı
├── .github/workflows/
│   ├── ci.yml                         ← Build + Test
│   └── deploy.yml                     ← ECS deploy + Slack
│
├── shared/
│   └── common-events/                 ← Paylaşılan event sınıfları
│
├── infrastructure/
│   ├── api-gateway/
│   ├── discovery-server/              ← Eureka
│   └── config-server/
│       └── src/main/resources/configs/ ← Servis konfigürasyonları
│
├── services/
│   ├── user-service/
│   ├── product-service/
│   ├── cart-service/
│   ├── order-service/
│   ├── payment-service/
│   └── notification-service/
│
└── frontend/
    └── src/
        ├── components/ui/             ← Paylaşılan UI bileşenleri
        ├── features/                  ← Feature-based modüller
        │   ├── auth/
        │   ├── products/
        │   ├── cart/
        │   └── orders/
        ├── hooks/                     ← Custom hooks
        ├── stores/                    ← Zustand store'ları
        ├── lib/axios.ts               ← Axios + JWT interceptor
        └── pages/                     ← Route sayfaları
```

### Her Servisin İç Yapısı (Hexagonal Architecture)

```
src/main/java/com/ecommerce/{service}/
├── domain/                ← Framework'ten bağımsız iş mantığı
│   ├── model/             ← Domain entity'leri (JPA değil)
│   ├── exception/         ← Domain exception'ları
│   └── service/           ← Pure business logic
│
├── application/           ← Use case orchestration
│   ├── usecase/           ← Her senaryo için ayrı sınıf (SRP)
│   ├── port/in/           ← Incoming port interface'leri
│   ├── port/out/          ← Outgoing port interface'leri
│   └── dto/               ← Application DTO'ları
│
├── infrastructure/        ← Framework, DB, dış sistemler
│   ├── persistence/       ← JPA entity, repository, adapter
│   ├── messaging/         ← RabbitMQ publisher ve consumer
│   ├── client/            ← Feign clients
│   └── config/            ← Spring konfigürasyonları
│
└── interfaces/rest/       ← Dışa açılan yüzey
    ├── controller/        ← @RestController (ince, sadece routing)
    ├── dto/               ← Request / Response DTO'ları
    └── mapper/            ← MapStruct DTO ↔ Domain mapper
```

**Bağımlılık kuralı:**
```
interfaces → application → domain
infrastructure → application (port'ları implement eder)
domain hiçbir şeye bağımlı değildir
```

---

## Yerel Geliştirme

### Gereksinimler
- Java 21
- Maven 3.9+
- Docker + Docker Compose
- Node.js 20+ (frontend için)

### Altyapıyı Başlatma

```bash
# PostgreSQL (4 ayrı DB), Redis, RabbitMQ, Zipkin
docker-compose -f docker-compose.infra.yml up -d
```

Servis portları:
- `postgres-user` → 5433
- `postgres-product` → 5434
- `postgres-order` → 5435
- `postgres-payment` → 5436
- Redis → 6379
- RabbitMQ → 5672 / Management UI → 15672
- Zipkin → 9411

### Backend Servislerini Başlatma

```bash
# Sırayla başlatın
cd infrastructure/config-server && mvn spring-boot:run
cd infrastructure/discovery-server && mvn spring-boot:run
cd infrastructure/api-gateway && mvn spring-boot:run

# Business servisler (herhangi sırada)
cd services/user-service && mvn spring-boot:run
cd services/product-service && mvn spring-boot:run
# ...
```

### Frontend Başlatma

```bash
cd frontend
npm install
npm run dev   # http://localhost:5173
```

### API Dokümantasyonu (Swagger UI)

Her servis kendi Swagger UI'ına sahiptir:
- User Service: `http://localhost:8081/swagger-ui.html`
- Product Service: `http://localhost:8082/swagger-ui.html`
- Order Service: `http://localhost:8084/swagger-ui.html`
- Payment Service: `http://localhost:8085/swagger-ui.html`

### Testleri Çalıştırma

```bash
# Tüm servisler için
mvn verify

# Tek servis
cd services/user-service && mvn verify
```

Test katmanları:
- `unit/` — Mock ile domain ve use case testleri
- `integration/` — Testcontainers ile gerçek DB testi
- `architecture/` — ArchUnit ile katman kuralı testleri

Minimum coverage: %70 (line coverage)

---

## CI/CD Pipeline

### ci.yml — Her Push ve PR'da

```
1. Java 21 kurulumu
2. Maven build + test (mvn verify)
3. Test raporu yayınlama
4. Jib ile Docker image build (push olmadan, doğrulama amaçlı)
```

### deploy.yml — main Branch'e Merge Sonrası

```
1. Maven build + test
2. Jib ile Docker image build → ECR'a push
3. ECS service update (rolling deploy)
4. Health check bekleme
5. Slack bildirimi (başarı / hata)
```

---

## AWS Deployment

```
Internet
  → ALB (Application Load Balancer)
    → ECS Fargate Cluster
        API Gateway Task        (:8080)
        User Service Task       (:8081)
        Product Service Task    (:8082)
        Cart Service Task       (:8083) → ElastiCache Redis
        Order Service Task      (:8084)
        Payment Service Task    (:8085)
        Notification Task       (:8086)

RDS:         Her servis için ayrı PostgreSQL
ElastiCache: Redis (Cart Service)
ECR:         Docker image repository
```

---

## API Sözleşmeleri

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

### Paginated Yanıt
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

## Mimari Kararlar Özeti

| Karar | Gerekçe |
|---|---|
| Her servise ayrı PostgreSQL | Tam mikroservis bağımsızlığı |
| Cart için Redis | Ephemeral veri, TTL, yüksek performans |
| Choreography Saga (RabbitMQ) | Servisler birbirini bilmez, decoupled |
| Fiyat snapshot | Geçmiş sipariş/sepet fiyatı değişmez |
| Gateway'de JWT doğrulama | Tekrar eden kod önlenir |
| Iyzico Checkout Form | PCI-DSS yükü dışarıda |
| Guest checkout yok (v1) | Scope sadeliği |
| Notification v1: sadece log | Erken optimizasyondan kaçınma |
| processed_events tablosu | At-least-once delivery idempotency |
