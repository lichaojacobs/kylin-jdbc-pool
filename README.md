# kylin-jdbc-pool
better performance for kylin query with spring boot project

## spring boot version

- 1.5.4.RELEASE

-
## versions

- 1.0.0 —— jdbc pool & RowMapper 
- 1.1.0 —— support muti data sources
- 1.1.1 —— support gson SerializedName annotation
- 2.0.0 —— refactor project architecture

## how to use ?

### first configure settings 

```
kylin:
  project1:
    userName: admin
    password: KYLIN
    decrypt: true
    connectionUrl: jdbc:kylin://host:7070/project1
    maxWaitTime: 10000 #ms
  project2:
    userName: admin
    password: KYLIN
    decrypt: true
    connectionUrl: jdbc:kylin://host:7070/project2
    maxWaitTime: 10000 #ms

```

### then try your business 

```
@RunWith(SpringRunner.class)
@SpringBootTest
public class KylinJdbcPoolApplicationTests {

  @Resource(name = "project1JdbcTemplate")
  JdbcTemplate project1JdbcTemplate;
  @Resource(name = "project2JdbcTemplate")
  JdbcTemplate project2JdbcTemplate;

  @Test
  public void test() {
    int countResult = project1JdbcTemplate
        .queryForObject("select count(*) from SCHEMA.table",
            (resultSet, i) -> {
              return resultSet.getInt(1);
            });

    System.out.println("testResult: " + countResult);
  }

  @Test
  public void testRowMapper() {
    List<Demo> demoList = project1JdbcTemplate
        .query("select * from from SCHEMA.table limit 10",
            KylinRowMapper.getDefault(
                Demo.class));

    System.out.println(JSON.toJSONString(demoList));
  }

}

```
