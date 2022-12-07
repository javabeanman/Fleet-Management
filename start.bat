call mvn clean install -DskipTests

call docker-compose up -d

echo "Service is ready"

pause