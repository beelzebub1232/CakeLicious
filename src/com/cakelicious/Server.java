package com.cakelicious;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import com.cakelicious.handlers.AuthHandler;
import com.cakelicious.handlers.ProductHandler;
import com.cakelicious.handlers.FileHandler;

public class Server {
    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // API endpoints
        server.createContext("/api/login", new AuthHandler());
        server.createContext("/api/products", new ProductHandler());
        // TODO: Add more API contexts here e.g., /api/orders

        // Handler for static files (HTML, CSS, JS) must be the last one
        server.createContext("/", new FileHandler());

        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("CakeLicious server started on port " + port);
        System.out.println("Access the application at: http://localhost:8000");
    }
}