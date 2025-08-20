package com.example.shop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ApiWelcomeController {

    @GetMapping(produces = "text/html")
    public String welcome() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Shop API</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            margin: 40px;
                            display: flex;
                            flex-direction: column;
                            align-items: center;
                        }
                        .header {
                            color: #2c3e50;
                            text-align: center;
                        }
                        .endpoints-container {
                            text-align: left;
                            width: fit-content;
                            margin: 0 auto;
                        }
                        .endpoint {
                            margin: 15px 0;
                        }
                        a {
                            color: #3498db;
                            text-decoration: none;
                        }
                        a:hover {
                            text-decoration: underline;
                        }
                        .swagger-link {
                            display: inline-block;
                            margin-top: 20px;
                            padding: 10px 15px;
                            background: #49cc90;
                            color: white;
                            border-radius: 4px;
                            font-weight: bold;
                            text-align: center;
                        }
                    </style>
                </head>
                <body>
                    <div class="header">
                        <h1>🛍️ Добро пожаловать в Shop API!</h1>
                        <p>Версия 1.0.0</p>
                    </div>

                    <div class="endpoints-container">
                        <h2>📋 Доступные эндпоинты:</h2>
                        <div class="endpoint">
                            <a href='/swagger-ui/index.html#/client-controller'>/api/clients</a> - управление клиентами
                        </div>
                        <div class="endpoint">
                            <a href='/swagger-ui/index.html#/product-controller'>/api/product</a> - управление продуктами
                        </div>
                        <div class="endpoint">
                            <a href='/swagger-ui/index.html#/image-controller'>/api/image</a> - работа с изображениями
                        </div>
                        <div class="endpoint">
                            <a href='/swagger-ui/index.html#/supplier-controller'>/api/supplier</a> - управление поставщиками
                        </div>
                    </div>

                    <a href='/swagger-ui/index.html' class="swagger-link">
                        📚 Открыть Swagger документацию
                    </a>
                </body>
                </html>
                """;
    }

}