# Transmissible ThreadLocal

## Overview

在傳統的 Web Server 設計是所有使用者共用固定 `Thread` 的資源，所以可能出現特定幾位使用者佔用了大量 `Thread` ，導致資源分配的不平均，如果請求耗時長，使用者心急重複提交請求，容易造成服務資源耗盡和阻塞。


所以我們需要為每一位使用者分配獨立的 `Thread` 和 `Queue` 來處理請求，公平的使用 `Server` 的資源，為了達到該目的，傳統`同步`的請求，則需要改為`非同步`的請求，此時原本是用到`ThreadLocal`和`InheritableThreadLocal`暫存的資料則會消失，因此我們需要一個能不斷傳遞當前`Thread`暫存的資料給下一個`Thread`，所以設計了`TransmissibleThreadLocal`。

其實阿里巴巴已經有設計 [TransmittableThreadLocal](https://github.com/alibaba/transmittable-thread-local) 來處理資料傳遞的問題，但實現原理不是特別難，所以就手動自己來一個了。

## QuickStart

### pom.xml
```xml
<dependency>
    <groupId>io.github.babyblue94520</groupId>
    <artifactId>transmissible-thread-local</artifactId>
    <version>1.0.0-RELEASE</version>
</dependency>
```

### Usage

* `TransmissibleRunnable` & `TransmissibleCallable`

```java

import java.util.concurrent.Executors;

class Example {
    
    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    
    public static void main(String[] args) {
        threadLocal.set("main");
        
        Executors.newSingleThreadExecutor()
                .execute(TransmissibleRunnable.of(()->{
                    threadLocal.get(); // "main"
                }))
        ;
        
        Executors.newSingleThreadExecutor()
                .submit(TransmissibleCallable.of(()->{
                    threadLocal.get(); // "main"
                    return null;
                }))
        ;
    }
}

```

* `TransmissibleExecutors`

```java

import java.util.concurrent.Executors;

class Example {
    
    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    
    public static void main(String[] args) {
        TransmissibleExecutors.newSingleThreadExecutor()
                .execute(()->{
                    threadLocal.get(); // "main"
                })
        ;

        TransmissibleExecutors.newSingleThreadExecutor()
                .submit(()->{
                    threadLocal.get(); // "main"
                    return null;
                })
        ;
    }
}
```

[more...](src/test/java/com/primestar/transmissible/AbstractExecutorTest.java)
