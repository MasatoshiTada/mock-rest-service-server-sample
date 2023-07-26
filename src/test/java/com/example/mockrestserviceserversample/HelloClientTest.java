package com.example.mockrestserviceserversample;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.junit.jupiter.api.Assertions.*;

@RestClientTest(
        components = HelloClient.class,
        properties = "hello-service.base-url=http://localhost"
)
class HelloClientTest {

    @Autowired
    HelloClient helloClient;

    @Autowired
    MockRestServiceServer server;

    @Nested
    @DisplayName("getHello()")
    class GetHelloTest {
        @Test
        @DisplayName("GETリクエストを送信してサーバーから200が返ると、レスポンスボディをHelloResponseで受け取れる")
        void success() {
            // モックサーバーを設定する
            server.expect(requestTo("/api/hello"))  // このURLに
                    .andExpect(method(HttpMethod.GET))  // GETリクエストすると
                    .andRespond(withSuccess("""
                            {"message":"hello"}
                            """, MediaType.APPLICATION_JSON));  // 200 OKとこんなJSONを返すよう設定する
            // テスト実行＋アサーション
            HelloResponse actual = helloClient.getHello();
            assertEquals("hello", actual.message());
        }

        @Test
        @DisplayName("GETリクエストを送信してサーバーから500が返ると、RuntimeExceptionがスローされる")
        void error() {
            // モックサーバーを設定する
            server.expect(requestTo("/api/hello"))  // このURLに
                    .andExpect(method(HttpMethod.GET))  // GETリクエストすると
                    .andRespond(withServerError());  // 500が返る
            // テスト実行＋アサーション
            assertThrows(RuntimeException.class, () -> helloClient.getHello());
        }
    }

    @Nested
    @DisplayName("postHello()")
    class PostHelloTest {
        @Test
        @DisplayName("JSONをPOSTしてサーバーから200が返ると、'OK'が返る")
        void success() {
            server.expect(requestTo("/api/hello"))  // このURLに
                    .andExpect(method(HttpMethod.POST))  // POSTリクエストで
                    .andExpect(content().json("""
                            {"message":"hello"}
                            """))  // こんなJSONを送信すると
                    .andRespond(withStatus(HttpStatus.OK));  // 200が返るよう設定する
            // テスト実行＋アサーション
            String actual = helloClient.postHello(new HelloRequest("hello"));
            assertEquals("OK", actual);
        }

        @Test
        @DisplayName("JSONをPOSTしてサーバーから500が返ると、RuntimeExceptionがスローされる")
        void error() {
            server.expect(requestTo("/api/hello"))  // このURLに
                    .andExpect(method(HttpMethod.POST))  // POSTリクエストで
                    .andExpect(content().json("""
                            {"message":"hello"}
                            """))  // こんなJSONを送信すると
                    .andRespond(withServerError());  // 500が返るように設定する
            // テスト実行＋アサーション
            assertThrows(RuntimeException.class, () -> helloClient.postHello(new HelloRequest("hello")));
        }
    }
}
