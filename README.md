# Snowball

An open source web-based trading bot for Indian markets.

## Tech Stack
- **Java + Spring Boot** (with Security & Thymeleaf)
- **TA4J** for technical analysis
- **PostgreSQL** for storing and caching data
- **Docker** for containerization

## Core Features
- Watchlists of stocks
- Caching historical data in the database
- Custom strategy creation (rule-based, using technical indicators)
- Backtesting on historical data
- Forward testing with paper money
- Actual live trading (starting with Zerodha)

## Local Development Setup

### Prerequisites
- Java 17 or later
- Docker & Docker Compose
- Maven (or use the provided `mvnw` wrapper)

### Environment Variables
The application uses environment variables for configuration. You can set them in your shell or in a `.env` file:

```
DB_URL=jdbc:postgresql://localhost:5432/snowball
DB_USERNAME=myuser
DB_PASSWORD=secret
POSTGRES_USER=myuser
POSTGRES_PASSWORD=secret
KITE_API_KEY=your_kite_api_key
KITE_API_SECRET=your_kite_api_secret
KITE_USER_ID=your_kite_user_id
```

- `KITE_API_KEY`, `KITE_API_SECRET`, and `KITE_USER_ID` are required for Zerodha Kite live trading integration.

### Running the Database
Start PostgreSQL with Docker Compose (data will persist across restarts):

```
docker compose up -d
```

### Running the Application
Build and run the Spring Boot app:

```
./mvnw spring-boot:run
```

Or build the jar and run:

```
./mvnw clean package
java -jar target/*.jar
```

### Stopping the Database
```
docker compose down
```
(Data will persist due to Docker volume.)

## Contributing
Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

## License
[MIT](LICENSE)
