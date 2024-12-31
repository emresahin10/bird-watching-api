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

## API Dokümantasyonu

### Kullanıcı İşlemleri

#### Kullanıcı Kaydı
```http
POST /api/users/register
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "password123",
    "name": "John Doe"
}
```

Başarılı Yanıt:
```json
{
    "id": "user_id",
    "email": "user@example.com",
    "name": "John Doe"
}
```

#### Kullanıcı Girişi
```http
POST /api/users/login
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "password123"
}
```

Başarılı Yanıt:
```json
{
    "token": "JWT_TOKEN",
    "user": {
        "id": "user_id",
        "email": "user@example.com",
        "name": "John Doe"
    }
}
```

### Kuş Türleri

#### Tüm Kuşları Listele
```http
GET /api/birds
Authorization: Bearer JWT_TOKEN

# Opsiyonel Query Parametreleri:
# name: Kuş adına göre filtreleme
# Örnek: /api/birds?name=Serçe
```

Başarılı Yanıt:
```json
[
    {
        "id": "bird_id",
        "name": "Serçe",
        "scientificName": "Passer domesticus",
        "habitat": ["Şehir", "Kırsal"],
        "photoUrl": "http://example.com/photos/bird.jpg",
        "description": "Yaygın görülen küçük bir kuş türü",
        "createdAt": "2024-01-20T10:00:00Z",
        "updatedAt": "2024-01-20T10:00:00Z"
    }
]
```

#### Belirli Bir Kuşu Görüntüle
```http
GET /api/birds/{id}
Authorization: Bearer JWT_TOKEN
```

#### Yeni Kuş Ekle
```http
POST /api/birds
Authorization: Bearer JWT_TOKEN
Content-Type: application/json

{
    "name": "Serçe",
    "scientificName": "Passer domesticus",
    "habitat": ["Şehir", "Kırsal"],
    "photoUrl": "http://example.com/photos/bird.jpg",
    "description": "Yaygın görülen küçük bir kuş türü"
}
```

#### Kuş Bilgilerini Güncelle
```http
PUT /api/birds/{id}
Authorization: Bearer JWT_TOKEN
Content-Type: application/json

{
    "name": "Serçe",
    "scientificName": "Passer domesticus",
    "habitat": ["Şehir", "Kırsal"],
    "photoUrl": "http://example.com/photos/bird.jpg",
    "description": "Güncellenen açıklama"
}
```

#### Kuş Kaydını Sil
```http
DELETE /api/birds/{id}
Authorization: Bearer JWT_TOKEN
```

### Gözlemler

#### Tüm Gözlemleri Listele
```http
GET /api/observations
Authorization: Bearer JWT_TOKEN

# Opsiyonel Query Parametreleri:
# userId: Kullanıcıya göre filtreleme
# birdId: Kuş türüne göre filtreleme
# Örnek: /api/observations?userId=user_id&birdId=bird_id
```

Başarılı Yanıt:
```json
[
    {
        "id": "observation_id",
        "userId": "user_id",
        "birdId": "bird_id",
        "location": {
            "latitude": 41.0082,
            "longitude": 28.9784
        },
        "date": "2024-01-20T10:00:00Z",
        "notes": "Sabah saatlerinde gözlemlendi",
        "photoUrl": "http://example.com/photos/observation.jpg",
        "createdAt": "2024-01-20T10:00:00Z",
        "updatedAt": "2024-01-20T10:00:00Z"
    }
]
```

#### Yeni Gözlem Ekle
```http
POST /api/observations
Authorization: Bearer JWT_TOKEN
Content-Type: application/json

{
    "birdId": "bird_id",
    "location": {
        "latitude": 41.0082,
        "longitude": 28.9784
    },
    "date": "2024-01-20T10:00:00Z",
    "notes": "Sabah saatlerinde gözlemlendi",
    "photoUrl": "http://example.com/photos/observation.jpg"
}
```

#### Gözlem Bilgilerini Güncelle
```http
PUT /api/observations/{id}
Authorization: Bearer JWT_TOKEN
Content-Type: application/json

{
    "location": {
        "latitude": 41.0082,
        "longitude": 28.9784
    },
    "date": "2024-01-20T10:00:00Z",
    "notes": "Güncellenen gözlem notu",
    "photoUrl": "http://example.com/photos/observation.jpg"
}
```

#### Gözlem Kaydını Sil
```http
DELETE /api/observations/{id}
Authorization: Bearer JWT_TOKEN
```

### Dosya İşlemleri

#### Dosya Yükleme
```http
POST /api/files/upload
Authorization: Bearer JWT_TOKEN
Content-Type: multipart/form-data

# Form parametresi:
# file: Yüklenecek dosya (maksimum 2MB)
# İzin verilen formatlar: jpg, jpeg, png, gif
# Tek seferde maksimum 5 dosya yüklenebilir
```

Başarılı Yanıt:
```json
{
    "files": [
        {
            "fileName": "550e8400-e29b-41d4-a716-446655440000.jpg",
            "fileUrl": "http://localhost:8080/api/files/view/550e8400-e29b-41d4-a716-446655440000.jpg",
            "status": "success"
        }
    ]
}
```

#### Dosya Görüntüleme (Public Endpoint)
```http
GET /api/files/view/{fileName}
```

#### Dosya İndirme
```http
GET /api/files/download/{fileName}
Authorization: Bearer JWT_TOKEN
```

#### Dosya Silme
```http
DELETE /api/files/{fileName}
Authorization: Bearer JWT_TOKEN
```

## Hata Yanıtları

API aşağıdaki hata formatında yanıt verir:
```json
{
    "error": "Hata mesajı"
}
```

Genel HTTP Durum Kodları:
- 200: Başarılı
- 201: Başarılı oluşturma
- 204: Başarılı silme
- 400: Geçersiz istek
- 401: Yetkisiz erişim
- 404: Bulunamadı
- 500: Sunucu hatası

## Geliştirme

Projeyi geliştirmek için:

1. Fork'layın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Değişikliklerinizi commit edin (`git commit -m 'Add some amazing feature'`)
4. Branch'inizi push edin (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun 