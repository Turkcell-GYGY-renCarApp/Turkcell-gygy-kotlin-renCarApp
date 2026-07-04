# RenCar Mimari ve Tasarım Kararları

Bu doküman, RenCar araç kiralama mobil uygulamasının geliştirilmesi aşamasında alınan tasarım ve mimari kararlarını içerir.

## 1. Tema ve Renk Kararları

* **Karar:** Android 12+ (API 31) sürümü ile gelen "Dinamik Renk" (dynamicColor) özelliği varsayılan olarak devre dışı bırakılmıştır.
  * **Gerekçe:** Uygulamanın marka kimliğini (RenCar Mavisi ve özel koyu/açık tema renk paleti) tüm cihazlarda tutarlı bir şekilde yansıtmak.
  * **İlgili Dosyalar:**
    * Color.kt
    * Theme.kt

* **Renk Eşleşmeleri:**
  * **Birincil (Primary):** `#0F62CD` (RenCar Mavisi)
  * **Açık Tema Arka Plan:** `#F3F5F9` (Hafif grimsi mavi)
  * **Koyu Tema Arka Plan:** `#0A0F18` (Derin koyu mavi-siyah)

## 2. Navigasyon Yapısı

* **Karar:** Karşılama (Welcome) ve Giriş Yap (Login) ekranları arasındaki geçişler için resmi Jetpack Compose Navigation (`androidx.navigation:navigation-compose`) kütüphanesi entegre edilmiş ve navigasyon yapısı `MainActivity` dışına çıkarılarak `ui/navigation` paketinde ayrıştırılmıştır.
  * **Gerekçe:** Uygulamanın genişleme potansiyelini desteklemek, `MainActivity` dosyasını hafif tutmak ve endüstri standardı olan "Separation of Concerns" (Sorumlulukların Ayrılması) prensibini uygulamak.
  * **İlgili Dosyalar:**
    * RenCarNavHost.kt
    * MainActivity.kt
    * build.gradle.kts (app)
    * libs.versions.toml

## 3. Alt Gezinme Çubuğu ve Harita Ekranı Düzenlemeleri

* **Karar:** Alt gezinme çubuğunun (bottom navigation bar) sistem gezinti çubuğu ile çakışmasını engellemek için `navigationBarsPadding()` eklenmiş ve sabit yükseklik kısıtı kaldırılmıştır. Harita ekranı geçici olarak boş bir ekran (`Box`) ile değiştirilmiştir.
  * **Gerekçe:** Android'in kenardan kenara (edge-to-edge) çizim özelliği nedeniyle alt gezinme çubuğunun kapanması sorununu düzeltmek ve harita ekranını geliştirme aşamasında geçici olarak gizlemek.
  * **İlgili Dosyalar:**
    * RencarBottomNavigationBar.kt
    * MainDashboardScreen.kt

## 4. Tema Tercihinin DataStore ile Kalıcılaştırılması ve Profil Ekranına Taşınması

* **Karar:** Tema değiştirme butonu Karşılama (Welcome) ekranından kaldırılarak yeni oluşturulan Profil ekranındaki "Ayarlar" menü satırına modern bir Switch/seçici ile taşınmıştır. Kullanıcının seçtiği tema durumu (Açık/Koyu) Jetpack DataStore Preferences kullanılarak yerel depolamada kalıcı hale getirilmiştir.
  * **Gerekçe:** Kullanıcı deneyimini iyileştirmek, tema ayarını uygulamanın ayarlar alanına almak ve uygulamanın yeniden başlatılması durumunda kullanıcının tema tercihini korumak (asenkron ve güvenli veri saklama).
  * **İlgili Dosyalar:**
    * ThemePreferenceRepository.kt
    * MainActivity.kt
    * WelcomeScreen.kt
    * ProfileScreen.kt
    * build.gradle.kts (app)
    * libs.versions.toml

## 5. Mimari Yapı (MVI) ve Bağımlılık Enjeksiyonu (Hilt)

