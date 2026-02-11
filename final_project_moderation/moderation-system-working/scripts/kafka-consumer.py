#!/usr/bin/env python3
"""
Consumer для чтения модерированных событий из Kafka Topic-2
"""

import json
from kafka import KafkaConsumer
import argparse

KAFKA_BOOTSTRAP_SERVERS = ['localhost:9092']
TOPIC_NAME = 'moderated-requests'

def consume_events(count=None):
    """Чтение событий из Kafka Topic-2"""
    
    consumer = KafkaConsumer(
        TOPIC_NAME,
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        auto_offset_reset='earliest',
        enable_auto_commit=True,
        group_id='test-consumer-group',
        value_deserializer=lambda x: json.loads(x.decode('utf-8')),
        consumer_timeout_ms=10000  # Остановка после 10 секунд без сообщений
    )
    
    print(f"Listening to topic '{TOPIC_NAME}'...")
    print("-" * 80)
    
    messages_received = 0
    
    try:
        for message in consumer:
            event = message.value
            messages_received += 1
            
            print(f"\n📨 Message #{messages_received}")
            print(f"Partition: {message.partition}, Offset: {message.offset}")
            print(f"Key: {message.key.decode('utf-8') if message.key else 'None'}")
            print(f"Event ID: {event.get('eventId')}")
            print(f"Request ID: {event.get('requestId')}")
            print(f"Customer ID: {event.get('customerId')}")
            print(f"Category: {event.get('category')}")
            print(f"Priority: {event.get('priority')}")
            print(f"Customer Tier: {event.get('customerTier')}")
            print(f"Moderation Status: {event.get('moderationStatus')}")
            print(f"Moderated At: {event.get('moderatedAt')}")
            print(f"Reason: {event.get('moderationReason')}")
            print("-" * 80)
            
            if count and messages_received >= count:
                break
                
    except KeyboardInterrupt:
        print("\n\n⚠️  Consumer stopped by user")
    finally:
        consumer.close()
        print(f"\n✅ Total messages received: {messages_received}")

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Consume events from Kafka Topic-2')
    parser.add_argument('--count', type=int, default=None, 
                       help='Number of messages to consume (default: unlimited)')
    
    args = parser.parse_args()
    consume_events(count=args.count)
