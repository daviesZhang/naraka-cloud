package com.davies.naraka.puppeteer;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;

/**
 * @author davies
 * @date 2022/3/14 8:35 PM
 */
public class ReactorTest {


    // final  ExecutorService executor = Executors.newFixedThreadPool();
    @Test
    public void test() throws IOException {

        Flux.range(1, 10).subscribe(next -> {
            System.out.println("next = " + next);
        });
        System.in.read();
    }
}
