# Flyway Database Migration Guide

This project uses **Flyway** for database schema versioning and migrations.

## 📁 Migration Files Location

All migration files are located in:
```
src/main/resources/db/migration/
```

## 📝 Naming Convention

Flyway migration files must follow this naming pattern:
```
V{version}__{description}.sql
```

Examples:
- `V1__initial_schema.sql` - Initial database schema
- `V2__add_user_table.sql` - Add new user table
- `V3__add_email_column.sql` - Add email column to users

**Important Rules:**
- Version numbers must be unique and sequential
- Use **double underscore** (`__`) between version and description
- Use **single underscore** (`_`) in description for word separation
- Versions are applied in order (V1, V2, V3, etc.)

## 🚀 How It Works

1. **On Application Startup**: Flyway automatically:
   - Creates a `flyway_schema_history` table to track migrations
   - Checks which migrations have been applied
   - Runs any new migrations in order

2. **Development (H2)**: Uses in-memory database, migrations run fresh each time
3. **Production (PostgreSQL)**: Migrations are applied once and tracked

## 🔧 Configuration

### Development (Default)
```yaml
# application.yaml
spring:
  datasource:
    url: jdbc:h2:mem:tedtalksdb
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

### Production
```yaml
# application-prod.yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tedtalks
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
```

## 📊 Creating New Migrations

### Step 1: Create Migration File
Create a new file in `src/main/resources/db/migration/`:
```sql
-- V2__add_category_column.sql
ALTER TABLE ted_talks ADD COLUMN category VARCHAR(100);
CREATE INDEX idx_category ON ted_talks(category);
```

### Step 2: Regenerate jOOQ Classes
```bash
mvn generate-sources
```

### Step 3: Test
```bash
mvn clean test
```

## 🗄️ PostgreSQL Setup

### Using Docker
```bash
docker run --name tedtalks-postgres \
  -e POSTGRES_DB=tedtalks \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:16
```

### Run with PostgreSQL Profile
```bash
# Set environment variables
export DATABASE_URL=jdbc:postgresql://localhost:5432/tedtalks
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=postgres

# Run application
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

Or using application properties:
```bash
java -jar target/tedtalk-api-1.0.0.jar --spring.profiles.active=prod
```

## 🔍 Flyway Commands (Maven Plugin)

Add to `pom.xml` for manual Flyway operations:
```xml
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <configuration>
        <url>jdbc:postgresql://localhost:5432/tedtalks</url>
        <user>postgres</user>
        <password>postgres</password>
    </configuration>
</plugin>
```

Then run:
```bash
mvn flyway:info      # Show migration status
mvn flyway:migrate   # Run migrations
mvn flyway:validate  # Validate migrations
mvn flyway:clean     # Drop all objects (use with caution!)
```

## ⚠️ Best Practices

1. **Never modify applied migrations** - Create a new migration instead
2. **Test migrations locally** before deploying to production
3. **Use transactions** - Flyway wraps each migration in a transaction
4. **Keep migrations small** - One logical change per migration
5. **Use descriptive names** - Make it clear what each migration does
6. **Version control** - Commit migration files to Git

## 🔄 Migration Workflow

```
1. Write SQL migration → V{N}__description.sql
2. Regenerate jOOQ   → mvn generate-sources
3. Update code       → Use new jOOQ classes
4. Test locally      → mvn test
5. Commit changes    → git commit
6. Deploy            → Flyway runs automatically
```

## 📚 Current Migrations

- **V1__initial_schema.sql** - Creates `ted_talks` and `import_status` tables with indexes

## 🆘 Troubleshooting

### Migration Failed
```bash
# Check Flyway history
SELECT * FROM flyway_schema_history;

# If needed, mark migration as repaired
mvn flyway:repair
```

### Out of Order Migrations
Set in `application.yaml`:
```yaml
spring:
  flyway:
    out-of-order: true  # Allow out-of-order migrations
```

### Baseline Existing Database
```bash
mvn flyway:baseline -Dflyway.baselineVersion=1
```
