BACKEND
    
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class EduCoachServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/coach", new CoachHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("EduCoach AI Java backend running on port 8080");
    }

    static class CoachHandler implements HttpHandler {
public void handle(HttpExchange exchange) throws IOException {

    // Allow CORS
    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

    // Handle preflight request
    if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
        exchange.sendResponseHeaders(204, -1);
        return;
    }

    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
        exchange.sendResponseHeaders(405, -1);
        return;
    }

    InputStream is = exchange.getRequestBody();
    String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

    String message = extractValue(body, "message");
    String grade = extractValue(body, "grade");
    String language = extractValue(body, "language");

    String reply = generateAdvice(message, grade, language);

    String jsonResponse = "{ \"reply\": \"" + reply.replace("\"", "\\\"") + "\" }";

    exchange.getResponseHeaders().add("Content-Type", "application/json");
    exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);

    OutputStream os = exchange.getResponseBody();
    os.write(jsonResponse.getBytes());
    os.close();
}

        private static String extractValue(String json, String key) {
            String pattern = "\"" + key + "\":\"";
            int start = json.indexOf(pattern);
            if (start == -1) return "";
            start += pattern.length();
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        }

        private static String generateAdvice(String message, String grade, String language) {

            return "For " + grade + " in " + language + " medium: "
                    + "Try a short interactive activity related to the topic. "
                    + "Ask students to discuss in pairs, use local examples, "
                    + "and break the concept into small steps. "
                    + "If behavior is an issue, give a quick energizer and set clear rules.";
        }
    }
}
