# Backend курсового проекта: «Расчёт спутниковой трассы»

Данный репозиторий содержит серверную часть курсового проекта.
Реализована на Java + Spring Boot, в качестве СУБД используется PostgreSQL.
Предоставляет REST API для управления справочными данными (геолокация, спутники, антенны, конвертеры) и
выполнения расчёта уровня сигнала по трассе «спутник → антенна → конвертер».

## Быстрый старт
### Требования
- Java 17+
- Maven 3.8+
- PostgreSQL 14+


### Локальный запуск
Сервер поднимется на http://localhost:8080
```
# Клонировать репозиторий
git clone https://github.com/PusySultan/course_work_satellite_engine.git

# Настроить подключение к БД в application.properties
# spring.datasource.url=jdbc:postgresql://localhost:5432/satcalc
# spring.datasource.username=postgres
# spring.datasource.password=yourpassword

# Собрать и запустить
mvn spring-boot:run
```

### API Эндпоинты
    Базовый путь: ``` /api ```

1. Геолокация (Locality)
   #### Создать геолокацию
    ```shell
    POST /locality/create
    ```
   Тело:
    ```json5
    {
      "name": "Saransk",
      "latitude": {
        "degrees": 54,
        "minutes": 11,
        "seconds": 1.7
      },
      "longitude": {
        "degrees": 45,
        "minutes": 10,
        "seconds": 29.6
      }
    }
    ```

   #### Получить геолокацию по имени
    ```shell
    GET /locality/get/name?name=Saransk
    ```

2. Спутник (Satellite)
   #### Создать спутник
    ```shell
    POST /satellite/create
    ```
   Тело:
    ```json5
    {
      "satelliteName": "Экспресс АТ2",
      "EIRP": 50,
      "carrierFrequency": 12.149,
      "latitude": {
        "degrees": 0,
        "minutes": 0,
        "seconds": 0
      },
      "longitude": {
        "degrees": 56,
        "minutes": 0,
        "seconds": 0
      }
    }
    ```

    - EIRP – эквивалентная изотропно-излучаемая мощность (дБВт).
        - carrierFrequency – частота несущей (ГГц).
        - Координаты – положение спутника на орбите (в долготе/широте).

   #### Поиск спутника по имени
    ```shell
    GET /satellite/get/name?name=Экспресс АТ2
    ```

3. Антенна (Antenna)
   #### Создать антенну
    ```shell
    POST /antenna/create
    ```
   Тело:
    ```json5
    {
      "name": "Supral АУМ",
      "diameter": 1,
      "efficiency": 65
    }
    ```
    - diameter – диаметр зеркала (м).
        - efficiency – КПД антенны (%).

   #### Поиск антенны по имени
    ```shell
    GET /antenna/get/name?name=Supral АУМ
    ```

4. ТВ‑конвертер (Converter)
   #### Создать конвертер
    ```shell
    POST /converter/create
    ```
   Тело:
    ```json5
    {
      "name": "LNB Quattro",
      "converterGain": 50
    }
    ```
    - converterGain – коэффициент усиления конвертера (дБ).

   #### Поиск конвертера по имени
    ```shell
    GET /converter/get/name?name=LNB Quattro
    ```

5. Расчёт трассы (Engine)

   Выполняет полный расчёт уровня сигнала:
   ЭИИМ спутника → плотность потока мощности у земли → усиление антенны → потери в фидере → усиление конвертера.

   Запрос:
    ```shell
    POST /engine
    ```
   Тело:
    ```json5
    {
      "locality": {
        "name": "Саранск"
      },
      "satellite": {
        "name": "Ямал 401"
      },
      "antenna": {
        "name": "Supral АУМ"
      },
      "converter": {
        "name": "LNB Quattro"
      },
      "override": {
      }
    }
    ```
   Поля override:
   Позволяют переопределить любые параметры без изменения справочников.
   Например, можно временно указать другой диаметр антенны или усиление конвертера:
    ```json5
    {
      "override": {
        "antennaDiameter": 1.2,
        "antennaEfficiency": 70,
        "converterGain": 55,
        "EIRP": 52.5
      }
    }
    ```
   Ответ (пример):
    ```json5
    {
      "1": 50.0,
      "2": -155.92,
      "3": -117.64,
      "4": -120.64,
      "5": -73.64
    }
    ```

