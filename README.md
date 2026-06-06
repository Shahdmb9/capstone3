



## 📊 1. Class Diagram Architecture
![Class Diagram](src/main/resources/Class%20Diagram.jpg) 

# HABIT SYSTEM ENDPOINTS (CAPSTONE 3)

Individual Controller & Service 

| HTTP Method | API Path | Description |
| :--- | :--- | :--- |
| `PUT` | `/api/v1/individual/add-interest/{individualId}/{categoryId}
| `POST` | `/api/v1/individual/{individualId}/ai/generate-plan
| `GET` | `/api/v1/individual/{individualId}/ai/achievement-index
| `GET` | `/api/v1/individual/{individualId}/ai/badges-progress

---

### Parent Controller & Service 

| HTTP Method | API Path | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/parent/{parentId}/pending-habits
| `GET` | `/api/v1/parent/{parentId}/children/{childId}/ai/behavior
| `GET` | `/api/v1/parent/{parentId}/children/{childId}/ai/recommended-rewards
---

### Habit Controller & Service 

| HTTP Method | API Path | Description |
| :--- | :--- | :--- |
| `PUT` | `/api/v1/habit/complete-habit/{habitId}` | إتمام الفرد أو الطفل للعادة لكسب النقاط والشارات (Complete Habit) |
| `PUT` | `/api/v1/habit/review-log-of-child/{parentId}/{habitId}/{status}` | الأب يراجع عادة ابنه المعلقة ويوافق عليها (Review Habit) |
| `GET` | `/api/v1/habit/{habitId}/ai/improvement` | مستشار تحسين أداء العادة المعينة ومقاومة الكسل (Improvement) |
| `GET` | `/api/v1/habit/{habitId}/ai/commitment` | تحليل الالتزام والتنبؤ بالـ Streak الحركي للعادة (Commitment) |

---

### 🎁 Reward & Task Reward Controllers (دورة الثواب وجوائز المسابقات الجماعية)
تحتوي على ربط الجوائز والمكافآت وتوثيق خطوط الصرف واستحقاق التحديات:


| HTTP Method | API Path | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/reward/add/{parentId}/{habitId}` | الأب يربط مكافأة مخصصة بنقاط عادة ابنه (Add Reward to Habit) |
| `POST` | `/api/v1/task-reward/add/{parentId}/{taskId}` | الأب يربط جائزة كبرى فورية بالتحدي الجماعي (Add Reward to Task) |
| `POST` | `/api/v1/task/apply/{childId}/{taskId}` | تقديم الطفل الأسرع لطلب الفوز وتثبيت استحقاق المكافأة (Apply Task) |
