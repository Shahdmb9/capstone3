
## Project Summary
AI-Driven Habit Cultivation & Family Gamification SystemAn advanced web ecosystem engineered with Spring Boot and integrated with Core AI engines (Claude AI) . The system delivers two dedicated, isolated tracks: the Individual Path, focused on tracking personal streaks, auto-assigning milestone badges, and rendering personalized mental roadmap advisories . Concurrently, the Family Track empowers parents to design pediatric habit structures, execute log approval systems, and initiate collaborative challenges with material incentives . The system automatically aggregates real-time performance to output interactive PDF compliance dashboards delivered dynamically via Email and WhatsApp APIs .

## Class Diagram 
![Class Diagram]([https://github.com](https://github.com/Shahdmb9/capstone3/blob/master/src/main/resources/Class%20Diagram.jpg))

# HABIT SYSTEM ENDPOINTS (CAPSTONE 3)


### 1-Parent 

| HTTP Method | API Path |
| :--- | :--- |
| `GET` | `/api/v1/parent/{id}/children-report/{period}` |
| `GET` | `/api/v1/parent/download/{id}/children-report/{period}` |
| `GET` | `/api/v1/parent/family_discipline_score/{id}` |
| `GET` | `/api/v1/parent/family_activity/{id}/{city}` |
| `GET` | `/api/v1/parent/leaderboard/{id}` |


### 2-Habit

| HTTP Method | API Path |
| :--- | :--- |
| `GET` | `/api/v1/habit/ia_risk_prediction/{id}` |
| `GET` | `/api/v1/habit/ia_best_habit_time/{id}` |
 
### 3 - Task 
 
| HTTP Method | API Path |
| :--- | :--- |
| `POST` | `/api/v1/task/apply/{childId}/{taskId}` |
| `PUT` | `/api/v1/task/approve/{applicationId}/{action}` |
| `GET` | `/api/v1/task/parent-pending/{parentId}` |
 
### 4 - Task Reward 
 
| HTTP Method | API Path |
| :--- | :--- |
| `POST` | `/api/v1/task-reward/add/{parentId}/{taskId}` |
 
