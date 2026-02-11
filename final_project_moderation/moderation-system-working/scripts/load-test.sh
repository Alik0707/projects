#!/bin/bash

echo "⚡ Нагрузочное тестирование системы модерации"
echo "============================================"
echo ""

# Проверка наличия Python скрипта
if [ ! -f "kafka-producer.py" ]; then
    echo "❌ Файл kafka-producer.py не найден!"
    exit 1
fi

# Параметры по умолчанию
RPS=${1:-100}
DURATION=${2:-60}
TOTAL_EVENTS=$((RPS * DURATION))
DELAY=$(awk "BEGIN {print 1/$RPS}")

echo "📊 Параметры нагрузки:"
echo "  - RPS (запросов в секунду): $RPS"
echo "  - Длительность (секунд): $DURATION"
echo "  - Всего событий: $TOTAL_EVENTS"
echo "  - Задержка между событиями: $DELAY сек"
echo ""

read -p "Начать тестирование? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Отменено."
    exit 0
fi

echo ""
echo "🚀 Запуск генератора событий..."
echo ""

python3 kafka-producer.py --count "$TOTAL_EVENTS" --delay "$DELAY"

echo ""
echo "✅ Нагрузочное тестирование завершено!"
echo ""
echo "📈 Проверьте метрики:"
echo "  - Kafka UI: http://localhost:8090"
echo "  - Metrics Service-1: http://localhost:8080/actuator/metrics"
echo "  - Metrics Service-2: http://localhost:8081/actuator/metrics"
