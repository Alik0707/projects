#!/bin/bash

echo "🚀 Запуск системы модерации обращений клиентов"
echo "=============================================="
echo ""

# Проверка наличия Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker не установлен. Установите Docker и повторите попытку."
    exit 1
fi

# Определение команды docker-compose
if command -v docker-compose &> /dev/null; then
    DOCKER_COMPOSE="docker-compose"
elif docker compose version &> /dev/null; then
    DOCKER_COMPOSE="docker compose"
else
    echo "❌ Docker Compose не установлен."
    echo "Установите Docker Compose v2 или используйте: sudo apt install docker-compose"
    exit 1
fi

echo "ℹ️  Используется: $DOCKER_COMPOSE"

# Переход в директорию docker
cd "$(dirname "$0")/../docker" || exit 1

echo "📦 Запуск инфраструктуры..."
$DOCKER_COMPOSE up -d

echo ""
echo "⏳ Ожидание готовности сервисов (это может занять 30-60 секунд)..."
sleep 30

echo ""
echo "🔍 Проверка статуса сервисов..."
$DOCKER_COMPOSE ps

echo ""
echo "✅ Система запущена!"
echo ""
echo "📊 Доступные интерфейсы:"
echo "  - Kafka UI:          http://localhost:8090"
echo "  - Service-1 Health:  http://localhost:8080/actuator/health"
echo "  - Service-2 Health:  http://localhost:8081/actuator/health"
echo ""
echo "📝 Следующие шаги:"
echo "  1. Заполните тестовые данные: cd ../scripts && ./populate-test-data.sh"
echo "  2. Отправьте события: python3 kafka-producer.py --scenarios"
echo "  3. Просмотрите результаты: python3 kafka-consumer.py"
echo ""
echo "🛑 Для остановки: cd docker && $DOCKER_COMPOSE down"
