# Spring Data Common Web
> Spring Data JPA는, Spring Data Common과 함께 Spring Data 프로젝트의 일부이다.<br>
Spring 기반 웹 프로젝트에 활용하기 위해 Spring Data Common과 Spring Data JPA의 기능을 직접 사용하며 정리한 내용이다.

## JPA 프로그래밍: 프로젝트 세팅 

스프링 부트

- 스프링 부트 v2.* 
- 스프링 프레임워크 v5.*

 

스프링 부트 스타터 JPA

- JPA 프로그래밍에 필요한 의존성 추가
  - Hibernate v5.*
  - spring-data-jpa v2.*

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

하이버네이트 의존성 덕분에 자동설정 동작.

- 자동 설정: HibernateJpaAutoConfiguration
  - EntityMangerFactoryBuilder, EntityMangerFactoryBean이 등록되어 있기 때문에 EntityManger를 bean으로 주입 받아서 사용할 수 있다.
  - 컨테이너가 관리하는 EntityManager (프록시) 빈 설정
  - PlatformTransactionManager 빈 설정
  - 즉 JPA 관련 필요한 bean들이 자동으로 등록이 된다.

Data source 설정

```properties
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.url=jdbc:mariadb://localhost:3306/springdatademo
spring.datasource.username=root
spring.datasource.password=ldcc!2626
```



 hibernate 관련 설정

> spring.jpa.hibernate.ddl-auto 값은 create, update 등이 있다.
> create: App 구동 시, DB 다시 생성
> update: App 구동 시작 시 Entity 클래스와 DB 스키마 구조를 비교해서 DB쪽에 생성되지 않은 테이블, 컬럼 추가 (제거는 하지 않음)
> validate: 이미 스키마는 생성되어져있고, 맵핑해야 할 객체의 정보가 DB에 잘 맵핑이 되는지 검증. 검증 실패 시, App 구동 시에 바로 에러 발생

```properties
spring.jpa.hibernate.ddl-auto=create
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.type.descriptor.sql=trace
```



## JPA 프로그래밍: 엔티티 맵핑

@Entity

- “엔티티”는 객체 세상에서 부르는 이름.
- 보통 클래스와 같은 이름을 사용하기 때문에 값을 변경하지 않음.

@Table

- “릴레이션" 세상에서 부르는 이름.
- Entity와 맵핑할 테이블의 이름 지정. 생략 시, 맵핑한 @Entity의 이름이 기본값.
- 테이블의 이름은 SQL에서 쓰임.

@Id

- 엔티티의 주키를 맵핑할 때 사용.

@GeneratedValue

- 해당 값이 자동 생성되도록 할 때 사용.
- 주키의 생성 방법을 맵핑하는 애노테이션
- 생성 전략과 생성기를 설정할 수 있다.
  - 기본 전략은 AUTO: 사용하는 DB에 따라 적절한 전략 선택

@Column

- unique
- nullable
- ...

 

@Temporal

- Date, Calendar 타입의 표시형식을 지정할 수 있음

 

@Transient

- 컬럼으로 맵핑하고 싶지 않은 멤버 변수에 사용.

 

```java
@Entity
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    // @Column 이 생략되어있는 것이나 마찬가지
    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();

    @Transient
    private String no;
```



## Enumerated type 맵핑

```java
@Entity
public class Comment {
    // ...
    @Enumerated(EnumType.STRING) // default는 ORDINAL (순서로 맵핑)
    private CommentState commentState;
    // ...
 }
```

```java
public enum CommentState {
    DRAFT, DELETED, PUBLISHED
}
```

 

## JPA 프로그래밍: Value 타입 맵핑

엔티티 타입과 Value 타입 구분

- 식별자가 있어야 하는가.
- 독립적으로 존재할 수 있는가.

Value 타입 종류

- 기본 타입 (String, Date, Boolean, ...)
- Composite Value 타입

Composite Value 타입 맵핑

- @Embeddable
  - 컴포짓한 value type
- @Embedded
- @AttributeOverrides
  - 주소라는 value type을 여러번 맵핑하는 경우(회사 주소, 집 주소 이런식으로)
- @AttributeOverride

 

 ```java
@Embeddable
public class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
}
 ```

```java
//Address라는 valuetype의 street 필드를, home_street으로 바꿔 쓰고 싶을 때
@Embedded
@AttributeOverrides({
	@AttributeOverride(name = "street", column = @Column(name = "home_street"))
})
private Address address;
```

집주소 직장주소 

 

## JPA 프로그래밍: 1대다 맵핑

단방향 @ManyToOne

- 기본값은 FK 생성

```java
@Entity
public class Study {
	// ...
    @ManyToOne
    private Account owner;
    // ...
```



단방향 @OneToMany

- 기본값은 조인 테이블 생성

```java
@Entity
public class Account {
	// ...
    @OneToMany
    private Set<Study> studies = new HashSet<>();
    // ...
```



관계에는 항상 두 엔티티가 존재

- 둘 중 하나는 그 관계의 주인(owning)이고
- 다른 쪽은 종속된(non-owning) 쪽
- 해당 관계의 반대쪽 레퍼런스를 가지고 있는 쪽이 주인.

 

단방향에서의 관계의 주인은 명확

- 관계를 정의한 쪽이 그 관계의 주인

 

양방향

- FK 가지고 있는 쪽이 오너 따라서 기본값은 @ManyToOne 가지고 있는 쪽이 주인.
- 주인이 아닌쪽(@OneToMany쪽)에서 mappedBy 사용해서 관계를 맺고 있는 필드를 설정

 

양방향

- @ManyToOne (이쪽이 주인)
  - 예시에서는 ``Study``가 관계의 주인.
- @OneToMany(mappedBy)
- 주인한테 관계를 설정해야 DB에 반영된다.

 ```java
@Entity
public class Account {
   //...
    @OneToMany(mappedBy = "owner") //Study 에서 owner라는 value와 양방향 관계
    private Set<Study> studies = new HashSet<>();
    // ...
 ```

```java
@Entity
public class Study { // 관계의 주인
	// ...
    @ManyToOne
    private Account owner;
    // ...
```

> account.getStudies().add(study); 
> 위와 같이, 관계의 주인이 아닌 쪽에 관계를 설정하려고 하면, 관계에 대한 정보가 저장이 되지 않는다.

따라서 아래와 같이 실행한다.

```java
public class Account{
    // ...
    public void addStudy(Study study) {
        this.getStudies().add(study);
        study.setOwner(this);
    }
    // ...
}
```





## JPA 프로그래밍: Cascade

 

![img](file:///C:/Users/ghdrj/AppData/Local/Temp/msohtmlclip1/01/clip_image002.gif)

 

**엔티티의 상태** 변화를 전파 시키는 옵션.

- Transient: JPA가 모르는 상태
- Persistent: JPA가 관리중인 상태 (1차 캐시, Dirty Checking, Write Behind, ...)
  - 1차 캐시: persistenceContext에 들어있는 상태
    - 1차 캐시되어있는 객체를 다시 조회하면 SELECT 쿼리를 실행하지 않는다.
  - Dirty checking
    - 변경 사항 감지
  - Write Behind
    - 객체의 상태 변화를 DB에 최대한 **늦게**(필요한 시점에) 반영한다.
  - 이런 면에서 성능에 장점이 있다고 보는 것.
- Detached: JPA가 더이상 관리하지 않는 상태.
  - Transaction이 끝난 이후에 다른곳으로 return 해서 사용한다든가..
- Removed: JPA가 관리하긴 하지만 삭제하기로 한 상태.
  - Commit이 일어날 때 삭제



Cascade는 이러한 상태 변화를 전이시키는 것.

- 게시글과 댓글을 예로 들면, 게시글이 persistent 상태가 되었을 때 댓글도 persistent 상태로

```java
@Entity
public class Post {    
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL) // 일부 상태변화만 적용도 가능
    private Set<Comment> comments = new HashSet<>();
```





## JPA 프로그래밍: Fetch

연관 관계의 엔티티를 어떻게 가져올 것이냐... 지금 (Eager)? 나중에(Lazy)?

- @OneToMany의 기본값은 Lazy
  - 기본적으로 게시글 정보를 읽을 때, 댓글 정보는 읽지 않지만
  - Eager로 수정한다면 댓글 정보도 조회한다.
- @ManyToOne의 기본값은 Eager
  - 댓글 정보를 읽을 때, 댓글이 작성된 게시글 정보도 같이 읽어온다. 
  - Lazy로 수정하면 그렇지 않다.

 

## JPA 프로그래밍: Query

JPQL (HQL)

- Java Persistence Query Language / Hibernate Query Language
- 데이터베이스 테이블이 아닌, 엔티티 객체 모델 기반으로 쿼리 작성.
- JPA 또는 하이버네이트가 해당 쿼리를 SQL로 변환해서 실행함.
- https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#hql

 ```java
TypedQuery<Post> query = entityManager
    .createQuery("SELECT p FROM Post As p", Post.class);
List<Post> posts = query.getResultList();  
 ```



Criteria

- 타입 세이프 쿼리
  - 오타 방지
- https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#criteria

 ```java
CriteriaBuilder builder = entityManager.getCriteriaBuilder();     
CriteriaQuery<Post> query = builder.createQuery(Post.class);      
Root<Post> root = query.from(Post.class);
query.select(root);
List<Post> posts = entityManager.createQuery(criteria).getResultList();  
 ```



Native Query

- SQL 쿼리 실행하기
- https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#sql

 ```java
List<Post> posts  = entityManager
    .createNativeQuery("SELECT * FROM Post", Post.class).getResultList();  
 ```

 



## 스프링 데이터 JPA 원리

JpaRepository<Entity, Id> 인터페이스

- 스프링 데이터 JPA에서 제공하는 인터페이스
- @Repository가 없어도 빈으로 등록해 줌.

 

@EnableJpaRepositories

- 이 애너테이션을  configuration class에 붙여줘야 하는데, spring boot 덕분에 그럴 필요 없이 동작한다.

- @Import(**JpaRepositoriesRegistrar.class**)
- 핵심은 **ImportBeanDefinitionRegistrar** 인터페이스

 



## 스프링 데이터 

![img](file:///C:/Users/ghdrj/AppData/Local/Temp/msohtmlclip1/01/clip_image004.gif)

 

| 스프링 데이터         | SQL & NoSQL 저장소  지원 프로젝트의 묶음.                    |
| --------------------- | ------------------------------------------------------------ |
| 스프링 데이터 Common  | 여러 저장소 지원 프로젝트의 공통  기능 제공. repository 생성해서 bean으로 등록하는 방법, 쿼리 메소드를 만드는 기본 매커니즘 포함 |
| **스프링 데이터 JPA** | 스프링 데이터 Common이 제공하는  기능에 **JPA 관련 기능 추가**. |

http://projects.spring.io/spring-data/



## 스프링 데이터 Common: Repository 

![img](file:///C:/Users/ghdrj/AppData/Local/Temp/msohtmlclip1/01/clip_image006.gif)

JpaRepository 인터페이스는 PagingAndSortingRepository를 상속받고 있음

CrudRepository 인터페이스에 기능들이 정의되어 있음

- save, saveAll, findById ...





## 스프링 데이터 Common: Repository 인터페이스 정의하기

 Repository 인터페이스로 공개할 메소드를 직접 일일이 정의하고 싶다면(원하는 기능만)

 특정 리포지토리 당

- @RepositoryDefinition

 ```java
@RepositoryDefinition(domainClass  = Comment.class, idClass = Long.class)  
public interface  CommentRepository { 
    Comment save(Comment comment);
    List<Comment> findAll();
}  
 ```



공통 인터페이스 정의

- 예를 들어 위에서 직접 정의한 save, findAll을 다른 인터페이스에도 넣을 것이다

●    @NoRepositoryBean

- @NoRepositoryBean 어노테이션은 이 인터페이스가 Repository 용도로서 사용되는 것이 아닌 단지 Repository의 메서드를 정의하는 인터페이스라는 것을 명시

 ```java
@NoRepositoryBean
public interface MyRepository<T, ID extends Serializable>
    extends Repository<T,  ID> {
    <E extends T> E save(E entity); //T의 하위타입을 저장하는 것도 지원
    List<T> findAll();
}  
 ```

이후에 위 인터페이스를 상속받도록 repository를 작성하면 된다.



## 스프링 데이터 Common: Null 처리하기

스프링 데이터 2.0 부터 자바 8의 Optional 지원.

- Optional<Post> findById(Long id);

 콜렉션은 Null을 리턴하지 않고, 비어있는 콜렉션을 리턴

 

스프링 프레임워크 5.0부터 지원하는 Null 애노테이션 지원.

- @NonNullApi, @NonNull, @Nullable.
- 런타임 체크 지원 함.
- JSR 305 애노테이션을 메타 애노테이션으로 가지고 있음. (IDE 및 빌드 툴 지원)

인텔리J 설정

- Build, Execution, Deployment
  - Compiler
    - Add runtime assertion for notnull-annotated methods and parameters

​    ![img](file:///C:/Users/ghdrj/AppData/Local/Temp/msohtmlclip1/01/clip_image008.gif)



## 스프링 데이터 Common: 쿼리 만들기 개요

스프링 데이터 저장소의 메소드 이름으로 쿼리 만드는 방법

- 메소드 이름을 분석해서 **쿼리 만들기** (CREATE)
- 미리 정의해 둔 **쿼리 찾아 사용하기** (USE_DECLARED_QUERY)
  - @Query("JPQL"), @Query("SQL", nativeQuery=true)  등의 정보
- **미리 정의한 쿼리 찾아보고 없으면 만들기 (CREATE_IF_NOT_FOUND)**
  - 기본 전략
- 어떤 전략을 사용할 지는, @EnableJpaRepository(QueryLookupStrategy. ...) 설정

 

쿼리 만드는 방법

●    리턴타입 {접두어}{도입부}By{프로퍼티 표현식}(조건식)[(And|Or){프로퍼티 표현식}(조건식)]{정렬 조건} (매개변수)

 

| 접두어          | Find,  Get, Query, Count, ...                                |
| --------------- | ------------------------------------------------------------ |
| 도입부          | Distinct, First(N), Top(N)                                   |
| 프로퍼티 표현식 | Person.Address.ZipCode =>  find(Person)ByAddress_ZipCode(...) |
| 조건식          | IgnoreCase, Between, LessThan, GreaterThan, Like,  Contains, ... |
| 정렬 조건       | OrderBy{프로퍼티}Asc\|Desc                                   |
| 리턴 타입       | E, Optional<E>, List<E>, Page<E>,  Slice<E>, Stream<E>       |
| 매개변수        | Pageable,  Sort                                              |

 

쿼리 찾는 방법

- 메소드 이름으로 쿼리를 표현하기 힘든 경우에 사용.
- 저장소 기술에 따라 다름.
- JPA: @Query @NamedQuery

 

## 스프링 데이터 Common: 쿼리 생성

 기본 예제

 ```java
List<Person> findByEmailAddressAndLastname(EmailAddress emailAddress, String lastname);

// distinct
List<Person> findDistinctPeopleByLastnameOrFirstname(String lastname, String firstname);

List<Person> findPeopleDistinctByLastnameOrFirstname(String lastname, String firstname);

// ignoring case
List<Person> findByLastnameIgnoreCaseString lastname);

// ignoring case
List<Person> findByLastnameAndFirstnameAllIgnoreCase(String lastname, String firstname);
 ```



정렬

 ```java
List<Person> findByLastnameOrderByFirstnameAsc(String lastname);

List<Person> findByLastnameOrderByFirstnameDesc(String lastname);
 ```



페이징

 ```java
Page<User> findByLastname(String lastname, Pageable pageable);

Slice<User> findByLastname(String lastname, Pageable pageable);

List<User> findByLastname(String lastname, Sort sort);

List<User> findByLastname(String lastname, Pageable pageable);
 ```



## 스프링 데이터 Common: 비동기 쿼리

```java
@Async Future<User> findByFirstname(String firstname);        

@Async CompletableFuture<User> findOneByFirstname(String firstname); 

@Async ListenableFuture<User> findOneByLastname(String lastname);
```

- 해당 메소드를 스프링 TaskExecutor에 전달해서 별도의 쓰레드에서 실행함.

권장하지 않는 이유

- 테스트 코드 작성이 어려움.
- 코드 복잡도 증가.
- 성능상 이득이 없음.
  - DB 부하는 결국 같고.
  - 메인 쓰레드 대신 백드라운드 쓰레드가 일하는 정도의 차이.
  - 단, 백그라운드로 실행하고 결과를 받을 필요가 없는 작업이라면 @Async를 사용해서 응답 속도를 향상 시킬 수는 있다.

 

## 스프링 데이터 Common: 커스텀 리포지토리

쿼리 메소드(쿼리 생성과 쿼리 찾아쓰기)로 해결이 되지 않는 경우 직접 코딩으로 구현 가능.

- 스프링 데이터 리포지토리 **인터페이스에 기능 추가**.
- 스프링 데이터 리포지토리 **기본 기능 덮어쓰기 가능**.
- 구현 방법
  1. 커스텀 리포지토리 인터페이스 정의 
  2. 인터페이스 구현 클래스 만들기 (기본 접미어는 Impl)
  3. 엔티티 리포지토리에 커스텀 리포지토리 인터페이스 추가

```java
public interface PostCustomRepository {
    List<Post> findMyPost();
}
```

```java
@Repository
@Transactional
public class PostCustomRepositoryImpl implements PostCustomRepository {

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Post> findMyPost() {
        return entityManager
            .createQuery("SELECT p FROM Post AS p", Post.class)
                .getResultList();
    }
}
```

```java
public interface PostRepository 
    extends JpaRepository<Post, Long>, PostCustomRepository {
}
```

기능 추가하기

기본 기능 덮어쓰기

- 이미 존재하는 기능을 내가 직접 구현하면 직접 구현한 게 우선순위가 높다



## 스프링 데이터 Common: 기본 리포지토리 커스터마이징 

모든 리포지토리에 공통적으로 추가하고 싶은 기능이 있거나 덮어쓰고 싶은 기본 기능이 있다면 

1. JpaRepository를 상속 받는 인터페이스 정의
   - @NoRepositoryBean
2. 기본 구현체를 상속 받는 커스텀 구현체 만들기
   - 지켜야 하는 접미어가 없다.
   - SimpleJpaRepository: JpaRepository를 상속 받으면 가져오게 되는 구현체
3. @EnableJpaRepositories에 설정
   - repositoryBaseClass

 

```java
@NoRepositoryBean
public interface MyRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
    // 어떤 entity가 persistent 상태에 들어있는지 확인
    boolean contains(T entity);
}
```

```java
public class SimpleMyRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements MyRepository<T, ID> {

    private EntityManager entityManager;
	
    // 생성자
    public SimpleMyRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public boolean contains(T entity) {
        return entityManager.contains(entity);
    }
}
```

```java
@EnableJpaRepositories(repositoryBaseClass = SimpleMyRepository.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

```java
public interface PostRepository extends MyRepository<Post, Long> {
}
```



## 스프링 데이터 Common: QueryDSL

```java
findByFirstNameIngoreCaseAndLastNameStartsWithIgnoreCase(String firstName, String lastName) 
```

>  이 정도 되면 그냥 한글로 주석을 달아 두는게...

 

여러 쿼리 메소드는 대부분 두 가지 중 하나.

- Optional<T> findOne(**Predicate**): **이런 저런 조건**으로 무언가 하나를 찾는다.
- List<T>|Page<T>|.. findAll(**Predicate**): **이런 저런 조건**으로 무언가 여러개를 찾는다.
- [QuerydslPredicateExecutor 인터페이스](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/querydsl/QuerydslPredicateExecutor.html)

 

QueryDSL

●    http://www.querydsl.com/

●    타입 세이프한 쿼리 만들 수 있게 도와주는 라이브러리

●    JPA, SQL, MongoDB, JDO, Lucene, Collection 지원

●    [QueryDSL JPA 연동 가이드](http://www.querydsl.com/static/querydsl/4.1.3/reference/html_single/#jpa_integration)

 

스프링 데이터 JPA + QueryDSL

●    인터페이스: QuerydslPredicateExecutor<T>

●    구현체: QuerydslPredicateExecutor<T>

 

연동 방법

의존성 추가

 ```xml
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
</dependency>
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
</dependency>
 ```

```xml
<plugin>
    <groupId>com.mysema.maven</groupId>
    <artifactId>apt-maven-plugin</artifactId>
    <version>1.1.3</version>
    <executions>
        <execution>
            <goals>
                <goal>process</goal>
            </goals>
            <configuration>
                <outputDirectory>target/generated-sources/java</outputDirectory>
                <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
            </configuration>
        </execution>
    </executions>
</plugin>
```

이후 Maven compile

target/generated-sources/java 확인

![image-20210622155650093](C:\Users\ghdrj\AppData\Roaming\Typora\typora-user-images\image-20210622155650093.png)

```java
public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {
}
```



 ```java
@DataJpaTest
public class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void crud() {
        Account account1 = new Account();
        account1.setFirstName("Geonsu");
        account1.setLastName("hongggggggg");
        accountRepository.save(account1);

        QAccount account = QAccount.account;
        Predicate predicate = account
                .firstName.containsIgnoreCase("geonsu")
                .and(account.lastName.startsWith("hong"));

        Optional<Account> one = accountRepository.findOne(predicate);
        assertThat(one).isNotEmpty();
    }
}
 ```



## 스프링 데이터 Common: Web: DomainClassConverter

스프링 Converter

●    https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/convert/converter/Converter.html



```java
@RestController
public class PostController {
    private final PostRepository postRepository;
    
    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable Long id) {
        Optional<Post> byId = postRepository.findById(id);
        Post post = byId.get();
        return post.getTitle();
    }
}
```



 ```java
@RestController
public class PostController {
    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable("id") Post post) { //Domain Class Converter 동작
        return post.getTitle();
    }
}
 ```



## 스프링 데이터 Common: Web: Pageable과 Sort 매개변수

스프링 MVC HandlerMethodArgumentResolver

●    스프링 MVC 핸들러 메소드의 매개변수로 받을 수 있는 객체를 확장하고 싶을 때 사용하는 인터페이스

●    https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/method/support/HandlerMethodArgumentResolver.html

 

페이징과 정렬 관련 매개변수

- page: 0부터 시작.
- size: 기본값 20.
- sort: property,property(,ASC|DESC)
- 예) sort=created,desc&sort=title (asc가 기본값)

 

## 스프링 데이터 Common: Web: HATEOAS

Page를 PagedResource로 변환하기

- HATEOAS 의존성 추가 (starter-hateoas)
- 핸들러 매개변수로 PagedResourcesAssembler

 

리소스로 변환하기 전 

```java
@GetMapping("/posts")
public Page<Post> getPosts(Pageable pageable) {
    return postRepository.findAll(pageable);
}
```

response body

```json
  {      "content":[   ...       {         "id":111,        "title":"jpa",        "created":null     }    ],     "pageable":{        "sort":{         "sorted":true,        "unsorted":false     },       "offset":20,       "pageSize":10,       "pageNumber":2,       "unpaged":false,       "paged":true    },     "totalElements":200,     "totalPages":20,     "last":false,     "size":10,     "number":2,     "first":false,     "numberOfElements":10,     "sort":{        "sorted":true,       "unsorted":false    }  }  
```



리소스로 변환한 뒤

```java
@GetMapping("/posts")
public PagedModel<EntityModel<Post>> getPosts(Pageable pageable, 							PagedResourcesAssembler<Post> assembler) {
    return assembler.toModel(postRepository.findAll(pageable));
}
```

response body

 ```json
  {      "_embedded":{        "postList":[         {           "id":140,          "title":"jpa",          "created":null        },  ...        {           "id":109,          "title":"jpa",          "created":null        }     ]    },     "_links":{        "first":{         "href":"http://localhost/posts?page=0&size=10&sort=created,desc&sort=title,asc"     },     "prev":{         "href":"http://localhost/posts?page=1&size=10&sort=created,desc&sort=title,asc"     },     "self":{         "href":"http://localhost/posts?page=2&size=10&sort=created,desc&sort=title,asc"     },     "next":{         "href":"http://localhost/posts?page=3&size=10&sort=created,desc&sort=title,asc"     },     "last":{         "href":"http://localhost/posts?page=19&size=10&sort=created,desc&sort=title,asc"     }    },    "page":{      "size":10,     "totalElements":200,     "totalPages":20,     "number":2    }  }  
 ```





## 스프링 데이터 JPA: JPA Repository

@EnableJpaRepositories

- 스프링 부트 사용할 때는 사용하지 않아도 자동 설정 됨.
- 스프링 부트 사용하지 않을 때는 @Configuration과 같이 사용.

 

@Repository 애노테이션을 붙여야 하나 말아야 하나?

- 안 붙여도 된다.
- 또 붙인다고 별일이 생기는건 아니지만 중복일 뿐

 

## 스프링 데이터 JPA: 엔티티 저장하기

JpaRepository의 save()는 단순히 새 엔티티를 추가하는 메소드가 아니다.

- Transient 상태의 객체라면 EntityManager.persist() 동작
- Detached 상태의 객체라면 EntityManager.merge() 동작

 

Transient인지 Detached 인지 어떻게 판단 하는가?

- 엔티티의 @Id 프로퍼티를 찾는다. 해당 프로퍼티가 null이면 Transient 상태로 판단하고 id가 null이 아니면 Detached 상태로 판단한다.

EntityManager.persist()

- https://docs.oracle.com/javaee/6/api/javax/persistence/EntityManager.html#persist(java.lang.Object)
- Persist() 메소드에 넘긴 그 엔티티 객체를 Persistent 상태로 변경

![img](file:///C:/Users/ghdrj/AppData/Local/Temp/msohtmlclip1/01/clip_image010.gif)

EntityManager.merge()

- https://docs.oracle.com/javaee/6/api/javax/persistence/EntityManager.html#merge(java.lang.Object)
- Merge() 메소드에 넘긴 그 엔티티의 복사본을 만들고, 그 복사본을 다시 Persistent 상태로 변경하고 그 복사본을 반환

![img](file:///C:/Users/ghdrj/AppData/Local/Temp/msohtmlclip1/01/clip_image012.gif)



```java
@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void crud() {
        Post post = new Post();
        post.setTitle("jpa");
        Post savedPost = postRepository.save(post);// persist (transient -> persistent 상태)
        //반환되는 객체를 사용하는 게 best practice.

        assertThat(entityManager.contains(post)).isTrue();
        assertThat(entityManager.contains(savedPost)).isTrue();
        assertThat(savedPost == post).isTrue();

        Post postUpdate = new Post();
        postUpdate.setId(post.getId()); //id가 있다
        postUpdate.setTitle("hibernate");
        Post updatedPost = postRepository.save(postUpdate);// merge 즉 UPDATE 쿼리
        // postUpdate의 복사본인 updatedPost를 영속화 하고, 복사본(updatedPost) 반환

        assertThat(entityManager.contains(updatedPost)).isTrue(); //영속화된 복사본
        assertThat(entityManager.contains(postUpdate)).isFalse(); //save한 인스턴스
        assertThat(updatedPost == postUpdate).isFalse();
    }
}
```



## 스프링 데이터 JPA: 쿼리 메소드

쿼리 생성하기: Spring Data JPA에서 지원하는 키워드

- https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
- And, Or
- Is, Equals
- LessThan, LessThanEqual, GreaterThan, GreaterThanEqual
- After, Before
- IsNull, IsNotNull, NotNull
- Like, NotLike
- StartingWith, EndingWith, Containing
- OrderBy
- Not, In, NotIn
- True, False
- IgnoreCase

 

쿼리 찾아쓰기

- 엔티티에 정의한 쿼리 찾아 사용하기 JPA Named 쿼리
  - @NamedQuery

```java
@NamedQuery(name = "Post.findByTitle2", query = "SELECT p FROM Post AS p WHERE p.title = ?1")
@Entity
public class Post {
    @Id @GeneratedValue
    private Long id;
    
    private String title;
    // ...
}
```

```java
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTitle2(String title);
}
```



- 리포지토리 메소드에 정의한 쿼리 사용하기
  - @Query

```java
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p from Post AS p WHERE p.title =?1")
    List<Post> findByTitle2(String title);
}
```

```java
@Entity
public class Post {
    @Id @GeneratedValue
    private Long id;
    
    private String title;
    // ...
}
```



 

## 스프링 데이터 JPA: 쿼리 메소드 Sort

이전과 마찬가지로 Pageable이나 Sort를 매개변수로 사용할 수 있는데, @Query와 같이 사용할 때 제약 사항이 있다.

 Order by 절에서 함수를 호출하는 경우에는 Sort를 사용하지 못한다. 그 경우에는 JpaSort.unsafe()를 사용 해야 한다.

- Sort는 그 안에서 사용한 **프로퍼티** 또는 **alias**가 엔티티에 없는 경우에는 예외가 발생
- JpaSort.unsafe()를 사용하면 함수 호출 가능
  - JpaSort.unsafe(“LENGTH(firstname)”);

 ```java
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTitleStartsWith(String title, Sort sort);
}
 ```

```java
@Test
public void findByTitleStartsWith() {
 // ...
    List<Post> all = postRepository
        .findByTitleStartsWith("Spring", Sort.by(Sort.Direction.DESC, "title"));

    all = postRepository.findByTitle("Spring", JpaSort.unsafe(Sort.Direction.DESC, "LENGTH(title)"));
}
```



## 스프링 데이터 JPA: Named Parameter, SpEL

 

Named Parameter

- @Query에서 참조하는 매개변수를 ?1, ?2 이렇게 채번으로 참조하는게 아니라 이름으로 :title 이렇게 참조하는 방법

 ```java
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post AS p WHERE p.title = :title")
    List<Post> findByTitle(@Param("title") String keyword, Sort sort);
}
 ```

 

SpEL

- 스프링 표현 언어
- https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#expressions
- @Query에서 엔티티 이름을 #{#entityName} 으로 표현할 수 있다.

 ```java
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p from #{#entityName} AS p WHERE p.title = :title")
    List<Post> findByTitle(@Param("title") String keyword, Sort sort);
} 
 ```



## 스프링 데이터 JPA: Update 쿼리 메소드

Update 쿼리 직접 정의하기

- @Modifying @Query
- 비추천

 ```java
public interface PostRepository extends JpaRepository<Post, Long> {
 	@Modifying(clearAutomatically = true)
    @Query("UPDATE Post p SET p.title = ?1 WHERE p.id = ?2")
    int updateTitle(String title, Long id);
}
 ```



```java
@Test
public void updateTitle() {
    Post spring = savePost("Spring"); //persistent
    postRepository.updateTitle("hibernate", spring.getId()); //DB 갱신 -> 캐시를 비우면 해결되는
    
    Optional<Post> byId = postRepository.findById(spring.getId()); //SELECT
    assertThat(byId.get().getTitle()).isEqualTo("hibernate"); // hibernamte
}

private Post savePost(String s) {
    Post post = new Post();
    post.setTitle(s);
    return postRepository.save(post);
}
```

> @Modifying 애너테이션에 옵션을 주지 않으면,
> UPDATE 쿼리는 날아가지만, Post spring 객체가 여전히 persistent 상태로 캐시가 되어있기 때문에,
> 아래의 조회에서 title이 "Spring"으로 조회된다. 캐시가 갱신되려면 SELECT 쿼리의 발생이 필요하다.
>
> @Modifying 애너테이션에 옵션을 통해 persistent context 를 clear 해주면, Post spring 객체가 persistent 상태가 아니게 되기 때문에
> findById 할 때 다시 DB에서 select 쿼리가 발생하고, 갱신된 값을 읽어올 수 있다.
>
> 권장하는 방법은 아니다. 그렇다면 권장하는 방법은?

 ```java
@Test
public void updateTitle2() {
    Post spring = savePost("Spring");
    spring.setTitle("hibernate");
    
    // transaction 외부의 변경까지 고려해야 하므로 DB와 sync
    List<Post> all = postRepository.findAll();
    assertThat(all.get(0).getTitle()).isEqualTo("hibernate");
	
     // persistent context가 알고있는 id에 대한 질의이기 때문에 캐시로 처리
    Optional<Post> byId = postRepository.findById(spring.getId());
    assertThat(byId.get().getTitle()).isEqualTo("hibernate");
}
 ```



## 스프링 데이터 JPA: Projection

 엔티티의 일부 데이터만 가져오기.

 

**인터페이스 기반** 프로젝션

- Nested 프로젝션 가능.
- **Closed 프로젝션**
  - 쿼리를 최적화 할 수 있다. 가져오려는 애트리뷰트가 뭔지 알고 있으니까.
    - SELECT 실행 시, 필요한 칼럼만 조회한다.

```java
public interface CommentSummary {
    String getComment();
    int getUp();
    int getDown();
}
```

```java
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<CommentSummary> findByPost_Id(Long id); 
    // type에 projection의 대상이 되는 컬럼의 getter를 담은 interface를 넣어준다.
}
```

```java
@Test
public void getComment() {
    commentRepository.findByPost_Id(1l);
}
```

- Open 프로젝션
  - @Value(SpEL)을 사용해서 연산을 할 수 있다. 스프링 빈의 메소드도 호출 가능.
  - 쿼리 최적화를 할 수 없다. SpEL을 엔티티 대상으로 사용하기 때문에.
    - SELECT 실행 시, Entity의 모든 칼럼을 조회한다.

 

```java
public interface CommentSummary {
    @Value("#{target.up + ' ' + target.down}")
    String getVotes();
}
```



클래스 기반 프로젝션

- SELECT 실행 시, 필요한 칼럼만 조회한다.

- 롬복으로 코드 줄일 수 있다.

```java
public class CommentSummary {
    private String comment;
    private int up;
    private int down;

    public CommentSummary(String comment, int up, int down) {
        this.comment = comment;
        this.up = up;
        this.down = down;
    }
	// getter...
}
```



다이나믹 프로젝션

- 프로젝션 용 메소드 하나만 정의하고 실제 프로젝션 타입은 타입 인자로 전달하기.

```java
public interface CommentRepository extends JpaRepository<Comment, Long> {
    <T> List<T> findByPost_Id(Long id, Class<T> type); //generic 활용
}
```

```java
@Test
public void getComment() {
    commentRepository.findByPost_Id(savedPost.getId(), CommentOnly.class)
}
```

 

## 스프링 데이터 JPA: Specifications

에릭 에반스의 책 DDD에서 언급하는 Specification 개념을 차용 한 것으로 QueryDSL의 Predicate와 비슷하다.

 

설정 하는 방법

- https://docs.jboss.org/hibernate/stable/jpamodelgen/reference/en-US/html_single/
- 의존성 설정
- 플러그인 설정
- IDE에 애노테이션 처리기 설정
- 프로젝트 빌드 후 소스 생성 확인
- 코딩 시작

```xml
 <dependency>
   <groupId>org.hibernate</groupId>
   <artifactId>hibernate-jpamodelgen</artifactId>
 </dependency>
```

```xml
<plugin>
    <groupId>org.bsc.maven</groupId>
    <artifactId>maven-processor-plugin</artifactId>
    <version>2.0.5</version>
    <executions>
        <execution>
            <id>process</id>
            <goals>
                <goal>process</goal>
            </goals>
            <phase>generate-sources</phase>
            <configuration>
                <processors>
               <processor>org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor</processor>
                </processors>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-jpamodelgen</artifactId>
            <version>${hibernate.version}</version>
        </dependency>
    </dependencies>
</plugin>
```



![image-20210623162836747](C:\Users\ghdrj\AppData\Roaming\Typora\typora-user-images\image-20210623162836747.png)

>  org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor

```java
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

}
```

```java
public class CommentSpecs {
    
    public static Specification<Comment> isBest() {
        return new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> root,
                                        CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder builder) {
                return builder.isTrue(root.get(Comment_.best)); 
                // root 는 comment
            }
        };
    }

    public static Specification<Comment> isGood() {
        return new Specification<Comment>() {
            @Override
            public Predicate toPredicate(Root<Comment> root,
                                        CriteriaQuery<?> criteriaQuery,
                                         CriteriaBuilder builder) {
                return builder
                    .greaterThanOrEqualTo(root.get(Comment_.up), 10);
            }
        };
    }
}
```

```java
@Test
public void specs() {
    Page<Comment> page = commentRepository
        .findAll(CommentSpecs.isBest()
                 .or(CommentSpecs.isGood()), PageRequest.of(0, 10));

}
```

> 위와 같이, 클라이언트 코드가 간단해진다는 장점이 있다.



## 스프링 데이터 JPA: 트랜잭션

 스프링 데이터 JPA가 제공하는 Repository의 모든 메소드에는 기본적으로 @Transaction이 적용되어 있다.

 

스프링 @Transactional

- 클래스, 인터페이스, 메소드에 사용할 수 있으며, 메소드에 가장 가까운 애노테이션이 우선 순위가 높다.

●    https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/annotation/Transactional.html 

JPA 구현체로 Hibernate를 사용할 때 트랜잭션을 readOnly를 사용하면 좋은 점

- Flush 모드를 NEVER로 설정하여, Dirty checking을 하지 않도록 한다.
  - 읽기만 하는 메소드이기 때문에, DB 동기화를 하지 않고(Flush 하지 않고)
  - 마찬가지로 변화 감지 또한 할 필요 없다.

```java
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
        @Transactional(readOnly = true)
        <T> List<T> findByPost_Id(Long id, Class<T> type); 
}
```



## 스프링 데이터 JPA: Auditing

 

스프링 데이터 JPA의 Auditing

```java
    @CreatedDate
    private Date created;

    @CreatedBy
    @ManyToOne
    private Account createdBy;

    @LastModifiedDate
    private Date updated;

    @LastModifiedBy
    @ManyToOne
    private Account updatedBy;
```



엔티티의 변경 시점에 언제, 누가 변경했는지에 대한 정보를 기록하는 기능.

 

아쉽지만 이 기능은 스프링 부트가 자동 설정 해주지 않는다.

1. 메인 애플리케이션 위에 @EnableJpaAuditing 추가
2. 엔티티 클래스 위에     @EntityListeners(AuditingEntityListener.class) 추가
3. AuditorAware 구현체 만들기
4. @EnableJpaAuditing에 AuditorAware 빈 이름 설정하기.

```java
@Service
public class AccountAuditAware implements AuditorAware<Account> {
    @Override
    public Optional<Account> getCurrentAuditor() {
        // spring security 에서 사용자 정보를 가져오는 logic이 들어갈 부분
        System.out.println("looking for current user");
        return Optional.empty();
    }
}
```

 ```java
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "accountAuditAware") // bean 이름
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
 ```



JPA의 라이프 사이클 이벤트

어떠한 Entity에 변화가 일어날 때, callback을 실행할 수 있음. Entity에 정의할 수 있다.

- https://docs.jboss.org/hibernate/orm/4.0/hem/en-US/html/listeners.html
- @PrePersist
- @PostPersist
- @PreUpdate
- ...

> auditing 관련 설정이 없다.

```java
@Entity
public class Comment {
    @Id @GeneratedValue
    private Long id;
    private String comment;
    private Date created;
    
    // ...

    @PrePersist
    public void prePersist() {
        this.created = new Date();
        // ...
    }
}
```

 
