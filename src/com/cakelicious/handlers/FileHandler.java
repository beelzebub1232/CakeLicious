
package com.cakelicious.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath().equals("/") ? "/index.html" : uri.getPath();
        String filePath = "." + path;

        File file = new File(filePath).getCanonicalFile();

        // Basic security check to prevent directory traversal
        if (!file.getPath().startsWith(new File(".").getCanonicalPath())) {
            sendResponse(exchange, 403, "403 Forbidden".getBytes());
            return;
        }

        if (file.exists() && !file.isDirectory()) {
            String contentType = getContentType(file.getName());
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, file.length());
            try (OutputStream os = exchange.getResponseBody(); FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int count;
                while ((count = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, count);
                }
            }
        } else {
            String response = "404 (Not Found)\n";
            sendResponse(exchange, 404, response.getBytes());
        }
    }

    private String getContentType(String fileName) {
        if (fileName.endsWith(".html")) return "text/html";
        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".js")) return "application/javascript";
        return "application/octet-stream";
    }

    private void sendResponse(HttpExchange exchange, int statusCode, byte[] responseBytes) throws IOException {
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}