## Postman
Для визуализации данных в postman scripts -> post-response вставить скрипт:
```js
// post-response скрипт для визуализации трассы сигнала (дБ)
const responseData = pm.response.json();

if (typeof responseData !== 'object' || responseData === null) {
    pm.visualizer.set("<h3>Ошибка: неверный формат данных</h3>");
    return;
}

// Преобразуем объект в массив точек с понятными названиями
const pointNames = {
    1: "📡 ЭИИМ спутника",
    2: "📡 На антенне",
    3: "📡 После усиления антенной",
    4: "🔻 Потери в тракте",
    5: "⚡ Усиление конвертера"
};

const points = Object.entries(responseData)
    .map(([key, value]) => ({ 
        id: parseInt(key, 10),
        name: pointNames[key] || `Точка ${key}`,
        value: Number(value)
    }))
    .sort((a, b) => a.id - b.id);

if (points.length === 0) {
    pm.visualizer.set("<h3>Нет данных для отображения</h3>");
    return;
}

// Статистика (по значениям в дБ)
const values = points.map(p => p.value);
const min = Math.min(...values);
const max = Math.max(...values);
const avg = values.reduce((a, b) => a + b, 0) / values.length;
const count = points.length;

// Подготовка для графика: подписи осей
const labels = points.map(p => p.name);
const series = points.map(p => p.value);

const htmlTemplate = `
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Визуализация трассы сигнала (дБ)</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    <style>
        * {
            box-sizing: border-box;
            font-family: system-ui, 'Segoe UI', 'Roboto', 'Helvetica Neue', sans-serif;
        }
        body {
            background: #f8f9fc;
            margin: 0;
            padding: 24px;
            color: #1e293b;
        }
        .dashboard {
            max-width: 1400px;
            margin: 0 auto;
        }
        .stats-container {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            margin-bottom: 32px;
        }
        .stat-card {
            background: #ffffff;
            border-radius: 24px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03), 0 1px 2px rgba(0, 0, 0, 0.05);
            flex: 1 1 200px;
            padding: 20px 16px;
            text-align: center;
            border: 1px solid #eef2f6;
        }
        .stat-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 20px rgba(0, 0, 0, 0.05);
        }
        .stat-label {
            font-size: 0.85rem;
            text-transform: uppercase;
            letter-spacing: 1px;
            font-weight: 600;
            color: #5b6e8c;
            margin-bottom: 10px;
        }
        .stat-value {
            font-size: 2.2rem;
            font-weight: 700;
            color: #0f2b3d;
            line-height: 1.2;
        }
        .stat-unit {
            font-size: 0.9rem;
            font-weight: 400;
            color: #6c7a91;
        }
        .chart-container {
            background: #ffffff;
            border-radius: 28px;
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.02), 0 1px 2px rgba(0, 0, 0, 0.05);
            padding: 20px 16px 16px 16px;
            margin-bottom: 32px;
            border: 1px solid #edf2f7;
        }
        .chart-title {
            font-weight: 600;
            font-size: 1.1rem;
            margin-bottom: 18px;
            padding-left: 8px;
            color: #1e2f44;
            border-left: 4px solid #88b0d4;
        }
        canvas {
            max-height: 400px;
            width: 100%;
        }
        .table-wrapper {
            background: #ffffff;
            border-radius: 28px;
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.02), 0 1px 2px rgba(0, 0, 0, 0.05);
            padding: 20px 0 8px 0;
            border: 1px solid #edf2f7;
            overflow-x: auto;
        }
        .table-title {
            font-weight: 600;
            font-size: 1rem;
            margin: 0 20px 16px 20px;
            color: #1e2f44;
            border-left: 4px solid #88b0d4;
            padding-left: 12px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.85rem;
        }
        th {
            text-align: left;
            padding: 12px 20px;
            background-color: #f9fbfd;
            color: #2c3e55;
            font-weight: 600;
            border-bottom: 1px solid #e2e8f0;
        }
        td {
            padding: 10px 20px;
            border-bottom: 1px solid #f0f4f9;
            color: #1f2d44;
        }
        tr:hover td {
            background-color: #fef9e6;
        }
        .point-name {
            font-weight: 600;
            color: #2c5a7a;
        }
        .value-negative {
            color: #c96b6b;
            font-weight: 500;
        }
        .value-positive {
            color: #3f8c6b;
        }
        footer {
            text-align: center;
            font-size: 0.7rem;
            color: #97a9c2;
            margin-top: 24px;
        }
    </style>
