# core-infrastructure

Bu paket, mikroservislerde ortak kullanılan güvenilirlik bileşenlerini içerir:
- `idempotency` (tekrarlanan event/istekleri tekilleştirme),
- `outbox` (DB yazımı ile event yayımını güvenli ayırma),
- `after-commit` yardımcıları (ack/publish işlemlerini commit sonrasına taşıma).

## E-commerce için neyi çözer?

Bu kütüphane özellikle event-driven e-commerce akışlarında ortaya çıkan şu problemleri çözmek için tasarlanmıştır:

- sipariş onayı event'i iki kez gelirse stok veya ödeme işleminin ikinci kez çalışmasını engellemek,
- iş verisi DB'ye yazılmışken Kafka publish başarısız olursa veriyi kaybetmemek,
- Kafka mesajını çok erken `ack` verip, DB transaction rollback olduğunda veri tutarsızlığı oluşturmamak,
- order, inventory ve payment gibi servislerde aynı güvenilirlik mantığını tekrar tekrar yazmamak.

Örnek kullanım alanları:

- `order-service`: sipariş onayı sonrası outbox kaydı oluşturup event'i güvenli şekilde publish etmek,
- `inventory-service`: aynı `OrderConfirmedEvent` tekrar gelse bile stok düşümünü tek sefer yapmak,
- `payment-service`: duplicate ödeme event'lerinde aynı tahsilat etkisini ikinci kez oluşturmamak,
- `product-listing-service` ve `user-service`: ortak request-id, exception handling ve web altyapısını paylaşmak.

Kısacası bu paket, e-commerce sistemlerinde sık görülen `duplicate event`, `lost publish`, `early ack`, `cross-service reliability` problemlerini ortak ve tekrar kullanılabilir şekilde çözmek için konumlanır.

## Idempotentlik (DB seviyesi garanti)

İdempotentlik sadece uygulama belleğinde değil, **veritabanı seviyesinde** sağlanır.

Temel yaklaşım:
1. `@Idempotent` aspect'i çalışır.
2. `processed_event` tablosuna `event_id` ile `insert ... on conflict do nothing` yapılır.
3. Insert başarılıysa işlem devam eder, başarısızsa event duplicate kabul edilir.

Bu sayede:
- aynı event 2, 3, 10 kez gelse bile tek bir tanesi işlenir,
- pod restart olsa da tekilleştirme kaybolmaz,
- kontrol merkezi DB olduğu için çoklu instance senaryolarında da tutarlıdır.

## Neden Redis yerine DB tabanlı idempotentlik?

Redis tabanlı idempotentlik yaklaşımı (ör. Trendyol `Jdempotent`) e-commerce akışları için pratik ve doğrudur.
Özellikle yüksek throughput ve düşük gecikme ihtiyaçlarında güçlü bir seçenektir.

Bu projede tercih edilen yaklaşım DB tabanlıdır; nedeni:
- geliştirme ve çalışma ortamına ek bir Redis container/operasyon yükü eklememek,
- mevcut PostgreSQL ile tekilleştirmeyi kalıcı ve merkezi şekilde yönetmek,
- idempotent kayıtlarını iş verisine yakın tutarak operasyonu sadeleştirmek.

Özet: Redis yaklaşımı yanlış değil, burada mimari sadeleşme ve ek altyapı istememe nedeniyle DB yaklaşımı seçilmiştir.

## Outbox (DB odaklı güvenli yayın)

İş kuralı transaction'ı içinde önce `outbox_event` tablosuna kayıt atılır (`NEW`).
Transaction commit olduktan sonra outbox worker bu kaydı Kafka'ya yayınlar.

Bu modelin amacı:
- iş verisi + yayın niyetini aynı DB transaction içinde kalıcı yapmak,
- anlık Kafka hatasında iş verisini kaybetmemek,
- publish başarısına göre outbox statüsünü (`SENT` / `FAILED`) güncelleyebilmek.

## ACK davranışı

Kafka tüketiminde `ack` işlemi commit sonrası yapılır:
- duplicate ise kısa devre edilir ve güvenli şekilde ack verilir,
- normal akışta iş kuralı + DB commit tamamlandıktan sonra ack verilir.

## Servis tarafı kullanım örneği
```java
@Idempotent(key = "#event.eventId()", eventType = "'OrderConfirmedEvent'", orderId = "#event.orderId()")
@Transactional
public void handle(Event event, Runnable ackAfterCommit) {
    if (IdempotencyContext.isDuplicate()) {
        AfterCommitExecutor.run(ackAfterCommit);
        return;
    }

    // business logic...

    AfterCommitExecutor.run(ackAfterCommit);
}
```

## Yol Haritası

Bu pakete, tablo büyümesini kontrol etmek için **periyodik cleanup job** eklenecektir:
- `processed_event` için retention bazlı silme/arşivleme,
- `outbox_event` için eski `SENT/FAILED` kayıtların temizlenmesi,
- batch ve zaman penceresi ayarlarıyla güvenli bakım.

Not: `IdempotencyContext` `ThreadLocal` kullanır ve aspect tarafından temizlenir.

## Okunması Gereken Yazılar

- [Trendyol Jdempotent](https://github.com/Trendyol/Jdempotent)
- [Saga, Outbox and Rate Limiter in Microservices – Why and How (Yapı Kredi Teknoloji)](https://medium.com/yapi-kredi-teknoloji/saga-outbox-and-rate-limiter-in-microservices-why-and-how-b9decf547f3d)
- [Transactional Outbox Pattern (microservices.io)](https://microservices.io/patterns/data/transactional-outbox.html)
