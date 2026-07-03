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