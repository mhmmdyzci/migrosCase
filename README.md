# 🚀 Spaceflight News Android App
**Demo bağlantısı:** [Trailer](https://drive.google.com/file/d/13WLBTD8SJuQyjAXGWLXCVcAk6clbQQdg/view?usp=sharing)

Bu proje, [Spaceflight News API](https://api.spaceflightnewsapi.net/) ile entegre çalışan, güncel uzay haberlerini listeleyen modern bir Android uygulamasıdır.  
Kotlin dilinde yazılmış olup **Clean Architecture** prensiplerine uygun olarak yapılandırılmıştır.

---

## 📱 Özellikler

- Anasayfada son haberlerin listelenmesi
- Favorilere ekleyip çıkarma
- Detay sayfasında başlık, görsel, özet ve paylaşma imkanı
- Arama ve filtreleme
- **Offline destek**: Önceki veriler internet yokken de gösterilir
- **Test coverage**: ViewModel ve Repository katmanları için Unit Test desteği

---

## 🧠 Kullanılan Teknolojiler ve Araçlar

| Katman            | Teknolojiler |
|-------------------|--------------|
| **UI**            | XML, Navigation Component, RecyclerView, ViewBinding |
| **Veri Katmanı**  | Retrofit, Room, DAO, DTO-Entity-Model dönüşümleri |
| **ViewModel**     | Android Architecture Components (LiveData, ViewModel) |
| **Dependency**    | Manual constructor injection |
| **Test**          | JUnit, Coroutine Test, MockK |

---

## 🧪 Testler

- **NewsViewModelTest**: ViewModel'in API ve Room senaryolarına tepkisini test eder.
- **NewsRepositoryImplTest**: Repository katmanının doğru çalıştığını doğrular.

---

## 🧩 Mimarî

Uygulama, **Clean Architecture** prensiplerine göre 3 katmana ayrılmıştır:

- **presentation**: Fragment ve ViewModel katmanı
- **domain**: Modeller ve repository arayüzleri
- **data**: DTO, Entity, Room, Retrofit

---

## 🏗 Agile Yaklaşımı

Proje, Agile prensiplerine uygun şekilde iteratif olarak geliştirilmiştir.

✅ İhtiyaç analizi ve gereksinimlerin tasklara bölünmesi  
✅ Task bazlı feature branch yönetimi (`feature/home-screen`, `feature/detail-screen`, `feature/unit-tests`, vb.)

---

