
## Project Summary
AI-Driven Habit Cultivation & Family Gamification SystemAn advanced web ecosystem engineered with Spring Boot and integrated with Core AI engines (Claude AI) . The system delivers two dedicated, isolated tracks: the Individual Path, focused on tracking personal streaks, auto-assigning milestone badges, and rendering personalized mental roadmap advisories . Concurrently, the Family Track empowers parents to design pediatric habit structures, execute log approval systems, and initiate collaborative challenges with material incentives . The system automatically aggregates real-time performance to output interactive PDF compliance dashboards delivered dynamically via Email and WhatsApp APIs .

## Class Diagram 
![Class Diagram]([https://github.com](https://github.com/Shahdmb9/capstone3/blob/master/src/main/resources/Class%20Diagram.jpg))

# HABIT SYSTEM ENDPOINTS (CAPSTONE 3)

### 1-Individual Path 

| HTTP Method | API Path |
| :--- | :--- |
| `PUT` | `/api/v1/individual/add-interest/{individualId}/{categoryId}` |
| `POST` | `/api/v1/individual/{individualId}/ai/generate-plan` |
| `GET` | `/api/v1/individual/{individualId}/ai/achievement-index` |
| `GET` | `/api/v1/individual/{individualId}/ai/badges-progress` |


### 2-Parent 

| HTTP Method | API Path |
| :--- | :--- |
| `GET` | `/api/v1/parent/{parentId}/pending-habits` |
| `GET` | `/api/v1/parent/{parentId}/children/{childId}/ai/behavior` |
| `GET` | `/api/v1/parent/{parentId}/children/{childId}/ai/recommended-rewards` |


### 3-Habit

| HTTP Method | API Path |
| :--- | :--- |
| `PUT` | `/api/v1/habit/complete-habit/{habitId}` |
| `PUT` | `/api/v1/habit/review-log-of-child/{parentId}/{habitId}/{status}` |
| `GET` | `/api/v1/habit/{habitId}/ai/improvement` |
| `GET` | `/api/v1/habit/{habitId}/ai/commitment` |

### 4-Reward & Task Reward 

| HTTP Method | API Path |
| :--- | :--- |
| `POST` | `/api/v1/reward/add/{parentId}/{habitId}` |
| `POST` | `/api/v1/task-reward/add/{parentId}/{taskId}` |
| `POST` | `/api/v1/task/apply/{childId}/{taskId}` |
