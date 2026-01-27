# Wordle Vocabulary Learning API

A Spring Boot REST API for learning vocabulary through quiz-based games, supporting Polish-Ukrainian word pairs.

## Features

- **JWT Authentication**: Secure user registration and login
- **Word Management**: Add words individually or in bulk
- **Learning Modes**:
  - Multiple choice quizzes (Ukrainian → Polish or Polish → Ukrainian)
  - Spelling tests with answer validation
  - Progress tracking (correct/incorrect counts)

## Technology Stack

- Java 21
- Spring Boot 3.2.1
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL Database
- Gradle (Kotlin DSL)
- Lombok

## Project Structure

```
src/main/java/com/example/wordle/
├── WordleApplication.java
├── config
│   └── SecurityConfig.java
├── controller
│   ├── AuthController.java
│   ├── QuizController.java
│   └── WordController.java
├── dto
│   ├── AuthResponse.java
│   ├── BulkWordRequest.java
│   ├── BulkWordResponse.java
│   ├── LoginRequest.java
│   ├── QuizQuestionDTO.java
│   ├── QuizResultDTO.java
│   ├── SignupRequest.java
│   ├── SpellCheckRequest.java
│   └── WordPairDTO.java
├── entity
│   ├── User.java
│   └── WordPair.java
├── repository
│   ├── UserRepository.java
│   └── WordPairRepository.java
├── security
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtUtils.java
└── service
├── AuthService.java
├── QuizService.java
└── WordService.java
```

## API Endpoints

### Authentication (Public)

#### POST `/auth/signup`
Register a new user
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "johndoe",
  "email": "john@example.com"
}
```

#### POST `/auth/login`
Login existing user
```json
{
  "username": "johndoe",
  "password": "password123"
}
```

### Word Management (Protected - Requires JWT)

#### POST `/api/words`
Add a single word pair
```json
{
  "polishWord": "kot",
  "ukrainianWord": "кіт"
}
```

#### POST `/api/words/bulk`
Add multiple word pairs
```json
{
  "wordPairs": [
    {"polishWord": "kot", "ukrainianWord": "кіт"},
    {"polishWord": "pies", "ukrainianWord": "собака"},
    {"polishWord": "dom", "ukrainianWord": "будинок"}
  ]
}
```

#### GET `/api/words`
Get all word pairs for the authenticated user

### Quiz Endpoints (Protected)

#### GET `/api/quiz/multiple-choice?questionLanguage=UKRAINIAN`
Get a multiple choice question

Query Parameters:
- `questionLanguage`: `UKRAINIAN` or `POLISH` (default: UKRAINIAN)

Response:
```json
{
  "questionWordId": 1,
  "questionWord": "кіт",
  "questionLanguage": "UKRAINIAN",
  "options": ["kot", "pies", "dom", "jabłko"]
}
```

#### POST `/api/quiz/spell-check`
Check spelling answer
```json
{
  "questionWord": "кіт",
  "questionLanguage": "UKRAINIAN",
  "answer": "kot"
}
```

Response:
```json
{
  "correct": true,
  "correctAnswer": "kot",
  "providedAnswer": "kot",
  "message": "Correct!"
}
```

## Running the Application

### Prerequisites
- JDK 21
- Gradle 8.x
- PostgreSQL 12+ (running on localhost:5432)

### Database Setup

1. **Run** the docker image
```bash
docker-compose up -d
```

3. **Update credentials** in `application.properties` if needed

### Build and Run

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

The API will be available at `http://localhost:8080`

### Database Access

View your data directly in PostgreSQL:
```bash
psql -U postgres -d wordledb

# List tables
\dt

# Query data
SELECT * FROM users;
SELECT * FROM word_pairs;
```

## Testing with cURL

### 1. Register a user
```bash
curl -X POST http://localhost:8080/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### 2. Save the JWT token from response
```bash
TOKEN="<your-jwt-token>"
```

### 3. Add words
```bash
curl -X POST http://localhost:8080/api/words/bulk \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "wordPairs": [
      {"polishWord": "kot", "ukrainianWord": "кіт"},
      {"polishWord": "pies", "ukrainianWord": "собака"},
      {"polishWord": "dom", "ukrainianWord": "будинок"},
      {"polishWord": "książka", "ukrainianWord": "книга"}
    ]
  }'
```

### 4. Get a quiz question
```bash
curl -X GET "http://localhost:8080/api/quiz/multiple-choice?questionLanguage=UKRAINIAN" \
  -H "Authorization: Bearer $TOKEN"
```

### 5. Check spelling
```bash
curl -X POST http://localhost:8080/api/quiz/spell-check \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "questionWord": "кіт",
    "questionLanguage": "UKRAINIAN",
    "answer": "kot"
  }'
```

## Configuration

### Database Configuration

For production, use environment variables:
```bash
export DATABASE_URL=jdbc:postgresql://your-host:5432/wordledb
export DATABASE_USERNAME=your_username
export DATABASE_PASSWORD=your_password
```

And update `application.properties`:

### JWT Configuration

Change JWT secret and expiration in `application.properties`:

## Security Features

- Password encryption using BCrypt
- JWT-based stateless authentication
- Token expiration after 24 hours
- User-specific data isolation
- Protected endpoints require valid JWT

## Performance Optimizations

- Batch insert for bulk operations (`hibernate.jdbc.batch_size=20`)
- Lazy loading for user-word relationships
- Indexed database queries for fast lookups
- Transaction management for data consistency

## Error Handling

The API includes comprehensive error handling:
- Validation errors (400 Bad Request)
- Authentication errors (401 Unauthorized)
- Not found errors (404 Not Found)
- Server errors (500 Internal Server Error)

All errors return a consistent JSON format:
```json
{
  "timestamp": "2024-01-23T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "details": {
    "polishWord": "Polish word is required"
  }
}
```

## Future Enhancements

- Spaced repetition algorithm
- Learning progress analytics
- Categories/tags for words
- Audio pronunciation support
- Mobile app integration
- Social features (shared word lists)
