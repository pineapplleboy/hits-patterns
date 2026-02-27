#!/bin/bash
set -euo pipefail

echo "Waiting for Kafka at kafka:9092..."
cub kafka-ready -b kafka:9092 1 60

topics=(
  "00_ban_user_auth"
  "00_ban_user_bank_accounts"
  "00_create_user"
  "00_take_credit"
  "00_transfer_request"
  "00_transfer_assignment"
)

for topic in "${topics[@]}"; do
  kafka-topics --bootstrap-server kafka:9092 \
    --create \
    --if-not-exists \
    --topic "$topic" \
    --partitions 1 \
    --replication-factor 1
done

echo "Kafka topics initialization completed."
