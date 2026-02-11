#!/bin/bash

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   Остановка системы модерации${NC}"
echo -e "${BLUE}========================================${NC}"

# Определяем команду docker compose
if docker compose version &> /dev/null 2>&1; then
    DOCKER_COMPOSE="docker compose"
else
    DOCKER_COMPOSE="docker-compose"
fi

cd "$(dirname "$0")/docker" || exit 1

echo -e "${YELLOW}🛑 Остановка контейнеров...${NC}"
$DOCKER_COMPOSE down

echo ""
echo -e "${GREEN}✓ Все сервисы остановлены${NC}"

# Опция для удаления volumes
if [ "$1" == "--clean" ]; then
    echo ""
    echo -e "${YELLOW}🧹 Очистка volumes...${NC}"
    $DOCKER_COMPOSE down -v
    echo -e "${GREEN}✓ Volumes удалены${NC}"
fi

echo ""
echo -e "${BLUE}Для полной очистки с удалением данных используйте: ./stop-system.sh --clean${NC}"
echo ""
