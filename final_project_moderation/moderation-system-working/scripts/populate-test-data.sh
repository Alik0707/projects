#!/bin/bash
set -euo pipefail

# Скрипт для заполнения тестовых данных в Service-2 (Enrichment)

ENRICHMENT_URL="http://localhost:8081/api/enrichment"

echo "⏳ Ждём пока Service-2 станет доступен..."
for i in {1..40}; do
  if curl -sSf "http://localhost:8081/actuator/health" >/dev/null 2>&1; then
    echo "✅ Service-2 доступен"
    break
  fi
  sleep 0.5
  if [ "$i" -eq 40 ]; then
    echo "❌ Service-2 не поднялся (http://localhost:8081/actuator/health недоступен)"
    exit 1
  fi
done

post_json () {
  local url="$1"
  local json="$2"
  echo "➡️  POST $url"
  curl -sS -f --connect-timeout 2 --max-time 8     -X POST "$url"     -H "Content-Type: application/json"     -d "$json" >/dev/null
  echo "   ✅ OK"
}

echo "Заполнение тестовых данных в Redis (через Service-2)..."

# Клиент 1: VIP клиент без активных обращений
post_json "${ENRICHMENT_URL}/customer-info?customerId=CUST001" '{
  "tier": "VIP",
  "preferredLanguage": "EN",
  "isBlocked": false,
  "registrationDate": "2023-01-15T10:00:00"
}'
echo "✓ Создан клиент CUST001 (VIP)"

# Клиент 2: Стандартный клиент с активным обращением категории TECHNICAL
post_json "${ENRICHMENT_URL}/customer-info?customerId=CUST002" '{
  "tier": "STANDARD",
  "preferredLanguage": "RU",
  "isBlocked": false,
  "registrationDate": "2024-05-20T14:30:00"
}'
post_json "${ENRICHMENT_URL}/active-request?customerId=CUST002" '{
  "requestId": "REQ-ACTIVE-001",
  "category": "TECHNICAL",
  "status": "ACTIVE",
  "createdAt": "2026-02-06T12:00:00"
}'
echo "✓ Создан клиент CUST002 (STANDARD) с активным обращением TECHNICAL"

# Клиент 3: Заблокированный клиент
post_json "${ENRICHMENT_URL}/customer-info?customerId=CUST003" '{
  "tier": "BASIC",
  "preferredLanguage": "EN",
  "isBlocked": true,
  "registrationDate": "2025-11-01T09:00:00"
}'
echo "✓ Создан клиент CUST003 (BLOCKED)"

# Клиент 4: Клиент с активным обращением категории BILLING
post_json "${ENRICHMENT_URL}/customer-info?customerId=CUST004" '{
  "tier": "STANDARD",
  "preferredLanguage": "RU",
  "isBlocked": false,
  "registrationDate": "2024-08-10T16:20:00"
}'
post_json "${ENRICHMENT_URL}/active-request?customerId=CUST004" '{
  "requestId": "REQ-ACTIVE-002",
  "category": "BILLING",
  "status": "ACTIVE",
  "createdAt": "2026-02-05T10:15:00"
}'
echo "✓ Создан клиент CUST004 (STANDARD) с активным обращением BILLING"

# Клиент 5: Новый клиент без данных
echo "✓ Клиент CUST005 (без данных в кэше)"

echo "✅ Тестовые данные успешно загружены!"
