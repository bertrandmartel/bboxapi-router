# Authentication status

```kotlin
val bboxapi = BboxApi()

println("authentication attempts : ${bboxapi.attempts}")
println("user is authenticated   : ${bboxapi.authenticated}")
println("user is blocked         : ${bboxapi.blocked}")
println("ban expiration date     : ${bboxapi.blockedUntil}")
```

```java
BboxApi bboxapi = new BboxApi();

System.out.println("authentication attempts : " + bboxapi.getAttempts());
System.out.println("user is authenticated   : " + bboxapi.getAuthenticated());
System.out.println("user is blocked         : " + bboxapi.getBlocked());
System.out.println("ban expiration date     : " + bboxapi.getBlockedUntil());
```

There is an anti-bruteforce mechanism that prevents user from entering invalid password multiple times. When more than 2 attempts have been issued the next failure will report the next one by 120 seconds (unless you reboot the Bbox).

The following variables provide authentication status :

| variable                | type     | default value | description |
|-------------------------|----------|---------------|-------------|
| `bboxapi.blocked`       | Boolean  | `false`       | true if there have been too many attempts |
| `bboxapi.blockedUntil`  | Date     | `Date()`      | ban expiration date |
| `bboxapi.attempts`      | Int      | `0`           | number of attempts so far |
| `bboxapi.authenticated` | Boolean  | `false`       | true if authenticated |
