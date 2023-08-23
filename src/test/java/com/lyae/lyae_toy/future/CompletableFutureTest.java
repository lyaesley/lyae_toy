package com.lyae.lyae_toy.future;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompletableFutureTest {

    @Test
    @DisplayName("반환값이 없는 경우")
    void runAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            String someDB = getSomeDB("반환값이없다.");
            System.out.println("someDB = " + someDB);
            System.out.println("Thread: " + Thread.currentThread().getName());
        });

        future.get();
        System.out.println("Thread: " + Thread.currentThread().getName());
    }

    @Test
    @DisplayName("반환값이 있는 경우")
    void supplyAsync() throws ExecutionException, InterruptedException {

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            String someDB = getSomeDB("반환값이있다.");
            System.out.println("someDB = " + someDB);
            return "Thread: " + Thread.currentThread().getName();
        });

        System.out.println(future.get());
        System.out.println("Thread: " + Thread.currentThread().getName());
    }

    @Test
    @DisplayName("반환값을 받지 않고 콜백 실행")
    void thenRun() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            String someDB = getSomeDB("run");
            System.out.println("someDB = " + someDB);
            return "Thread: " + Thread.currentThread().getName();
        }).thenRun(() -> {
            System.out.println("반환값을 받지 않고 콜백 실행");
            System.out.println("Thread: " + Thread.currentThread().getName());
        });

        future.get();
    }

    @Test
    void allOf() throws ExecutionException, InterruptedException {
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> {
            return "Hello";
        });

        CompletableFuture<String> mangKyu = CompletableFuture.supplyAsync(() -> {
            return "MangKyu";
        });

        List<CompletableFuture<String>> futures = List.of(hello, mangKyu);

        CompletableFuture<List<String>> result = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .thenApply(v -> futures.stream().
                        map(CompletableFuture::join).
                        collect(Collectors.toList()));

        result.get().forEach(System.out::println);
    }

    @Test
    public void getAllOfSelect() throws ExecutionException, InterruptedException {

        List<CompletableFuture<String>> futures = List.of(
                CompletableFuture.supplyAsync(() -> getSomeDB("A")),
                CompletableFuture.supplyAsync(() -> getSomeDB("B")),
                CompletableFuture.supplyAsync(() -> getSomeDB("C"))
        );

        CompletableFuture<List<String>> result = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .thenApply(v -> futures.stream().
                        map(CompletableFuture::join).
                        collect(Collectors.toList()));

        result.get().forEach(System.out::println);
    }

    @Test
    public void getAllOfSelect2() throws ExecutionException, InterruptedException {

        List<CompletableFuture<String>> futures = List.of(
                CompletableFuture.supplyAsync(() -> getSomeDB("A")),
                CompletableFuture.supplyAsync(() -> getSomeDB("B")),
                CompletableFuture.supplyAsync(() -> getSomeDB("C"))
        );

        CompletableFuture<Void> result = CompletableFuture.allOf(futures.get(0), futures.get(1));

        result.get();
        System.out.println("완료");
    }


    @Test
    public void getResult() {

        // Create Stream of tasks:
        Stream<Supplier<String>> tasks = Stream.of(
                () -> getSomeDB("A"),
                () -> getSomeDB("B"),
                () -> getSomeDB("C"));

        List<String> lists = tasks
                // Supply all the tasks for execution and collect CompletableFutures
                .map(CompletableFuture::supplyAsync).collect(Collectors.toList())
                // Join all the CompletableFutures to gather the results
                .stream()
                .map(CompletableFuture::join).collect(Collectors.toList());
        System.out.println("1111111111");
        System.out.println(lists);
        System.out.println("222222222222222");
    }

    @Test
    public void getResult2() {

        // Create Stream of tasks:
        Stream<Runnable> tasks = Stream.of(
                () -> getSomeDB("A"),
                () -> getSomeDB("B"),
                () -> getSomeDB("C"));

        List<Void> lists = tasks.map(CompletableFuture::runAsync)
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join).collect(Collectors.toList());

        System.out.println("1111111111");
        System.out.println(lists);
        System.out.println("222222222222222");
    }

    private <T> T getSomeDB(T data) {
        System.out.println("함수 시작");
        try {
            System.out.println("로직 시작");
            Thread.sleep(2000L);
            System.out.println("로직 종료");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("함수 종료");
        return data;
    }
}




