#!/bin/bash

# Цвета
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  Проверка системных требований${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

SUCCESS=0
WARNINGS=0
ERRORS=0

# Функция проверки
check() {
    local name=$1
    local command=$2
    local required=$3
    
    if eval "$command" &> /dev/null; then
        echo -e "${GREEN}✓${NC} $name"
        ((SUCCESS++))
        return 0
    else
        if [ "$required" == "required" ]; then
            echo -e "${RED}✗${NC} $name ${RED}(ТРЕБУЕТСЯ)${NC}"
            ((ERRORS++))
        else
            echo -e "${YELLOW}⚠${NC} $name ${YELLOW}(рекомендуется)${NC}"
            ((WARNINGS++))
        fi
        return 1
    fi
}

# Проверка версии
check_version() {
    local name=$1
    local command=$2
    local min_version=$3
    
    if eval "$command" &> /dev/null; then
        local version=$(eval "$command")
        echo -e "${GREEN}✓${NC} $name: $version"
        ((SUCCESS++))
    else
        echo -e "${RED}✗${NC} $name ${RED}(не найден)${NC}"
        ((ERRORS++))
    fi
}

echo -e "${YELLOW}Проверка основных утилит:${NC}"
check "Docker" "command -v docker" "required"
check "Docker Compose v2" "docker compose version" "optional"
check "Docker Compose v1" "command -v docker-compose" "optional"
check "curl" "command -v curl" "required"
check "Python 3" "command -v python3" "required"
check "pip3" "command -v pip3" "required"

echo ""
echo -e "${YELLOW}Проверка версий:${NC}"
if command -v docker &> /dev/null; then
    check_version "Docker" "docker --version | cut -d' ' -f3 | cut -d',' -f1" ""
fi

if command -v python3 &> /dev/null; then
    check_version "Python" "python3 --version | cut -d' ' -f2" ""
fi

echo ""
echo -e "${YELLOW}Проверка портов:${NC}"
check_port() {
    local port=$1
    local name=$2
    
    if ! sudo lsof -i :$port &> /dev/null; then
        echo -e "${GREEN}✓${NC} Порт $port свободен ($name)"
        ((SUCCESS++))
    else
        echo -e "${RED}✗${NC} Порт $port занят ($name)"
        echo -e "   ${YELLOW}Процесс:${NC}"
        sudo lsof -i :$port | grep LISTEN
        ((ERRORS++))
    fi
}

check_port 8080 "Moderation Service"
check_port 8081 "Enrichment Service"
check_port 9092 "Kafka"
check_port 6379 "Redis"
check_port 2181 "Zookeeper"
check_port 8090 "Kafka UI"

echo ""
echo -e "${YELLOW}Проверка ресурсов:${NC}"

# Проверка памяти
TOTAL_RAM=$(free -g | awk '/^Mem:/{print $2}')
if [ "$TOTAL_RAM" -ge 4 ]; then
    echo -e "${GREEN}✓${NC} Оперативная память: ${TOTAL_RAM}GB (достаточно)"
    ((SUCCESS++))
elif [ "$TOTAL_RAM" -ge 2 ]; then
    echo -e "${YELLOW}⚠${NC} Оперативная память: ${TOTAL_RAM}GB (минимум, может быть медленно)"
    ((WARNINGS++))
else
    echo -e "${RED}✗${NC} Оперативная память: ${TOTAL_RAM}GB (недостаточно, нужно минимум 2GB)"
    ((ERRORS++))
fi

# Проверка диска
AVAILABLE_DISK=$(df -BG . | awk 'NR==2 {print $4}' | sed 's/G//')
if [ "$AVAILABLE_DISK" -ge 10 ]; then
    echo -e "${GREEN}✓${NC} Свободное место: ${AVAILABLE_DISK}GB (достаточно)"
    ((SUCCESS++))
elif [ "$AVAILABLE_DISK" -ge 5 ]; then
    echo -e "${YELLOW}⚠${NC} Свободное место: ${AVAILABLE_DISK}GB (минимум)"
    ((WARNINGS++))
else
    echo -e "${RED}✗${NC} Свободное место: ${AVAILABLE_DISK}GB (недостаточно)"
    ((ERRORS++))
fi

# Проверка Docker демона
if systemctl is-active --quiet docker 2>/dev/null; then
    echo -e "${GREEN}✓${NC} Docker демон запущен"
    ((SUCCESS++))
else
    echo -e "${RED}✗${NC} Docker демон не запущен"
    echo -e "   ${YELLOW}Запустите: sudo systemctl start docker${NC}"
    ((ERRORS++))
fi

# Проверка прав Docker
if docker ps &> /dev/null; then
    echo -e "${GREEN}✓${NC} Права на использование Docker"
    ((SUCCESS++))
else
    echo -e "${RED}✗${NC} Нет прав на использование Docker"
    echo -e "   ${YELLOW}Выполните: sudo usermod -aG docker \$USER${NC}"
    echo -e "   ${YELLOW}Затем: newgrp docker${NC}"
    ((ERRORS++))
fi

echo ""
echo -e "${YELLOW}Проверка Python пакетов:${NC}"
check "kafka-python" "python3 -c 'import kafka'" "required"
check "redis" "python3 -c 'import redis'" "optional"

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}           Результаты${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✓ Успешно:${NC} $SUCCESS"
echo -e "${YELLOW}⚠ Предупреждения:${NC} $WARNINGS"
echo -e "${RED}✗ Ошибки:${NC} $ERRORS"
echo ""

if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✅ Система готова к запуску!${NC}"
    echo -e "${BLUE}Запустите: ./start-system.sh${NC}"
    exit 0
elif [ $ERRORS -le 2 ]; then
    echo -e "${YELLOW}⚠️  Система может работать, но рекомендуется исправить ошибки${NC}"
    exit 0
else
    echo -e "${RED}❌ Необходимо исправить критические ошибки перед запуском${NC}"
    echo ""
    echo -e "${YELLOW}Рекомендации:${NC}"
    echo -e "1. Установите Docker: ${BLUE}curl -fsSL https://get.docker.com | sh${NC}"
    echo -e "2. Добавьте права: ${BLUE}sudo usermod -aG docker \$USER${NC}"
    echo -e "3. Установите Python пакеты: ${BLUE}pip3 install kafka-python redis --break-system-packages${NC}"
    echo -e "4. Освободите порты или остановите конфликтующие сервисы"
    exit 1
fi
