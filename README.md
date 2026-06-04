
# Expense Management Server

This project is a backend application for managing shared expenses within groups. It allows users to track expenses, calculate balances, and easily determine who owes money to whom.

The project is being developed for educational purposes as a way to learn and practice modern software development techniques and backend architecture.

The application follows the principles of Clean Architecture, focusing on maintainability, separation of concerns, and scalability.

## Project Structure

```text
.
├── .github/
│   └── workflows/          # CI/CD pipeline definitions
├── src/
│   ├── domain/             # Core business entities, value objects, and domain rules
│   ├── application/        # Use cases and application services
│   ├── infrastructure/     # Database, external services, and framework-specific implementations
│   ├── api/                # API controllers, DTOs, and request/response models
│   └── config/             # Application configuration and environment setup
├── dist/                   # Compiled production build
├── .env                    # Environment variables
├── package.json
└── tsconfig.json
```

### Architecture

This project follows the principles of **Clean Architecture**:

* **Domain** – contains business entities and domain logic.
* **Application** – contains use cases that orchestrate business operations.
* **Infrastructure** – contains technical implementations such as database access, external APIs, and framework integrations.
* **api** – exposes the application through HTTP endpoints and handles request/response mapping.

Dependencies always point inward, ensuring that business logic remains independent from frameworks and infrastructure concerns.

## Available Commands

| Command                 | Description                                                                       |
| ----------------------- | --------------------------------------------------------------------------------- |
| `npm run dev`           | Starts the application in development mode with automatic reload on file changes. |
| `npm run debug`         | Starts the application in development mode with the Node.js debugger enabled.     |
| `npm run build`         | Compiles the TypeScript source code into the `dist` directory.                    |
| `npm run start`         | Runs the compiled production build from the `dist` directory.                     |
| `npm run check`         | Runs code quality and linting checks using Biome.                                 |
| `npm run format`        | Automatically formats and fixes safe code style issues using Biome.               |
| `npm run format:unsafe` | Applies all available Biome fixes, including potentially unsafe ones.             |

### Development

```bash
npm install
npm run dev
```

### Production

```bash
npm run build
npm run start
```