* **Karar:** Uygulamanın genel mimarisi MVI (Model-View-Intent) olarak kurgulanmış ve bağımlılıklerin yönetimi için Dagger Hilt kütüphanesi tercih edilmiştir. Tema tercihi veri katmanı repository deseniyle sarmalanmış ve MainActivity üzerinde doğrudan alan enjeksiyonu (Field Injection) ile kullanılmıştır.
  * **Gerekçe:** State yönetimini tek bir kaynaktan (Single Source of Truth) yapmak, kullanıcı etkileşimlerini ve arayüz durumlarını tek yönlü veri akışı ile daha öngörülebilir kılmak, test edilebilirliği artırmak ve uygulama genelindeki bağımlılıkları Hilt ile merkezileştirmek.
  * **İlgili Dosyalar:**
    * RenCarApplication.kt
    * MainActivity.kt
    * ThemePreferenceRepository.kt

## 6. Backend API Entegrasyonu ve JWT Token Yönetimi

* **Karar:** Uygulamanın giriş, kayıt, otp doğrulama, çıkış ve profil servisleri için Retrofit ve OkHttp entegrasyonu yapılmıştır. JWT token'lar (AccessToken ve RefreshToken) ile kullanıcı profil verileri Jetpack DataStore Preferences kullanılarak yerel olarak kalıcı hale getirilmiştir. Korumalı endpoint'lere giden isteklerde token eklenmesi OkHttp Interceptor vasıtasıyla otomatikleştirilmiştir.
  * **Gerekçe:** Güvenli ve tutarlı bir kimlik doğrulama akışı sağlamak, giden API isteklerine JWT token ekleme sorumluluğunu merkezileştirmek ve uygulama kapatılsa dahi oturum durumunun korunmasını garanti altına almak.
  * **İlgili Dosyalar:**
    * AuthPreferencesRepository.kt
    * AuthApi.kt
    * AuthInterceptor.kt
    * NetworkModule.kt

* **Karar:** `openapi.json` spesifikasyonunda `UserResponseDto` altında yer alan `phone` alanı `nullable` olarak tanımlandığı için, Kotlin tarafındaki `UserDto` veri sınıfında ve buna bağlı veri kaydetme (`saveAuthData`) akışlarında `phone` alanı `String?` (nullable) olarak güncellenmiştir.
  * **Gerekçe:** Backend API yanıtlarında telefon numarasının `null` gelme ihtimaline karşı GSON serileştirme/deserileştirme hatalarını ve olası çalışma zamanı çökmelerini önlemek.
  * **İlgili Dosyalar:**
    * AuthDto.kt
    * AuthRepository.kt
    * AuthRepositoryImpl.kt
    * AuthViewModel.kt

* **Karar:** Projedeki veri serileştirme (JSON parsing) altyapısı GSON'dan modern Kotlinx Serialization kütüphanesine taşınmıştır. Retrofit converter entegrasyonu yapılmıştır. Ayrıca `/auth/login` isteğindeki DTO yapıları kullanıcının talebi doğrultusunda `@Serializable` formatında `OtpRequest` ve `OtpResponse` / `OtpResponseData` olarak güncellenmiştir.
  * **Gerekçe:** Android Jetpack Compose ve Kotlin standartlarına daha uygun, derleme zamanı tipleriyle güvenli çalışan modern kotlinx-serialization yapısını kullanmak ve backend tarafındaki yeni veri hiyerarşisine (data wrapper) uyum sağlamak.
  * **İlgili Dosyalar:**
    * build.gradle.kts (project & app)
    * libs.versions.toml
    * AuthDto.kt
    * NetworkModule.kt
    * AuthApi.kt
    * AuthRepository.kt
    * AuthRepositoryImpl.kt
    * AuthViewModel.kt

## 7. Repository Deseni ve AuthModule Entegrasyonu

