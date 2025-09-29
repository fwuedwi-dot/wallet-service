# wallet-service
Wallet in Java using Spring 3.

Запуск базы данных

cd /path/to/wallet-service

docker-compose up -d postgres

docker ps

Работа 

# Создание кошелька
curl -X POST http://localhost:8080/api/v1/wallets

# Должен вернуть что-то вроде: {"walletId":"a1b2c3d4-1234-5678-9012-abcdef123456","balance":0}

# Пополнение (используйте реальный UUID из предыдущего шага)
curl -X POST http://localhost:8080/api/v1/wallet \
  -H "Content-Type: application/json" \
  -d '{
    "valletId": "a1b2c3d4-1234-5678-9012-abcdef123456",
    "operationType": "DEPOSIT",
    "amount": 1000
  }'

# Проверка баланса
curl http://localhost:8080/api/v1/wallets/a1b2c3d4-1234-5678-9012-abcdef123456
