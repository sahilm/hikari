# HikariCP testing
Test of connection validation under load. The findings are as follows:
1. Given enough load connection validations will stop because connections are reused within [aliveBypassWindowMs](https://github.com/brettwooldridge/HikariCP/blob/0a6ccdb334b2ecde25ae090034669d534736a0de/src/main/java/com/zaxxer/hikari/pool/HikariPool.java#L65). `keepAliveTime` can be used to ensure validations under this heavy load but the minimum keepAliveTimer is 30 seconds.
2. Beneath this threshold load connection validations are performed on connection checkout (when last query was executed > aliveBypassWindowMs) with all default HikariCP settings including maxLifetime of 30 minutes.

## Usage

```bash
docker-compose -d up
./gradlew run
```

### Handy scripts

```bash
./make_mysql_readonly.sh
./make_mysql_writable.sh
./watch_counter.sh # watches the counter update in the DB
```

### Sample output

```
10:06:53.537 [main] DEBUG com.zaxxer.hikari.HikariConfig - HikariPool-1 - configuration:
10:06:53.541 [main] DEBUG com.zaxxer.hikari.HikariConfig - allowPoolSuspension................................false
10:06:53.541 [main] DEBUG com.zaxxer.hikari.HikariConfig - autoCommit................................true
10:06:53.541 [main] DEBUG com.zaxxer.hikari.HikariConfig - catalog................................none
10:06:53.541 [main] DEBUG com.zaxxer.hikari.HikariConfig - connectionInitSql................................none
10:06:53.541 [main] DEBUG com.zaxxer.hikari.HikariConfig - connectionTestQuery................................none
10:06:53.542 [main] DEBUG com.zaxxer.hikari.HikariConfig - connectionTimeout................................30000
10:06:53.542 [main] DEBUG com.zaxxer.hikari.HikariConfig - dataSource................................org.example.WritableDataSource@69e153c5
10:06:53.542 [main] DEBUG com.zaxxer.hikari.HikariConfig - dataSourceClassName................................none
10:06:53.542 [main] DEBUG com.zaxxer.hikari.HikariConfig - dataSourceJNDI................................none
10:06:53.542 [main] DEBUG com.zaxxer.hikari.HikariConfig - dataSourceProperties................................{password=<masked>}
10:06:53.542 [main] DEBUG com.zaxxer.hikari.HikariConfig - driverClassName................................none
10:06:53.542 [main] DEBUG com.zaxxer.hikari.HikariConfig - exceptionOverrideClassName................................none
10:06:53.542 [main] DEBUG com.zaxxer.hikari.HikariConfig - healthCheckProperties................................{}
10:06:53.543 [main] DEBUG com.zaxxer.hikari.HikariConfig - healthCheckRegistry................................none
10:06:53.543 [main] DEBUG com.zaxxer.hikari.HikariConfig - idleTimeout................................600000
10:06:53.543 [main] DEBUG com.zaxxer.hikari.HikariConfig - initializationFailTimeout................................1
10:06:53.543 [main] DEBUG com.zaxxer.hikari.HikariConfig - isolateInternalQueries................................false
10:06:53.543 [main] DEBUG com.zaxxer.hikari.HikariConfig - jdbcUrl................................none
10:06:53.543 [main] DEBUG com.zaxxer.hikari.HikariConfig - keepaliveTime................................0
10:06:53.543 [main] DEBUG com.zaxxer.hikari.HikariConfig - leakDetectionThreshold................................0
10:06:53.543 [main] DEBUG com.zaxxer.hikari.HikariConfig - maxLifetime................................1800000
10:06:53.543 [main] DEBUG com.zaxxer.hikari.HikariConfig - maximumPoolSize................................10
10:06:53.544 [main] DEBUG com.zaxxer.hikari.HikariConfig - metricRegistry................................none
10:06:53.544 [main] DEBUG com.zaxxer.hikari.HikariConfig - metricsTrackerFactory................................none
10:06:53.544 [main] DEBUG com.zaxxer.hikari.HikariConfig - minimumIdle................................10
10:06:53.544 [main] DEBUG com.zaxxer.hikari.HikariConfig - password................................<masked>
10:06:53.544 [main] DEBUG com.zaxxer.hikari.HikariConfig - poolName................................"HikariPool-1"
10:06:53.544 [main] DEBUG com.zaxxer.hikari.HikariConfig - readOnly................................false
10:06:53.544 [main] DEBUG com.zaxxer.hikari.HikariConfig - registerMbeans................................false
10:06:53.544 [main] DEBUG com.zaxxer.hikari.HikariConfig - scheduledExecutor................................none
10:06:53.544 [main] DEBUG com.zaxxer.hikari.HikariConfig - schema................................none
10:06:53.544 [main] DEBUG com.zaxxer.hikari.HikariConfig - threadFactory................................internal
10:06:53.544 [main] DEBUG com.zaxxer.hikari.HikariConfig - transactionIsolation................................default
10:06:53.544 [main] DEBUG com.zaxxer.hikari.HikariConfig - username................................none
10:06:53.544 [main] DEBUG com.zaxxer.hikari.HikariConfig - validationTimeout................................5000
10:06:53.549 [main] INFO  com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Starting...
10:06:53.750 [main] INFO  org.example.WritableConnection - validating...
10:06:53.757 [main] INFO  org.example.WritableConnection - isValid=true
10:06:53.758 [main] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.example.WritableConnection@68b6f0d6
10:06:53.759 [main] INFO  com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Start completed.
10:06:53.810 [DefaultDispatcher-worker-2] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 9, queue 10
10:06:53.814 [DefaultDispatcher-worker-4] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 9, queue 10
10:06:53.817 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.example.WritableConnection@362363de
10:06:53.818 [DefaultDispatcher-worker-5] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 8, queue 9
10:06:53.820 [DefaultDispatcher-worker-7] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 8, queue 9
10:06:53.822 [DefaultDispatcher-worker-8] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 8, queue 10
10:06:53.823 [DefaultDispatcher-worker-9] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 8, queue 10
10:06:53.824 [DefaultDispatcher-worker-10] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 8, queue 10
10:06:53.825 [DefaultDispatcher-worker-1] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 8, queue 10
10:06:53.826 [DefaultDispatcher-worker-3] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 8, queue 10
10:06:53.827 [DefaultDispatcher-worker-2] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 8, queue 10
10:06:53.828 [DefaultDispatcher-worker-3] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 8, queue 10
10:06:53.829 [DefaultDispatcher-worker-5] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 8, queue 10
10:06:53.830 [DefaultDispatcher-worker-7] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 8, queue 10
10:06:53.830 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.example.WritableConnection@771e69e5
10:06:53.831 [DefaultDispatcher-worker-6] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 7, queue 9
10:06:53.832 [DefaultDispatcher-worker-8] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 7, queue 9
10:06:53.833 [DefaultDispatcher-worker-9] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 7, queue 9
10:06:53.835 [DefaultDispatcher-worker-10] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 7, queue 9
10:06:53.835 [DefaultDispatcher-worker-1] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 7, queue 9
10:06:53.837 [DefaultDispatcher-worker-9] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 7, queue 9
10:06:53.838 [DefaultDispatcher-worker-2] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 7, queue 9
10:06:53.839 [DefaultDispatcher-worker-3] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 7, queue 9
10:06:53.840 [DefaultDispatcher-worker-9] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 7, queue 9
10:06:53.841 [DefaultDispatcher-worker-7] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 7, queue 9
10:06:53.842 [DefaultDispatcher-worker-3] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 6, queue 9
10:06:53.842 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.example.WritableConnection@46237f85
10:06:53.844 [DefaultDispatcher-worker-8] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 6, queue 8
10:06:53.845 [DefaultDispatcher-worker-4] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 6, queue 8
10:06:53.847 [DefaultDispatcher-worker-3] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 6, queue 8
10:06:53.847 [DefaultDispatcher-worker-10] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 6, queue 8
10:06:53.848 [DefaultDispatcher-worker-5] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 6, queue 8
10:06:53.849 [DefaultDispatcher-worker-4] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 6, queue 8
10:06:53.850 [DefaultDispatcher-worker-3] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 6, queue 8
10:06:53.851 [DefaultDispatcher-worker-10] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 6, queue 8
10:06:53.852 [DefaultDispatcher-worker-7] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 6, queue 8
10:06:53.853 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.example.WritableConnection@173196aa
10:06:53.854 [DefaultDispatcher-worker-1] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 5, queue 7
10:06:53.855 [DefaultDispatcher-worker-8] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 5, queue 7
10:06:53.856 [DefaultDispatcher-worker-2] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 5, queue 7
10:06:53.857 [DefaultDispatcher-worker-6] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 5, queue 7
10:06:53.858 [DefaultDispatcher-worker-9] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 5, queue 7
10:06:53.859 [DefaultDispatcher-worker-5] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 5, queue 7
10:06:53.860 [DefaultDispatcher-worker-4] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 5, queue 7
10:06:53.861 [DefaultDispatcher-worker-2] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 5, queue 7
10:06:53.861 [DefaultDispatcher-worker-10] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 5, queue 7
10:06:53.862 [DefaultDispatcher-worker-7] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 5, queue 7
10:06:53.863 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Pool stats (total=5, active=5, idle=0, waiting=5)
10:06:53.863 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Fill pool skipped, pool is at sufficient level.
10:06:53.864 [DefaultDispatcher-worker-1] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 5, queue 7
10:06:53.865 [DefaultDispatcher-worker-8] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 5, queue 7
10:06:53.865 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.example.WritableConnection@137f6d3a
10:06:53.866 [DefaultDispatcher-worker-3] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 4, queue 6
10:06:53.867 [DefaultDispatcher-worker-6] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 4, queue 6
10:06:53.868 [DefaultDispatcher-worker-9] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 4, queue 6
10:06:53.870 [DefaultDispatcher-worker-5] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 4, queue 6
10:06:53.871 [DefaultDispatcher-worker-4] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 4, queue 6
10:06:53.872 [DefaultDispatcher-worker-2] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 4, queue 6
10:06:53.873 [DefaultDispatcher-worker-10] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 4, queue 6
10:06:53.874 [DefaultDispatcher-worker-7] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 4, queue 6
10:06:53.875 [DefaultDispatcher-worker-1] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 4, queue 6
10:06:53.876 [DefaultDispatcher-worker-8] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 4, queue 6
10:06:53.876 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.example.WritableConnection@5a10d19c
10:06:53.877 [DefaultDispatcher-worker-3] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 3, queue 5
10:06:53.878 [DefaultDispatcher-worker-6] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 3, queue 5
10:06:53.879 [DefaultDispatcher-worker-9] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 3, queue 5
10:06:53.880 [DefaultDispatcher-worker-5] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 3, queue 5
10:06:53.881 [DefaultDispatcher-worker-4] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 3, queue 5
10:06:53.882 [DefaultDispatcher-worker-2] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 3, queue 5
10:06:53.883 [DefaultDispatcher-worker-10] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 3, queue 5
10:06:53.884 [DefaultDispatcher-worker-7] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 3, queue 5
10:06:53.885 [DefaultDispatcher-worker-1] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 3, queue 5
10:06:53.886 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.example.WritableConnection@3b75971c
10:06:53.886 [DefaultDispatcher-worker-8] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 2, queue 4
10:06:53.887 [DefaultDispatcher-worker-3] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 2, queue 4
10:06:53.888 [DefaultDispatcher-worker-6] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 2, queue 4
10:06:53.889 [DefaultDispatcher-worker-9] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 2, queue 4
10:06:53.890 [DefaultDispatcher-worker-5] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 2, queue 4
10:06:53.890 [DefaultDispatcher-worker-4] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 2, queue 4
10:06:53.891 [DefaultDispatcher-worker-2] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 2, queue 4
10:06:53.893 [DefaultDispatcher-worker-10] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 2, queue 4
10:06:53.894 [DefaultDispatcher-worker-7] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 2, queue 4
10:06:53.895 [DefaultDispatcher-worker-1] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 2, queue 4
10:06:53.896 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.example.WritableConnection@7141e3c3
10:06:53.896 [DefaultDispatcher-worker-8] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 1, queue 3
10:06:53.898 [DefaultDispatcher-worker-3] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 1, queue 3
10:06:53.899 [DefaultDispatcher-worker-6] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 1, queue 3
10:06:53.900 [DefaultDispatcher-worker-9] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 1, queue 3
10:06:53.901 [DefaultDispatcher-worker-2] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 1, queue 3
10:06:53.902 [DefaultDispatcher-worker-4] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 1, queue 3
10:06:53.903 [DefaultDispatcher-worker-5] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 1, queue 3
10:06:53.905 [DefaultDispatcher-worker-10] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 1, queue 3
10:06:53.907 [DefaultDispatcher-worker-7] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 1, queue 3
10:06:53.908 [DefaultDispatcher-worker-8] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 1, queue 3
10:06:53.910 [DefaultDispatcher-worker-3] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Add connection elided, waiting 1, queue 3
10:06:53.910 [HikariPool-1 connection adder] DEBUG com.zaxxer.hikari.pool.HikariPool - HikariPool-1 - Added connection org.example.WritableConnection@6c2d8272
10:06:53.940 [DefaultDispatcher-worker-9] INFO  Main - counter=91
10:06:53.940 [DefaultDispatcher-worker-2] INFO  Main - counter=92
10:06:54.916 [DefaultDispatcher-worker-8] INFO  org.example.WritableConnection - validating...
10:06:54.916 [DefaultDispatcher-worker-9] INFO  org.example.WritableConnection - validating...
10:06:54.918 [DefaultDispatcher-worker-9] INFO  org.example.WritableConnection - isValid=true
10:06:54.918 [DefaultDispatcher-worker-8] INFO  org.example.WritableConnection - isValid=true
10:06:55.103 [DefaultDispatcher-worker-8] INFO  Main - counter=994
10:06:55.681 [DefaultDispatcher-worker-4] INFO  org.example.WritableConnection - validating...
10:06:55.681 [DefaultDispatcher-worker-10] INFO  org.example.WritableConnection - validating...
10:06:55.683 [DefaultDispatcher-worker-4] INFO  org.example.WritableConnection - isValid=true
10:06:55.683 [DefaultDispatcher-worker-10] INFO  org.example.WritableConnection - isValid=true
10:06:56.193 [DefaultDispatcher-worker-9] INFO  Main - counter=1854
```