* **Karar:** Veri işlemlerini soyutlamak ve temiz mimariyi desteklemek amacıyla `data/auth` paketi altında `AuthRepository` arayüzü ve `AuthRepositoryImpl` sınıfı oluşturulmuştur. DTO ve API servis tanımları `AuthDto.kt` ve `AuthApi.kt` isimleriyle bu pakete taşınmıştır. Hilt bağımlılık yönetimi için `di/AuthModule.kt` modülü kurulmuştur.
  * **Gerekçe:** ViewModel katmanının doğrudan Retrofit veya DataStore Preferences bağımlılıklarına sahip olmasını önlemek, test edilebilirliği artırmak ve kimlik doğrulama/kayıt akışını tek bir veri kaynağında konsolide etmek.
  * **İlgili Dosyalar:**
    * AuthDto.kt
    * AuthApi.kt
    * AuthRepository.kt
    * AuthRepositoryImpl.kt
    * AuthModule.kt
    * AuthViewModel.kt

## 8. Ehliyet Doğrulama Akışının Backend Entegrasyonu ve Selfie Adımının Durumu

* **Karar:** Ehliyet doğrulama ekranı için `openapi.json` standartlarına uygun olarak `/license/upload` (POST) ve `/license/status` (GET) uçları entegre edilmiştir. Kullanıcının önizleme yapabilmesi için galeri dosya seçicisi eklenmiştir. Kullanıcı onayı doğrultusunda Selfie doğrulama adımı bir backend API'sine bağlanmadan arayüzde yerel bir placeholder (taslak) adım olarak korunmuştur.
  * **Gerekçe:** OpenAPI spesifikasyonunda selfie yüklemesi için bir endpoint bulunmamakta olup, ehliyet doğrulama akışının sunucu tarafında tamamlanabilmesi için ehliyet görsellerinin yüklenmesi yeterlidir. Kullanıcı deneyimini bozmamak adına selfie adımı görsel bir adım olarak tutulmuştur.
  * **İlgili Dosyalar:**
    * LicenseDto.kt
    * LicenseApi.kt
    * LicenseRepository.kt
    * LicenseRepositoryImpl.kt
    * LicenseViewModel.kt
    * LicenseVerificationScreen.kt
    * ProfileScreen.kt
    * RenCarNavHost.kt

## 9. Giriş, Kayıt, OTP ve Ehliyet Ekranlarının MVI Mimarisine Dönüştürülmesi

* **Karar:** Giriş (Login), Kayıt (Register), OTP Doğrulama (Verify) ve Ehliyet Doğrulama (License) ekranları Model-View-Intent (MVI) mimari desenine uygun şekilde yeniden tasarlanmıştır. Her bir ekran için kendi adıyla ilişkili ayrı kontrat dosyaları (`LoginContract.kt`, `RegisterContract.kt`, `VerifyContract.kt`, `LicenseContract.kt`) `ui/contract` paketi altında oluşturulmuştur. Ekranlar durum yönetimini ViewState (State), eylemlerini ViewIntent (Intent) ve tek seferlik olayları ViewEffect (Effect) yapıları üzerinden yönetmekte; ViewModel'ler bu kontratlara göre güncellenmektedir.
  * **Gerekçe:** State yönetimini tek bir kontrattan yöneterek veri akışını tek yönlü kılmak (Unidirectional Data Flow), ekranların test edilebilirliğini ve önizleme (Preview) kolaylığını artırmak (Stateless/Stateful ekran ayrımı), ve kod sürdürülebilirliğini üst düzeye çıkarmak.
  * **İlgili Dosyalar:**
    * LoginContract.kt, RegisterContract.kt, VerifyContract.kt, LicenseContract.kt
    * LoginViewModel.kt, RegisterViewModel.kt, VerifyViewModel.kt, LicenseViewModel.kt
    * LoginScreen.kt, RegisterScreen.kt, VerifyScreen.kt, LicenseVerificationScreen.kt
    * RenCarNavHost.kt