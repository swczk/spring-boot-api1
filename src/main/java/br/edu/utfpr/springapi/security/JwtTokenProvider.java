package br.edu.utfpr.springapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {
   private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

   @Value("${aws.cognito.userPoolId}")
   private String userPoolId;

   @Value("${aws.cognito.url}")
   private String cognitoUrl;

   private final ObjectMapper objectMapper = new ObjectMapper();

   public JwtTokenProvider() {
   }

   public Map<String, Object> parseToken(String token) {
      try {
         String[] chunks = token.split("\\.");
         if (chunks.length < 2) {
            return new HashMap<>();
         }

         Base64.Decoder decoder = Base64.getUrlDecoder();
         String payload = new String(decoder.decode(chunks[1]));

         @SuppressWarnings("unchecked")
         Map<String, Object> result = objectMapper.readValue(payload, Map.class);
         return result;

      } catch (Exception e) {
         logger.error("Error parsing JWT token", e);
         return new HashMap<>();
      }
   }

   public boolean validateToken(String token) {
      try {
         Map<String, Object> claims = parseToken(token);

         // Basic validation
         if (claims.isEmpty()) {
            return false;
         }

         // Check expiration
         if (claims.containsKey("exp")) {
            Date expiration = new Date(((Number) claims.get("exp")).longValue() * 1000);
            if (expiration.before(new Date())) {
               return false;
            }
         }

         // Check issuer
         if (claims.containsKey("iss")) {
            String issuer = (String) claims.get("iss");
            String expectedIssuer = "https://cognito-idp.sa-east-1.amazonaws.com/" + userPoolId;
            if (!issuer.equals(expectedIssuer)) {
               return false;
            }
         }

         return true;
      } catch (Exception e) {
         logger.error("Error validating token", e);
         return false;
      }
   }

   public String extractUsername(String token) {
      Map<String, Object> claims = parseToken(token);
      return (String) claims.get("username");
   }
}
