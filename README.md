# Bird Watching API

Bu proje, kuş gözlem kayıtlarını tutmak için geliştirilmiş bir REST API'dir.

## Teknolojiler

- Kotlin
- Ktor Framework
- MongoDB
- JWT Authentication

## Özellikler

- Kullanıcı yönetimi (kayıt, giriş)
- Kuş türleri yönetimi
- Gözlem kayıtları
- Dosya yükleme sistemi
- JWT tabanlı kimlik doğrulama

## Kurulum

1. MongoDB'yi yükleyin ve başlatın
2. Projeyi klonlayın:
```bash
git clone [repository-url]
```
3. Projeyi çalıştırın:
```bash
./gradlew run
```

## API Endpoint'leri

### Kullanıcı İşlemleri
- `POST /api/users/register` - Yeni kullanıcı kaydı
- `POST /api/users/login` - Kullanıcı girişi

### Kuş Türleri
- `GET /api/birds` - Tüm kuş türlerini listele
- `GET /api/birds/{id}` - Belirli bir kuş türünü görüntüle
- `POST /api/birds` - Yeni kuş türü ekle
- `PUT /api/birds/{id}` - Kuş türü güncelle
- `DELETE /api/birds/{id}` - Kuş türü sil

### Gözlemler
- `GET /api/observations` - Tüm gözlemleri listele
- `POST /api/observations` - Yeni gözlem ekle
- `PUT /api/observations/{id}` - Gözlem güncelle
- `DELETE /api/observations/{id}` - Gözlem sil

### Dosya İşlemleri
- `POST /api/files/upload` - Dosya yükle
- `GET /api/files/download/{fileName}` - Dosya indir
- `DELETE /api/files/{fileName}` - Dosya sil

## Geliştirme

Projeyi geliştirmek için:

1. Fork'layın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add some amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun 