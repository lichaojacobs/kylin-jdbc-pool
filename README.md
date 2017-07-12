# kylin-jdbc-pool
better performance for kylin query

## versions
1.0.0 —— jdbc pool & RowMapper 

## how to use ?

### first configure settings 

```
kylin:
  userName: admin
  password: KYLIN
  decrypt: true
  connectionUrl: jdbc:kylin://host:7070/project_name
  poolSize: 10

```

### then try your business 

```
@RunWith(SpringRunner.class)
@SpringBootTest
public class KylinJdbcPoolApplicationTests {

  @Resource(name = "kylinJdbcTemplate")
  JdbcTemplate jdbcTemplate;

  @Test
  public void test() {
    int countResult = jdbcTemplate
        .queryForObject("select count(*) from SCHEMA.table",
            (resultSet, i) -> {
              return resultSet.getInt(1);
            });

    System.out.println("testResult: " + countResult);
  }

  @Test
  public void testRowMapper() {
    List<Demo> demoList = jdbcTemplate
        .query("select * from from SCHEMA.table limit 10",
            KylinRowMapper.getDefault(
                Demo.class));

    System.out.println(JSON.toJSONString(demoList));
  }

}

```