</head>
<body>
<div class="dashboard">
    <div class="stats-container">
        <div class="stat-card">
            <div class="stat-label">🔢 Количество точек</div>
            <div class="stat-value">${count}</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">📉 Минимум (дБ)</div>
            <div class="stat-value">${min.toFixed(2)}</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">📈 Максимум (дБ)</div>
            <div class="stat-value">${max.toFixed(2)}</div>
        </div>
        <div class="stat-card">
            <div class="stat-label">⚖️ Среднее (дБ)</div>
            <div class="stat-value">${avg.toFixed(2)}</div>
        </div>
    </div>

    <div class="chart-container">
        <div class="chart-title">📡 Уровень сигнала по тракту (дБ) – плавная заливка</div>
        <canvas id="routeChart" width="800" height="350" style="width:100%; height:auto; max-height:380px"></canvas>
    </div>

    <div class="table-wrapper">
        <div class="table-title">📍 Детализация по точкам измерения</div>
        <table>
            <thead>
                <tr><th>Точка измерения</th><th>Значение (дБ)</th></tr>
            </thead>
            <tbody>
                ${points.map(p => {
                    const val = p.value;
                    const valueClass = val >= 0 ? 'value-positive' : 'value-negative';
                    return `<tr>
                        <td class="point-name">${p.name}</td>
                        <td class="${valueClass}">${val.toFixed(4)}</td>
                    </tr>`;
                }).join('')}
            </tbody>
        </table>
    </div>
    <footer>Единицы измерения: дБ · данные получены через API · Postman визуализация</footer>
</div>
<script>
    (function() {
        const ctx = document.getElementById('routeChart').getContext('2d');
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: ${JSON.stringify(labels)},
                datasets: [{
                    label: 'Уровень сигнала (дБ)',
                    data: ${JSON.stringify(series)},
                    borderColor: '#5f9bc2',
                    backgroundColor: 'rgba(95, 155, 194, 0.12)',
                    borderWidth: 2.5,
                    pointRadius: 4,
                    pointHoverRadius: 7,
                    pointBackgroundColor: '#ffffff',
                    pointBorderColor: '#3c7a9e',
                    pointBorderWidth: 2,
                    fill: true,
                    tension: 0.3,
                    cubicInterpolationMode: 'monotone'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    tooltip: { 
                        backgroundColor: '#ffffff',
                        titleColor: '#1e2f44',
                        bodyColor: '#2c5a7a',
                        borderColor: '#cbdde9',
                        borderWidth: 1,
                        callbacks: {
                            label: (context) => \`Уровень: \${context.raw.toFixed(4)} дБ\`
                        }
                    }
                },
                scales: {
                    y: {
                        grid: { color: '#eef2f6', drawBorder: false },
                        title: { display: true, text: 'Уровень (дБ)', color: '#6f85a3' },
                        ticks: { color: '#344e6c' }
                    },
                    x: {
                        grid: { display: false },
                        title: { display: true, text: 'Этап тракта', color: '#6f85a3' },
                        ticks: { color: '#344e6c', maxRotation: 25, minRotation: 20 }
                    }
                },
                elements: {
                    line: { borderJoin: 'round' }
                }
            }
        });
    })();
</script>
</body>
</html>
`;

pm.visualizer.set(htmlTemplate);
```



