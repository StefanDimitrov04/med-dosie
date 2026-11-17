# Документация — Медицинско досие (CSCB869)

## Съдържание
1. [Обща архитектура](#1-обща-архитектура)
2. [Java / Spring анотации — речник](#2-java--spring-анотации--речник)
3. [Слой Data — Entities](#3-слой-data--entities)
4. [Слой Data — Repositories](#4-слой-data--repositories)
5. [DTO класове](#5-dto-класове)
6. [Слой Service](#6-слой-service)
7. [View Controllers](#7-view-controllers)
8. [API Controllers](#8-api-controllers)
9. [Сигурност (Spring Security)](#9-сигурност-spring-security)
10. [Инфраструктура](#10-инфраструктура)
11. [Поток на заявките — примери](#11-поток-на-заявките--примери)

---

## 1. Обща архитектура

Приложението следва класическата **многослойна архитектура** (Layered Architecture):

```
Browser / REST клиент
        │
        ▼
   Controllers  (web/view  +  web/api)
        │
        ▼
    Services    (service/impl)
        │
        ▼
  Repositories  (data/repo)
        │
        ▼
    Database    (MySQL)
```

- **View Controllers** (`/examinations`, `/patients`, …) — обработват HTTP заявки от браузъра и връщат HTML чрез **Thymeleaf** шаблони.
- **API Controllers** (`/api/examinations`, …) — обработват AJAX заявки и връщат **JSON**.
- **Services** — съдържат бизнес логиката. Контролерите не пишат директно в базата.
- **Repositories** — Spring Data JPA интерфейси, генерират SQL автоматично.
- **Entities** — Java класове, наредени директно към таблици в MySQL с **JPA/Hibernate**.

**Технологии:**
| Технология | Версия | Роля |
|---|---|---|
| Spring Boot | 3.3.4 | Рамка за приложението |
| Spring MVC | (включено) | HTTP контролери |
| Spring Security | 6 | Автентикация и оторизация |
| Spring Data JPA | (включено) | Работа с база данни |
| Hibernate | (включено) | ORM — обекти ↔ таблици |
| Thymeleaf | (включено) | HTML шаблони |
| MySQL | 8 | База данни |
| Lombok | (включено) | Генерира boilerplate код |
| ModelMapper | (включено) | Копира данни между Entity и DTO |

---

## 2. Java / Spring анотации — речник

### Основни Spring анотации

**`@SpringBootApplication`**
Стартова анотация. Поставя се само на главния клас (`Csb869MedicinskoDosieApplication`). Обединява три анотации:
- `@Configuration` — класът дефинира beans
- `@EnableAutoConfiguration` — Spring Boot автоматично конфигурира всичко (Tomcat, JPA, Security…)
- `@ComponentScan` — сканира пакета и подпакетите за компоненти

**`@Component`**
Казва на Spring: „Създай обект (bean) от този клас и го управлявай." `@Service`, `@Controller`, `@Repository` са специализирани варианти.

**`@Service`**
Специализиран `@Component` за бизнес логика. Spring го открива автоматично. Няма техническа разлика с `@Component`, но изяснява намерението.

**`@Repository`**
Специализиран `@Component` за достъп до данни. Spring Data JPA не изисква тази анотация на интерфейсите, но я поддържа.

**`@Configuration`**
Класът съдържа конфигурационни `@Bean` методи. Spring го изпълнява при стартиране.

**`@Bean`**
Метод, отбелязан с `@Bean`, връща обект, който Spring ще управлява. Пример: `BCryptPasswordEncoder` и `ModelMapper` се дефинират така:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**`@RequiredArgsConstructor`** *(Lombok)*
Генерира конструктор с всички `final` полета като параметри. Spring вижда единствения конструктор и автоматично "инжектира" нужните beans (Dependency Injection). Заменя `@Autowired`.

**`@Autowired`**
Алтернатива на `@RequiredArgsConstructor`. Казва на Spring да намери подходящ bean и да го постави в полето. По-рядко се използва в съвременен Spring код.

---

### HTTP анотации (Spring MVC)

**`@Controller`**
Класът е MVC контролер. Методите му обработват HTTP заявки и могат да върнат имена на Thymeleaf шаблони (стрингове).

**`@RestController`**
Комбинация от `@Controller` + `@ResponseBody`. Всеки метод автоматично сериализира върнатия обект като JSON. Използва се за API контролерите.

**`@RequestMapping("/path")`**
Поставя се на клас. Задава базовия URL префикс за всички методи в класа.

**`@GetMapping`**
Метод, обработващ HTTP GET заявка (отваряне на страница, извличане на данни). Приема `value = "/path"` за подпътя.

**`@PostMapping`**
Метод, обработващ HTTP POST заявка (изпращане на форма, създаване на ресурс).

**`@PutMapping`**
HTTP PUT — обновяване на целия ресурс. Използва се в API контролерите.

**`@DeleteMapping`**
HTTP DELETE — изтриване на ресурс.

**`@PathVariable`**
Взима стойност от URL-а. Пример: URL `/examinations/5/edit` → параметър `@PathVariable Long id` получава `5`.

**`@RequestParam`**
Взима стойност от query string-а. Пример: URL `/statistics?diagnosisId=3` → `@RequestParam(required = false) Long diagnosisId` получава `3`. `required = false` позволява параметърът да липсва.

**`@RequestBody`**
Десериализира JSON от тялото на заявката към Java обект. Използва се в API контролерите.

**`@ResponseBody`**
(включено в `@RestController`) Казва на Spring да сериализира върнатото Java обекта директно в HTTP отговора като JSON, вместо да търси Thymeleaf шаблон.

**`@ModelAttribute("name")`**
Свързва параметъра на метода с обект от Thymeleaf формата. Когато е на параметър: попълва DTO от POST данните на формата. Когато е на метод: добавя атрибут в модела за всяко извикване на контролера.

---

### Валидация

**`@Valid`**
Казва на Spring да изпълни валидационните правила (`@NotBlank`, `@Pattern`, …) върху обекта преди метода да се изпълни. При грешки се попълва `BindingResult`.

**`@NotBlank`**
Полето не може да е null, празен стринг или само интервали.

**`@NotNull`**
Полето не може да е null (но може да е 0 или празен стринг).

**`@Size(min, max)`**
Минимална/максимална дължина на стринг или колекция.

**`@Min(value)`**
Числото трябва да е поне `value`.

**`@Pattern(regexp)`**
Стрингът трябва да отговаря на регулярния израз. **Важно:** `@Pattern` прескача null стойности — не дава грешка ако полето е null. Затова се прилага отделна ръчна проверка в контролера за задължителни полета.

**`BindingResult`**
Параметър, в който Spring складира всички валидационни грешки. Трябва да следва директно след `@Valid` параметъра. Методът проверява `br.hasErrors()` — ако има грешки, връща формата обратно с грешките показани. `br.rejectValue("fieldName", "code", "message")` добавя ръчна грешка към конкретно поле.

---

### JPA / Hibernate анотации

**`@Entity`**
Класът е JPA entity — съответства на таблица в базата данни.

**`@Table(name = "table_name")`**
Задава името на таблицата. Ако липсва, Hibernate използва името на класа.

**`@Id`**
Полето е primary key на таблицата.

**`@GeneratedValue(strategy = GenerationType.IDENTITY)`**
Стойността на primary key се генерира автоматично от базата данни (AUTO_INCREMENT в MySQL).

**`@Column(unique = true, nullable = false)`**
Конфигурира колоната — уникалност, null стойности, дължина и т.н.

**`@ManyToOne`**
Релация много-към-едно. Пример: много `Examination` записа принадлежат на един `Doctor`.

**`@OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)`**
Релация един-към-много. `mappedBy` сочи към полето в другия клас. `cascade = ALL` означава: при запис/изтриване на родителя, същото се прави и с децата. `orphanRemoval = true` — ако дете се премахне от колекцията, се изтрива от базата.

**`@ManyToMany`**
Релация много-към-много. Изисква junction таблица.

**`@JoinTable`**
Конфигурира junction таблицата за `@ManyToMany`. Задава нейното име и имената на foreign key колоните.

**`@JoinColumn(name = "column_name")`**
Задава името на foreign key колоната.

**`@JsonIgnore`**
Jackson анотация. Предотвратява сериализацията на полето в JSON. Използва се на `@OneToMany` колекции за да се избегне безкраен цикъл при сериализация (Doctor → Examinations → Doctor → …).

**`@MappedSuperclass`**
Базов клас, чиито полета се наследяват от entity класовете, но самият той няма таблица.

---

### Lombok анотации

**`@Getter` / `@Setter`**
Генерира getter/setter методи за всички полета.

**`@NoArgsConstructor`**
Генерира конструктор без параметри (изисква се от JPA).

**`@AllArgsConstructor`**
Генерира конструктор с всички полета като параметри.

**`@Builder`**
Генерира Builder паттерн — позволява: `Doctor.builder().name("Иванов").speciality("Кардиология").build()`.

---

### Spring Security анотации

**`@EnableWebSecurity`**
Активира Spring Security конфигурацията.

**`@EnableMethodSecurity(securedEnabled = true)`**
Активира `@Secured` анотациите по методите.

**`@Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})`**
Методът е достъпен само за потребители с поне една от изброените роли. Ако потребителят няма роля, Spring хвърля `AccessDeniedException` (403 Forbidden).

---

### Транзакции

**`@Transactional`**
Всички операции с базата данни в метода се изпълняват като една транзакция. Ако нещо се провали на средата, цялото се отменя (rollback). Пример: `registerPublic` — ако потребителят се запише, но пациентът се провали, потребителят също не се запазва.

---

## 3. Слой Data — Entities

Намират се в `data/entity/`. Всеки entity клас отговаря на таблица в MySQL.

### `BaseEntity`
```
Таблица: (базов клас, няма собствена таблица)
Полета: id (Long, AUTO_INCREMENT primary key)
```
Всички entity класове го наследяват. Благодарение на `@MappedSuperclass`, полето `id` е налично навсякъде без повторение.

---

### `Role`
```
Таблица: roles
Полета: id, name (уникално — "ADMIN", "DOCTOR", "PATIENT")
```
Представя роля в системата. Потребителят може да има много роли (`@ManyToMany` с `User`).

---

### `User`
```
Таблица: users
Полета: id, username (уникално), password (BCrypt хеш)
Релации:
  - @ManyToMany(EAGER) roles → junction таблица user_roles
  - @OneToOne doctor (nullable — само ако е DOCTOR)
  - @OneToOne patient (nullable — само ако е PATIENT)
```
Представя акаунта за вход. `EAGER` при ролите означава: при всяко зареждане на User, ролите се зареждат веднага (не lazy). Нужно е, защото Spring Security ги иска при всяка заявка.

---

### `Doctor`
```
Таблица: doctors
Полета: id, identificationNumber (уникално), name, speciality, isGeneralPractitioner (boolean)
Релации:
  - @OneToMany examinations (@JsonIgnore)
  - @OneToMany patients (patients.generalPractitioner) (@JsonIgnore)
```
`isGeneralPractitioner = true` означава, че докторът е личен лекар (GP) и може да се задава на пациент.

---

### `Patient`
```
Таблица: patients
Полета: id, name, egn (уникално, 10 цифри, nullable), healthInsured (boolean)
Релации:
  - @ManyToOne generalPractitioner → Doctor (nullable)
  - @OneToMany examinations (CASCADE ALL, @JsonIgnore)
```
`healthInsured` определя дали прегледите се плащат от пациента (`paidByPatient = !healthInsured`).

---

### `Examination`
```
Таблица: examinations
Полета: id, date (LocalDate), treatment (String), price (BigDecimal)
Релации:
  - @ManyToOne doctor
  - @ManyToOne patient
  - @ManyToMany diagnoses → junction таблица examination_diagnoses
  - @OneToMany sickLeaves (CASCADE ALL, orphanRemoval=true)
```
Централният entity. Свързва доктор, пациент и диагнози. `orphanRemoval=true` на болничните листа означава: ако болничен лист се премахне от колекцията на прегледа, се изтрива автоматично.

---

### `SickLeave`
```
Таблица: sick_leaves
Полета: id, startDate (LocalDate), durationDays (int, мин. 1)
Релации:
  - @ManyToOne examination
```
Болничен лист, прикачен към преглед.

---

### `Diagnosis`
```
Таблица: diagnoses
Полета: id, name, description
```
Диагноза — може да се асоциира с много прегледи.

---

## 4. Слой Data — Repositories

Намират се в `data/repo/`. Всеки е Java интерфейс, разширяващ `JpaRepository<EntityType, Long>`. Spring Data JPA автоматично генерира имплементация при стартиране.

**`JpaRepository` предоставя безплатно:**
- `save(entity)` — запис/обновяване
- `findById(id)` — намиране по ID, връща `Optional<T>`
- `findAll()` — всички записи
- `deleteById(id)` — изтриване
- `count()` — брой записи
- и много други

**Именувани методи** — Spring Data JPA разбира метода от неговото име:

```java
Optional<User> findByUsername(String username);
// генерира: SELECT * FROM users WHERE username = ?

List<Examination> findByDoctorId(Long doctorId);
// генерира: SELECT * FROM examinations WHERE doctor_id = ?

List<Examination> findByDoctorIdAndDateBetween(Long doctorId, LocalDate from, LocalDate to);
// генерира: SELECT * FROM examinations WHERE doctor_id = ? AND date BETWEEN ? AND ?
```

**JPQL заявки** — когато именуваният метод не е достатъчен, се пише заявка с `@Query`:

```java
@Query("SELECT e.doctor, SUM(e.price) FROM Examination e WHERE e.patient.healthInsured = false GROUP BY e.doctor")
List<Object[]> sumPricePaidByPatientsGroupedByDoctor();
// Object[] = { Doctor entity, BigDecimal sum }
```

**Native SQL** — за специфични MySQL функции:

```java
@Query(value = "SELECT d.name FROM diagnoses d JOIN examination_diagnoses ed ...", nativeQuery = true)
String findMostCommonDiagnosis();
```

### Всички repositories и техните специфични методи:

| Repository | Специфични методи |
|---|---|
| `UserRepository` | `findByUsername(String)` |
| `DoctorRepository` | `findByIdentificationNumber`, `findByIsGeneralPractitionerTrue`, JPQL `findGPsWithPatientCount` (Object[]{Doctor, count}) |
| `PatientRepository` | `findByEgn`, `findByGeneralPractitionerId`, JPQL `findByDiagnosisId`, JPQL `findByDiagnosisName` |
| `ExaminationRepository` | `findByPatientId`, `findByDoctorId`, `findByDoctorIdAndDateBetween`, `findByDateBetween`, JPQL `sumPricePaidByPatients`, JPQL `sumPricePaidByPatientsGroupedByDoctor`, JPQL `countVisitsPerDoctor` |
| `SickLeaveRepository` | `findByExaminationId`, JPQL `findMonthWithMostSickLeaves`, JPQL `findDoctorsWithMostSickLeaves` |
| `DiagnosisRepository` | `findByName`, native `findMostCommonDiagnosis` |
| `RoleRepository` | `findByName(String)` |

---

## 5. DTO класове

**DTO (Data Transfer Object)** — обекти за пренос на данни между слоевете. Не са entity класове — нямат `@Entity` и не се пазят директно в базата. Причините за тяхното използване:
- Да не се излагат вътрешни entity полета пред клиента
- Да се добавят изчислени полета (напр. `doctorName`, `paidByPatient`)
- Да се дефинират валидационни правила специфични за формата

Всички са Lombok `@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder`.

### `ExaminationDTO`
Полета: `id, date, doctorId, doctorName, patientId, patientName, diagnosisIds (Set<Long>), diagnoses (Set<DiagnosisDTO>), treatment, price, paidByPatient`

`paidByPatient` е изчислено поле — не съществува в entity. Зависи от `patient.healthInsured`: ако пациентът не е осигурен, плаща от джоба си.

### `PatientDTO`
Полета: `id, name, egn, generalPractitionerId (@NotNull), generalPractitionerName, healthInsured`

### `DoctorDTO`
Полета: `id, identificationNumber, name, speciality, isGeneralPractitioner`

### `SickLeaveDTO`
Полета: `id, startDate, durationDays (@Min(1)), examinationId (@NotNull), patientName, doctorName`

### `DiagnosisDTO`
Полета: `id, name, description`

### `PublicRegisterDTO`
Полета: `username (@NotBlank, 3-50 символа), password (@NotBlank, мин. 4), role, name (@NotBlank, 2-100), egn (@Pattern "^(\d{10})?$"), generalPractitionerId, identificationNumber, speciality, existingDoctorId`

Използва се в публичната форма за регистрация. EGN regex `^(\d{10})?$` — позволява null/празен стринг (за DOCTOR) или точно 10 цифри.

### `RegisterDTO`
Полета: `username, password, role, doctorId, patientId`

Само за ADMIN — свързва съществуващ Doctor/Patient профил с нов User акаунт.

---

## 6. Слой Service

Намира се в `service/` (интерфейси) и `service/impl/` (имплементации). Съдържа бизнес логиката.

**Защо интерфейс + имплементация?**
Spring препоръчва програмиране към интерфейс — контролерите зависят от `UserService`, не от `UserServiceImpl`. Това позволява лесна замяна (тестове, различни имплементации).

---

### `UserServiceImpl`
Имплементира `UserService` + `UserDetailsService` (Spring Security интерфейс).

**`loadUserByUsername(String username)`**
Извиква се от Spring Security при всеки login. Зарежда User от базата, преобразува ролите му в `GrantedAuthority` обекти с префикс `ROLE_`. Връща `UserDetails` — стандартен Spring Security обект.

**`register(RegisterDTO dto)`**
Използва се само от ADMIN чрез `UserViewController`. Създава User и го свързва с вече съществуващ Doctor или Patient по ID.

**`registerPublic(PublicRegisterDTO dto)` `@Transactional`**
Използва се от публичната форма. В зависимост от ролята:
- PATIENT → създава нов `Patient` entity, запазва го, свързва с User
- DOCTOR → създава нов `Doctor` entity (или свързва съществуващ), запазва, свързва с User

`@Transactional` гарантира, че ако нещо се провали (напр. дублирано EGN), нищо не се запазва в базата.

---

### `ExaminationServiceImpl`

**`getAllExaminations()`** — всички прегледи

**`getExaminationById(Long id)`** — един преглед по ID; хвърля `ResourceNotFoundException` ако не съществува

**`getExaminationsByPatient(Long patientId)`** — прегледи на конкретен пациент

**`getExaminationsByDoctor(Long doctorId)`** — прегледи направени от конкретен доктор

**`getExaminationsByDoctorAndPeriod(Long doctorId, LocalDate from, LocalDate to)`** — прегледи на доктор в период

**`createExamination(ExaminationDTO dto)`**
Вика `buildFromDTO` — resolve-ва Doctor по ID, Patient по ID, Diagnoses по Set\<Long\> IDs. Запазва новия Examination.

**`updateExamination(Long id, ExaminationDTO dto)`**
Зарежда съществуващия Examination, обновява всички полета чрез `buildFromDTO`.

**`deleteExamination(Long id)`** — изтрива преглед

**`getTotalPricePaidByPatients()`** — сума от прегледи на неосигурени пациенти (JPQL)

**`getPricePaidByPatientsPerDoctor()`** — същото, групирано по доктор (JPQL с `Object[]`)

**`getVisitCountPerDoctor()`** — брой прегледи по доктор (JPQL)

**Частен метод `toDTO(Examination e)`** — преобразува entity в DTO, включително изчислява `paidByPatient = !e.getPatient().isHealthInsured()`

---

### `PatientServiceImpl`

**`getAllPatients()`**, **`getPatientById(Long id)`**, **`createPatient(PatientDTO)`**, **`updatePatient(Long, PatientDTO)`**, **`deletePatient(Long)`** — стандартен CRUD

**`getPatientsByGeneralPractitioner(Long gpId)`** — пациентите на конкретен личен лекар

**`getPatientsByDiagnosis(Long diagnosisId)`** — пациенти с конкретна диагноза (JPQL)

**`getPatientsByDiagnosisName(String name)`** — пациенти по текстово търсене на диагноза

Ползва `ModelMapper` за преобразуване Entity ↔ DTO.

---

### `DoctorServiceImpl`

**`getAllDoctors()`**, **`getDoctorById(Long id)`**, **`createDoctor(DoctorDTO)`**, **`updateDoctor(Long, DoctorDTO)`**, **`deleteDoctor(Long)`** — стандартен CRUD

**`getGeneralPractitioners()`** — само доктори с `isGeneralPractitioner = true`

**`getGPsWithPatientCount()`** — JPQL заявка, връща `Object[]{Doctor, Long count}` — за статистиката

---

### `SickLeaveServiceImpl`

**`getAllSickLeaves()`**, **`getSickLeaveById(Long id)`**, **`createSickLeave(SickLeaveDTO)`**, **`updateSickLeave(Long, SickLeaveDTO)`**, **`deleteSickLeave(Long)`** — стандартен CRUD

**`getSickLeavesByExamination(Long examinationId)`** — болничните листа за конкретен преглед

**`getMonthWithMostSickLeaves()`** — JPQL заявка; връща месеца с най-много болнични (само първия резултат)

**`getDoctorsWithMostSickLeaves()`** — JPQL заявка; всички доктори с максимален брой болнични

---

### `DiagnosisServiceImpl`

**`getAllDiagnoses()`**, **`getDiagnosisById(Long id)`**, **`createDiagnosis(DiagnosisDTO)`**, **`updateDiagnosis(Long, DiagnosisDTO)`**, **`deleteDiagnosis(Long)`** — стандартен CRUD

**`getMostCommonDiagnosis()`** — native SQL заявка; диагнозата с най-много асоциирани прегледи

---

## 7. View Controllers

Намират се в `web/view/`. Обработват заявки от браузъра и връщат Thymeleaf шаблони (HTML). Всички са `@Controller @RequiredArgsConstructor`.

---

### `IndexController`
**URL:** `/`, `/login`

**`GET /`** → `index.html` — начална страница

**`GET /login`** → `login.html` — страница за вход (Spring Security я обработва автоматично при POST)

---

### `PublicRegisterController`
**URL:** `/register`

**`GET /register`**
Подготвя празен `PublicRegisterDTO`, зарежда списъка с всички доктори (за GP dropdown и doctor dropdown), връща `register.html`.

**`POST /register`**
1. Ако ролята е PATIENT и EGN е празно → добавя ръчна грешка в `BindingResult` (защото `@Pattern` прескача null/празни стрингове)
2. Ако има грешки → връща формата обратно (с грешките показани в HTML)
3. Вика `userService.registerPublic(dto)` — ако хвърли `IllegalArgumentException` (дублирано username/EGN), връща формата с грешка
4. **Автоматичен вход:** зарежда `UserDetails`, създава `UsernamePasswordAuthenticationToken`, поставя в `SecurityContextHolder`, записва контекста в HTTP сесията с `HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY` → `redirect:/`

**Защо се записва в HTTP сесията?**
`SecurityContextHolder` е thread-local — живее само в текущата нишка. При `redirect`, браузърът прави нова HTTP заявка → нова нишка → `SecurityContextHolder` е празен. Ако контекстът се запише в сесията, Spring Security го чете при всяка следваща заявка.

---

### `ExaminationViewController`
**URL:** `/examinations`

**`GET /examinations`**
Зарежда прегледи според ролята:
- PATIENT → само своите прегледи (по `patientId` от auth)
- DOCTOR → само своите прегледи (по `doctorId` от auth)
- ADMIN → всички

**`GET /examinations/new`** `@Secured({"ROLE_DOCTOR","ROLE_ADMIN"})`
Подготвя форма. За DOCTOR: взима doctorId от auth, слага го в DTO, добавя модел атрибут `isDoctor=true` и `currentDoctor` (за readonly показване на името). За ADMIN: зарежда всички доктори за dropdown.

**`POST /examinations/new`** `@Secured({"ROLE_DOCTOR","ROLE_ADMIN"})`
Валидира. За DOCTOR: принудително задава `doctorId = собственото ID` (предотвратява манипулация на формата). Вика `examinationService.createExamination(dto)`.

**`GET /examinations/{id}/edit`** `@Secured({"ROLE_DOCTOR","ROLE_ADMIN"})`
Зарежда съществуващ преглед. Същата логика като при new — DOCTOR вижда readonly поле за доктор.

**`POST /examinations/{id}/edit`** `@Secured({"ROLE_DOCTOR","ROLE_ADMIN"})`
Валидира и обновява. За DOCTOR: doctorId се заключва към собственото ID.

**`POST /examinations/{id}/delete`** `@Secured("ROLE_ADMIN")`
Изтрива преглед.

**Частни помощни методи:**
- `isDoctor(Authentication auth)` → `boolean` — проверява дали потребителят има роля ROLE_DOCTOR
- `getDoctorId(Authentication auth)` → `Long` — взима ID на Doctor entity свързан с текущия потребител

---

### `SickLeaveViewController`
**URL:** `/sick-leaves`

**`GET /sick-leaves`**
- PATIENT → само болничните листа от собствените прегледи
- DOCTOR/ADMIN → всички

**`GET /sick-leaves/new`** `@Secured({"ROLE_DOCTOR","ROLE_ADMIN"})`
Зарежда форма. Вика `getExaminationsForUser(auth)` за dropdown с прегледи.

**`POST /sick-leaves/new`** — валидира и създава болничен лист

**`GET /sick-leaves/{id}/edit`**, **`POST /sick-leaves/{id}/edit`** — редакция

**`POST /sick-leaves/{id}/delete`** `@Secured("ROLE_ADMIN")` — изтриване

**Частен метод `getExaminationsForUser(Authentication auth)`**
- DOCTOR → само прегледите на текущия доктор (`getExaminationsByDoctor(doctorId)`)
- ADMIN → всички прегледи

---

### `PatientViewController`
**URL:** `/patients`

**`GET /patients`**, **`GET /patients/{id}`** — достъпни за DOCTOR и ADMIN

**`GET /patients/new`**, **`POST /patients/new`** — само ADMIN; формата зарежда GPs (само доктори с `isGeneralPractitioner=true`)

**`GET /patients/{id}/edit`**, **`POST /patients/{id}/edit`** — само ADMIN

**`POST /patients/{id}/delete`** — само ADMIN

---

### `DoctorViewController`
**URL:** `/doctors`

**`GET /doctors`**, **`GET /doctors/{id}`** — всички автентикирани

**`GET /doctors/new`**, **`POST /doctors/new`**, **`GET /doctors/{id}/edit`**, **`POST /doctors/{id}/edit`**, **`POST /doctors/{id}/delete`** — само ADMIN

---

### `DiagnosisViewController`
**URL:** `/diagnoses`

**`GET /diagnoses`** — всички автентикирани

**`GET /diagnoses/new`**, **`POST /diagnoses/new`**, **`GET /diagnoses/{id}/edit`**, **`POST /diagnoses/{id}/edit`** — DOCTOR и ADMIN

**`POST /diagnoses/{id}/delete`** — само ADMIN

---

### `UserViewController`
**URL:** `/users`

Само за ADMIN. Показва списък с потребители и позволява създаване на нови с `RegisterDTO` — свързва съществуващ Doctor или Patient с нов акаунт по техните ID-та.

---

### `StatisticsViewController`
**URL:** `/statistics`
`@Secured({"ROLE_ADMIN", "ROLE_DOCTOR"})`

**`GET /statistics`** с опционални `@RequestParam`: `diagnosisId`, `gpDoctorId`, `examDoctorId`, `historyPatientId`, `from`, `to`

Винаги зарежда 7 статистики:
1. Най-честа диагноза
2. Месец с най-много болнични листа
3. Лекари с най-много болнични листа
4. Обща сума, платена от пациенти
5. Брой прегледи по лекар
6. Сума по лекар
7. Всички GP-та с брой пациенти

При зададени параметри зарежда допълнително:
- Пациенти с дадена диагноза
- Пациенти при конкретен GP
- Прегледи на конкретен доктор (с евентуален период)
- История на конкретен пациент

---

## 8. API Controllers

Намират се в `web/api/`. Всички са `@RestController` — връщат JSON. Достъпни чрез AJAX от страниците или директно.

---

### `ExaminationApiController`
**URL:** `/api/examinations`

| Метод | URL | Роля | Описание |
|---|---|---|---|
| GET | `/api/examinations` | DOCTOR, ADMIN | Всички прегледи |
| GET | `/api/examinations/{id}` | PATIENT (само свой), DOCTOR, ADMIN | Един преглед |
| GET | `/api/examinations/by-patient/{id}` | PATIENT (само свой), DOCTOR, ADMIN | По пациент |
| GET | `/api/examinations/by-doctor/{id}` | DOCTOR, ADMIN | По доктор |
| GET | `/api/examinations/by-doctor-period` | DOCTOR, ADMIN | По доктор и период |
| POST | `/api/examinations` | DOCTOR, ADMIN | Нов преглед |
| PUT | `/api/examinations/{id}` | DOCTOR, ADMIN | Обновяване |
| DELETE | `/api/examinations/{id}` | ADMIN | Изтриване |

**Важна логика при POST/PUT за DOCTOR:**
- `doctorId` се принудително задава на собственото ID на доктора (не може да се създаде преглед от чуждо име)
- При PUT допълнително `patientId` се заключва към съществуващата стойност (не може да се смени пациентът)

---

### `SickLeaveApiController`
**URL:** `/api/sick-leaves`

| Метод | URL | Роля | Описание |
|---|---|---|---|
| GET | `/api/sick-leaves` | DOCTOR, ADMIN | Всички болнични листа |
| GET | `/api/sick-leaves/{id}` | PATIENT (само свой), DOCTOR, ADMIN | Един болничен лист |
| GET | `/api/sick-leaves/by-examination/{id}` | PATIENT (само свой), DOCTOR, ADMIN | По преглед |
| POST | `/api/sick-leaves` | DOCTOR, ADMIN | Нов болничен лист |
| PUT | `/api/sick-leaves/{id}` | DOCTOR, ADMIN | Обновяване |
| DELETE | `/api/sick-leaves/{id}` | ADMIN | Изтриване |

---

### `PatientApiController`
**URL:** `/api/patients`

| Метод | URL | Роля | Описание |
|---|---|---|---|
| GET | `/api/patients` | DOCTOR, ADMIN | Всички пациенти |
| GET | `/api/patients/{id}` | PATIENT (само свой), DOCTOR, ADMIN | Един пациент |
| GET | `/api/patients/by-gp/{id}` | DOCTOR, ADMIN | По личен лекар |
| GET | `/api/patients/by-diagnosis/{id}` | DOCTOR, ADMIN | По диагноза |
| POST | `/api/patients` | ADMIN | Нов пациент |
| PUT | `/api/patients/{id}` | ADMIN | Обновяване |
| DELETE | `/api/patients/{id}` | ADMIN | Изтриване |

---

### `DoctorApiController`
**URL:** `/api/doctors`

GET endpoints достъпни за всички автентикирани. POST/PUT/DELETE само за ADMIN.

---

### `DiagnosisApiController`
**URL:** `/api/diagnoses`

GET endpoints (включително `most-common`) достъпни за всички. POST/PUT за DOCTOR и ADMIN. DELETE само ADMIN.

---

### `StatisticsApiController`
**URL:** `/api/stats`

Всички са GET, защитени чрез `SecurityConfig` (DOCTOR и ADMIN). Осигурява данните за страницата `/statistics` чрез AJAX.

---

### `UserApiController`
**URL:** `/api/users`

Само `POST /api/users/register` — за ADMIN. Вика `userService.register(RegisterDTO)`.

---

## 9. Сигурност (Spring Security)

### `SecurityConfig`

`@Configuration @EnableWebSecurity @EnableMethodSecurity(securedEnabled = true)`

**`DaoAuthenticationProvider`** — компонентът, отговорен за проверката при вход:
1. Взима въведеното username → вика `UserService.loadUserByUsername()` → зарежда User от базата
2. Сравнява въведената парола с BCrypt хеша от базата чрез `PasswordEncoder`
3. Ако съвпадат → вход успешен

**`SecurityFilterChain`** — конфигурира правилата за достъп:

```
/css/**, /js/**         → публично (без вход)
/login, /register       → публично
DELETE /api/**          → само ADMIN
POST/PUT /api/examinations/** → DOCTOR, ADMIN
POST/PUT /api/sick-leaves/**  → DOCTOR, ADMIN
POST/PUT /api/diagnoses/**    → DOCTOR, ADMIN
/api/patients/**, /patients/** → DOCTOR, ADMIN
/users/**               → само ADMIN
/statistics/**          → DOCTOR, ADMIN
GET /api/stats/**       → DOCTOR, ADMIN
всичко останало         → изисква вход (authenticated)
```

**CSRF защита:**
CSRF (Cross-Site Request Forgery) е атака, при която злонамерен сайт изпраща заявки от името на логнатия потребител. Spring Security добавя скрит CSRF токен в HTML формите и го проверява при всяко POST. За REST API (`/api/**`) CSRF е изключен — клиентът е JavaScript, не форма, и сам управлява заявките.

```java
.csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/api/**")))
```

**Login:** POST `/login` с полета `username` и `password`. При успех → redirect `/`. При грешка → redirect `/login?error`. При logout → redirect `/login?logout`.

---

### `PasswordEncoderConfig`

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**BCrypt** е адаптивен хеш алгоритъм — умишлено бавен за да затрудни brute-force атаки. Паролите никога не се пазят в plaintext.

---

## 10. Инфраструктура

### `DataInitializer`
`@Component implements ApplicationRunner`

`ApplicationRunner.run()` се изпълнява веднъж при стартиране на приложението.

**Какво прави:**
1. Ако ролите липсват → създава `ADMIN`, `DOCTOR`, `PATIENT`
2. Ако admin потребителят липсва → създава `admin` / `admin123`
3. Ако няма доктори → създава `д-р Иванов` (GP) и `д-р Петрова` (специалист)
4. Ако няма пациенти → създава `Иван Иванов` (пациент с GP = Иванов)
5. Ако няма `doctor1` потребител → създава `doctor1` / `doctor123` (свързан с Иванов)

**Демо данни за вход:**
| Username | Password | Роля |
|---|---|---|
| `admin` | `admin123` | ADMIN |
| `doctor1` | `doctor123` | DOCTOR (д-р Иванов) |

---

### `GlobalExceptionHandler`
`@RestControllerAdvice`

`@RestControllerAdvice` е `@ControllerAdvice` + `@ResponseBody`. Прихваща изключения хвърлени от всеки контролер и ги преобразува в HTTP отговори.

| Изключение | HTTP статус | Отговор |
|---|---|---|
| `ResourceNotFoundException` | 404 Not Found | `{ "error": "..." }` |
| `AccessDeniedException` | 403 Forbidden | `{ "error": "Access denied" }` |
| `IllegalArgumentException` | 400 Bad Request | `{ "error": "..." }` |
| `IllegalStateException` | 422 Unprocessable Entity | `{ "error": "..." }` |
| `MethodArgumentNotValidException` | 400 Bad Request | `{ "field": "grешка", ... }` |

---

### `ResourceNotFoundException`

Прост RuntimeException. Хвърля се от Service методите при `findById`, когато записът не съществува в базата.

---

### `MapperUtil`
`@Configuration`

Дефинира `@Bean ModelMapper` — библиотека за автоматично копиране на полета между обекти с еднакви имена. Пример: `modelMapper.map(doctorEntity, DoctorDTO.class)` копира всички съвпадащи полета.

Статичен помощен метод `mapList(List<S> source, Class<T> target, ModelMapper mapper)` — преобразува цял списък с един ред код.

---

## 11. Поток на заявките — примери

### Пример 1: Доктор създава нов преглед

```
1. DOCTOR отваря /examinations/new
   → GET /examinations/new (ExaminationViewController.newForm)
   → Spring Security проверява: има ли ROLE_DOCTOR? Да → разрешено
   → Взима doctorId от auth (UserRepository.findByUsername → user.getDoctor().getId())
   → Слага doctorId в нов ExaminationDTO
   → model.addAttribute("isDoctor", true), model.addAttribute("currentDoctor", ...)
   → Thymeleaf рендира form.html с readonly поле за доктор

2. DOCTOR попълва и натиска Save
   → POST /examinations/new (ExaminationViewController.create)
   → @Valid валидира ExaminationDTO (date, patientId задължителни)
   → ако грешки: връща формата обратно
   → isDoctor=true → принудително dto.setDoctorId(currentDoctorId)
   → examinationService.createExamination(dto)
      → ExaminationServiceImpl.buildFromDTO:
         → doctorRepository.findById(dto.getDoctorId())
         → patientRepository.findById(dto.getPatientId())
         → diagnosisRepository.findAllById(dto.getDiagnosisIds())
         → creates Examination entity
      → examinationRepository.save(examination)
   → redirect:/examinations
```

---

### Пример 2: Регистрация на нов пациент

```
1. Потребителят отваря /register
   → GET /register (PublicRegisterController.showForm)
   → Нов PublicRegisterDTO, списък с всички доктори
   → register.html

2. Попълва форма като PATIENT, натиска Register
   → POST /register (PublicRegisterController.register)
   → @Valid проверява: username (NotBlank, 3-50), password (min 4), name (NotBlank), egn (regex)
   → Ако PATIENT и egn е празно → br.rejectValue("egn", ...)
   → Ако няма грешки → userService.registerPublic(dto)
      → @Transactional:
         → проверява duplicate username
         → roleRepository.findByName("PATIENT")
         → Creates Patient (name, egn, healthInsured=false, GP ако е избран)
         → patientRepository.save(patient)
         → Creates User (username, BCrypt(password), role=PATIENT, patient=patient)
         → userRepository.save(user)
   → userService.loadUserByUsername(username) → UserDetails
   → UsernamePasswordAuthenticationToken(userDetails, null, authorities)
   → SecurityContextHolder.setContext(context с auth)
   → session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context)
   → redirect:/ (вече е логнат)
```

---

### Пример 3: Admin вижда статистика

```
1. ADMIN отваря /statistics?diagnosisId=2
   → GET /statistics (StatisticsViewController)
   → @Secured({"ROLE_ADMIN","ROLE_DOCTOR"}) → OK
   → Зарежда 7 статистики (винаги):
      examinationService.getTotalPricePaidByPatients()
      sickLeaveService.getMonthWithMostSickLeaves()
      sickLeaveService.getDoctorsWithMostSickLeaves()
      examinationService.getVisitCountPerDoctor()
      examinationService.getPricePaidByPatientsPerDoctor()
      doctorService.getGPsWithPatientCount()
      diagnosisService.getMostCommonDiagnosis()
   → diagnosisId = 2 → не е null →
      patientService.getPatientsByDiagnosis(2)
      model.addAttribute("patientsByDiagnosis", ...)
   → statistics.html с всички данни
```

---

*Документ генериран за проект CSCB869 — Медицинско досие.*
