# Bird Watching API

Bu proje, kuş gözlemcileri için geliştirilmiş bir backend API'dir. Kullanıcılar kuş türlerini kaydedebilir, gözlemlerini paylaşabilir ve diğer gözlemcilerle etkileşimde bulunabilir.

## Özellikler

- Kullanıcı yönetimi (kayıt, giriş, JWT tabanlı kimlik doğrulama)
- Kuş türleri yönetimi (ekleme, düzenleme, silme, listeleme)
- Gözlem kayıtları (konum bazlı gözlem kayıtları)
- Dosya yükleme desteği (kuş fotoğrafları için)
- MongoDB veritabanı entegrasyonu

## Teknolojiler

- Kotlin
- Ktor Framework
- MongoDB
- JWT Authentication
- Gradle

## Kurulum

1. Projeyi klonlayın:
```bash
git clone https://github.com/[YOUR_USERNAME]/bird-watching-api.git
```

2. MongoDB'yi kurun ve başlatın:
```bash
# MongoDB'yi başlatın
mongod
```

3. Projeyi çalıştırın:
```bash
./gradlew run
```

## API Endpoint'leri

### Kullanıcı İşlemleri
- `POST /api/users/register` - Yeni kullanıcı kaydı
- `POST /api/users/login` - Kullanıcı girişi
- `GET /api/users/{id}` - Kullanıcı bilgilerini görüntüleme

### Kuş Türleri
- `GET /api/birds` - Tüm kuş türlerini listeleme
- `GET /api/birds/{id}` - Belirli bir kuş türünü görüntüleme
- `POST /api/birds` - Yeni kuş türü ekleme (JWT gerekli)
- `PUT /api/birds/{id}` - Kuş türü güncelleme (JWT gerekli)
- `DELETE /api/birds/{id}` - Kuş türü silme (JWT gerekli)

### Gözlemler
- `GET /api/observations` - Tüm gözlemleri listeleme (JWT gerekli)
- `POST /api/observations` - Yeni gözlem ekleme (JWT gerekli)
- `PUT /api/observations/{id}` - Gözlem güncelleme (JWT gerekli)
- `DELETE /api/observations/{id}` - Gözlem silme (JWT gerekli)

### Dosya İşlemleri
- `POST /api/files/upload` - Dosya yükleme (JWT gerekli)
- `GET /api/files/download/{fileName}` - Dosya indirme (JWT gerekli)
- `DELETE /api/files/{fileName}` - Dosya silme (JWT gerekli)

## Geliştirme

1. Projeyi fork'layın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add some amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

## Lisans

Bu proje MIT lisansı altında lisanslanmıştır. Daha fazla bilgi için `LICENSE` dosyasına bakın. 