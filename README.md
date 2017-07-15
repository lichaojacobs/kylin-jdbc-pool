# kylin-jdbc-pool
better performance for kylin query

## versions

- 1.0.0 —— jdbc pool & RowMapper 
- 1.1.0 —— support muti data sources

## how to use ?

### first configure settings 

```
kylin:
  project1:
    userName: admin
    password: KYLIN
    decrypt: true
    connectionUrl: jdbc:kylin://host:7070/project1
  project2:
    userName: admin
    password: KYLIN
    decrypt: true
    connectionUrl: jdbc:kylin://host:7070/project2

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
