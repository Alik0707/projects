#!/bin/bash

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   Система модерации - Запуск${NC}"
echo -e "${BLUE}========================================${NC}"

# Проверка наличия Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker не установлен!${NC}"
    echo -e "${YELLOW}Установите Docker: https://docs.docker.com/engine/install/ubuntu/${NC}"
    exit 1
fi

# Проверка наличия Docker Compose
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo -e "${RED}❌ Docker Compose не установлен!${NC}"
    echo -e "${YELLOW}Установите Docker Compose${NC}"
    exit 1
fi

# Определяем команду docker compose
if docker compose version &> /dev/null; then
    DOCKER_COMPOSE="docker compose"
else
    DOCKER_COMPOSE="docker-compose"
fi

echo -e "${GREEN}✓ Docker установлен${NC}"
echo -e "${GREEN}✓ Docker Compose установлен${NC}"
echo ""

# Переходим в директорию docker
cd "$(dirname "$0")/docker" || exit 1

echo -e "${YELLOW}📦 Остановка старых контейнеров...${NC}"
$DOCKER_COMPOSE down -v

echo ""
echo -e "${YELLOW}🔨 Сборка образов...${NC}"
$DOCKER_COMPOSE build --no-cache

echo ""
echo -e "${YELLOW}🚀 Запуск сервисов...${NC}"
$DOCKER_COMPOSE up -d

echo ""
echo -e "${YELLOW}⏳ Ожидание готовности сервисов...${NC}"
sleep 10

# Проверка статуса
echo ""
echo -e "${BLUE}📊 Статус сервисов:${NC}"
$DOCKER_COMPOSE ps

echo ""
echo -e "${BLUE}🔍 Проверка здоровья сервисов...${NC}"
sleep 20

# Проверяем здоровье каждого сервиса
check_health() {
    local service=$1
    local url=$2
    local retries=12
    local count=0
    
    while [ $count -lt $retries ]; do
        if curl -sf "$url" > /dev/null 2>&1; then
            echo -e "${GREEN}✓ $service готов${NC}"
            return 0
        fi
        count=$((count + 1))
        sleep 5
    done
    
    echo -e "${RED}✗ $service не отвечает${NC}"
    return 1
}

check_health "Enrichment Service" "http://localhost:8081/actuator/health"
check_health "Moderation Service" "http://localhost:8080/actuator/health"

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✓ Система запущена!${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo -e "${YELLOW}📍 Доступные сервисы:${NC}"
echo -e "  • Kafka UI:           ${BLUE}http://localhost:8090${NC}"
echo -e "  • Moderation Service: ${BLUE}http://localhost:8080${NC}"
echo -e "  • Enrichment Service: ${BLUE}http://localhost:8081${NC}"
echo -e "  • Redis:              ${BLUE}localhost:6379${NC}"
echo -e "  • Kafka:              ${BLUE}localhost:9092${NC}"
echo ""
echo -e "${YELLOW}📝 Следующие шаги:${NC}"
echo -e "  1. Заполните тестовые данные: ${BLUE}cd scripts && ./populate-test-data.sh${NC}"
echo -e "  2. Отправьте тестовые события: ${BLUE}python3 kafka-producer.py --scenarios${NC}"
echo -e "  3. Просмотрите результаты:     ${BLUE}python3 kafka-consumer.py${NC}"
echo ""
echo -e "${YELLOW}🔧 Полезные команды:${NC}"
echo -e "  • Логи всех сервисов:  ${BLUE}$DOCKER_COMPOSE logs -f${NC}"
echo -e "  • Логи конкретного:    ${BLUE}$DOCKER_COMPOSE logs -f moderation-service${NC}"
echo -e "  • Остановка:           ${BLUE}$DOCKER_COMPOSE down${NC}"
echo -e "  • Перезапуск:          ${BLUE}$DOCKER_COMPOSE restart${NC}"
echo ""
