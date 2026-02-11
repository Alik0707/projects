#!/bin/bash

echo "🛑 Остановка системы модерации..."

# Определение команды docker-compose
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
elif docker compose version &> /dev/null; then
    DOCKER_COMPOSE="docker compose"
else
    echo "❌ Docker Compose не найден"
    exit 1
fi

cd "$(dirname "$0")/../docker" || exit 1

$DOCKER_COMPOSE down

echo ""
echo "✅ Система остановлена!"
echo ""
echo "💡 Для полной очистки (включая volumes): $DOCKER_COMPOSE down -v"
