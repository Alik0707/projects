#!/usr/bin/env python3
"""
Генератор тестовых событий для отправки в Kafka Topic-1
"""

import json
import time
from datetime import datetime, timedelta
from kafka import KafkaProducer
import random
import argparse

# Конфигурация Kafka
KAFKA_BOOTSTRAP_SERVERS = ['localhost:9092']
TOPIC_NAME = 'customer-requests'

# Тестовые данные
CATEGORIES = ['TECHNICAL', 'BILLING', 'COMPLAINT', 'GENERAL']
PRIORITIES = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL']
SOURCES = ['WEB', 'MOBILE', 'EMAIL', 'PHONE']
CUSTOMER_IDS = ['CUST001', 'CUST002', 'CUST003', 'CUST004', 'CUST005']

def generate_event(event_number, custom_customer_id=None, custom_category=None, custom_time=None):
    """Генерация одного события"""
    
    customer_id = custom_customer_id if custom_customer_id else random.choice(CUSTOMER_IDS)
    category = custom_category if custom_category else random.choice(CATEGORIES)
    timestamp = custom_time if custom_time else datetime.now()
    
    event = {
        'eventId': f'EVT-{int(time.time() * 1000)}-{event_number}',
        'requestId': f'REQ-{int(time.time() * 1000)}-{event_number}',
        'customerId': customer_id,
        'category': category,
        'priority': random.choice(PRIORITIES),
        'description': f'Test request for {category} from {customer_id}',
        'timestamp': timestamp.isoformat(),
        'source': random.choice(SOURCES)
    }
    
    return event

def send_events(count=10, delay=0.1):
    """Отправка событий в Kafka"""
    
    producer = KafkaProducer(
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        value_serializer=lambda v: json.dumps(v).encode('utf-8'),
        key_serializer=lambda v: v.encode('utf-8') if v else None
    )
    
    print(f"Sending {count} events to Kafka topic '{TOPIC_NAME}'...")
    
    for i in range(count):
        event = generate_event(i)
        
        # Отправляем с ключом requestId для партиционирования
        producer.send(TOPIC_NAME, key=event['requestId'], value=event)
        
        print(f"Sent event {i+1}/{count}: eventId={event['eventId']}, "
              f"customerId={event['customerId']}, category={event['category']}")
        
        if delay > 0:
            time.sleep(delay)
    
    producer.flush()
    producer.close()
    print(f"\n✅ Successfully sent {count} events!")

def send_test_scenarios():
    """Отправка специфичных тестовых сценариев"""
    
    producer = KafkaProducer(
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        value_serializer=lambda v: json.dumps(v).encode('utf-8'),
        key_serializer=lambda v: v.encode('utf-8') if v else None
    )
    
    scenarios = []
    
    # Сценарий 1: Событие должно пройти (VIP клиент, нет активных обращений)
    scenarios.append({
        'name': 'PASS - VIP customer, no active requests',
        'event': generate_event(1, custom_customer_id='CUST001', custom_category='GENERAL')
    })
    
    # Сценарий 2: Блокировка по активному обращению (CUST002 уже имеет TECHNICAL)
    scenarios.append({
        'name': 'REJECT - Active request exists (TECHNICAL)',
        'event': generate_event(2, custom_customer_id='CUST002', custom_category='TECHNICAL')
    })
    
    # Сценарий 3: Блокировка по заблокированному клиенту
    scenarios.append({
        'name': 'REJECT - Customer is blocked',
        'event': generate_event(3, custom_customer_id='CUST003', custom_category='GENERAL')
    })
    
    # Сценарий 4: Блокировка по рабочему времени (TECHNICAL вне рабочих часов)
    night_time = datetime.now().replace(hour=22, minute=0, second=0)
    scenarios.append({
        'name': 'REJECT - Outside working hours (TECHNICAL at 22:00)',
        'event': generate_event(4, custom_customer_id='CUST001', 
                               custom_category='TECHNICAL', custom_time=night_time)
    })
    
    # Сценарий 5: Дубликат события (идемпотентность)
    duplicate_event = generate_event(5, custom_customer_id='CUST001', custom_category='GENERAL')
    scenarios.append({
        'name': 'DUPLICATE - Same eventId (first time)',
        'event': duplicate_event
    })
    scenarios.append({
        'name': 'DUPLICATE - Same eventId (second time, should be skipped)',
        'event': duplicate_event  # Точно такое же событие
    })
    
    # Сценарий 6: Клиент без данных в кэше
    scenarios.append({
        'name': 'PASS - Customer not in cache',
        'event': generate_event(6, custom_customer_id='CUST005', custom_category='GENERAL')
    })
    
    # Сценарий 7: BILLING вне рабочего времени
    weekend_time = datetime.now()
    # Находим ближайшую субботу
    days_until_saturday = (5 - weekend_time.weekday()) % 7
    if days_until_saturday == 0:
        days_until_saturday = 7
    weekend_time += timedelta(days=days_until_saturday)
    weekend_time = weekend_time.replace(hour=14, minute=0, second=0)
    
    scenarios.append({
        'name': 'REJECT - BILLING on weekend',
        'event': generate_event(7, custom_customer_id='CUST001', 
                               custom_category='BILLING', custom_time=weekend_time)
    })
    
    print("Sending test scenarios...")
    for i, scenario in enumerate(scenarios, 1):
        event = scenario['event']
        producer.send(TOPIC_NAME, key=event['requestId'], value=event)
        print(f"{i}. {scenario['name']}")
        print(f"   eventId={event['eventId']}, customerId={event['customerId']}, "
              f"category={event['category']}, timestamp={event['timestamp']}")
        time.sleep(0.5)
    
    producer.flush()
    producer.close()
    print(f"\n✅ Sent {len(scenarios)} test scenarios!")

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Send test events to Kafka')
    parser.add_argument('--count', type=int, default=10, 
                       help='Number of random events to send')
    parser.add_argument('--delay', type=float, default=0.1, 
                       help='Delay between events in seconds')
    parser.add_argument('--scenarios', action='store_true', 
                       help='Send predefined test scenarios instead of random events')
    
    args = parser.parse_args()
    
    if args.scenarios:
        send_test_scenarios()
    else:
        send_events(count=args.count, delay=args.delay)
