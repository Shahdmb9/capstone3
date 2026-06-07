
## Project Summary
AI-Driven Habit Cultivation & Family Gamification SystemAn advanced web ecosystem engineered with Spring Boot and integrated with Core AI engines (Claude AI) . The system delivers two dedicated, isolated tracks: the Individual Path, focused on tracking personal streaks, auto-assigning milestone badges, and rendering personalized mental roadmap advisories . Concurrently, the Family Track empowers parents to design pediatric habit structures, execute log approval systems, and initiate collaborative challenges with material incentives . The system automatically aggregates real-time performance to output interactive PDF compliance dashboards delivered dynamically via Email and WhatsApp APIs .

## Class Diagram 
![Class Diagram](https://github.com/Shahdmb9/capstone3/blob/shahad/Class%20Diagram.jpg)

# SHAHAD ALBARRAK ENDPOINTS


### 1-HabitCotroller

| HTTP Method | API Path |
| :--- | :--- |
| `PUT` | `/api/v1/habit/complete-habit/{habitLogId}` |
| `PUT` | `/api/v1/habit/review-log-of-child/{parentId}/{habitId}/{status}` |
| `GET` | `/api/v1/habit/category/{categoryId}` |
| `GET` | `/api/v1/habit/habit-summary/{habitId}` |
| `GET` | `/api/v1/habit/ai-habits/{individualId}` |
| `GET` | `/api/v1/habit/accept-habit-suggested/{individualId}/{habitId}` |
| `DELETE` | `/api/v1/habit/delete-all-ai-suggested/{individualId}` |
| `GET` | `/api/v1/habit/get-ai-habit-suggested/{individualId}` |
| `GET` | `/api/v1/habit/get-today-habits-child/{childId}` |
| `GET` | `/api/v1/habit/get-today-habits-Individual/{individualId}` |


### 2-ParentCotroller

| HTTP Method | API Path |
| :--- | :--- |
| `PUT` | `/api/v1/parent/deduct-child-point/{parentId}/{childId}/{poits}` |



### 3-IndividualCotroller  

| HTTP Method | API Path |
| :--- | :--- |
| `GET` | `/api/v1/{individualId}/ai/roadmap` |



